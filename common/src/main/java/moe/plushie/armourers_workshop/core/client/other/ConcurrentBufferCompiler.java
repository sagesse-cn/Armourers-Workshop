package moe.plushie.armourers_workshop.core.client.other;

import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.client.IRenderedBuffer;
import moe.plushie.armourers_workshop.compatibility.client.AbstractVertexArrayObject;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.buffer.BufferBuilder;
import moe.plushie.armourers_workshop.core.client.buffer.OutlineBufferBuilder;
import moe.plushie.armourers_workshop.core.client.texture.SmartTexture;
import moe.plushie.armourers_workshop.core.data.cache.CacheQueue;
import moe.plushie.armourers_workshop.core.data.cache.ObjectPool;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Executors;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class ConcurrentBufferCompiler {

    private static final ExecutorService QUEUE = Executors.newFixedThreadPool(ModConfig.Client.vertexCompileThreadCount, "AW-SKIN-VB");
    private static final CacheQueue<Object, Group> CACHING = new CacheQueue<>(Duration.ofSeconds(30), it -> RenderSystem.recordRenderCall(it::release));
    private static final VertexIndexObject INDEXER = new VertexIndexObject(4, 6, (builder, index) -> {
        builder.accept(index);
        builder.accept(index + 1);
        builder.accept(index + 2);
        builder.accept(index + 2);
        builder.accept(index + 3);
        builder.accept(index);
    });

    private ArrayList<Group> pendingTasks;

    public static void clearAllCache() {
        CACHING.clearAll();
    }

    @Nullable
    public Group compile(BakedSkinPart part, BakedSkin skin, SkinPaintScheme scheme, boolean isOutline) {
        var options = createOptions(isOutline);
        var key = Key.of(part.getId(), options, part.requirements(scheme));
        var group = CACHING.get(key);
        if (group != null) {
            if (group.isCompiled()) {
                return group;
            }
            return null; // wait compile
        }
        group = new Group(part, skin, options, scheme);
        CACHING.put(key.copy(), group);
        startBatch(group);
        return null; // wait compile
    }

    private synchronized void startBatch(Group value) {
        var tasks = pendingTasks;
        if (tasks == null) {
            tasks = new ArrayList<>();
            pendingTasks = tasks;
            QUEUE.execute(this::compile);
        }
        tasks.add(value);
    }

    private synchronized ArrayList<Group> endBatch() {
        var tasks = pendingTasks;
        pendingTasks = null;
        return tasks;
    }

    private void compile() {
        var pendingTasks = endBatch();
        if (pendingTasks == null || pendingTasks.isEmpty()) {
            return;
        }
        //long startTime = System.nanoTime();
        var poseStack1 = new OpenPoseStack();
        var buildingTasks = new ArrayList<Pass>();
        for (var task : pendingTasks) {
            var part = task.part;
            var scheme = task.scheme;
            var usingTypes = new HashSet<RenderType>();
            var mergedTasks = new ArrayList<Pass>();
            part.getQuads().forEach((renderType, quads) -> {
                var builder = createBufferBuilder(renderType, quads.size(), task);
                quads.forEach((transform, faces) -> {
                    poseStack1.pushPose();
                    transform.apply(poseStack1);
                    faces.forEach(face -> face.render(part, scheme, 0xf000f0, OverlayTexture.NO_OVERLAY, poseStack1, builder));
                    poseStack1.popPose();
                });
                var renderedBuffer = builder.end();
                var compiledTask = new Pass(builder.getRenderType(), renderedBuffer, part.getRenderPolygonOffset(), part.getType(), task.isOutline());
                usingTypes.add(renderType);
                mergedTasks.add(compiledTask);
                buildingTasks.add(compiledTask);
            });
            task.mergedTasks = mergedTasks;
            task.usingTypes = Collections.compactMap(usingTypes, SmartTexture::of);
        }
        link(pendingTasks, buildingTasks);
        //long totalTime = System.nanoTime() - startTime;
        //ModLog.debug("compile tasks {}, times: {}ms", pendingTasks.size(), totalTime / 1e6f);
    }

    private void link(ArrayList<Group> cachedTasks, ArrayList<Pass> buildingTasks) {
        var indexer = INDEXER;
        var totalRenderedBytes = 0;
        var byteBuffers = new ArrayList<ByteBuffer>();

        for (var compiledTask : buildingTasks) {
            var renderedBuffer = compiledTask.bufferBuilder;
            var format = renderedBuffer.format();
            var byteBuffer = renderedBuffer.vertexBuffer().duplicate();
            compiledTask.vertexCount = renderedBuffer.vertexCount();
            compiledTask.vertexOffset = totalRenderedBytes;
            compiledTask.bufferBuilder = null;
            compiledTask.format = format;
            byteBuffers.add(byteBuffer);
            totalRenderedBytes += byteBuffer.remaining();
            renderedBuffer.release();
            // make sure the index buffer is of sufficient size.
            indexer.ensureCapacity(compiledTask.vertexCount * 2);
        }

        var mergedByteBuffer = ByteBuffer.allocateDirect(totalRenderedBytes);
        for (var byteBuffer : byteBuffers) {
            mergedByteBuffer.put(byteBuffer);
        }
        mergedByteBuffer.rewind();

        // upload only be called in the render thread !!!
        RenderSystem.safeCall(() -> upload(mergedByteBuffer, cachedTasks));
    }

    private void upload(ByteBuffer byteBuffer, ArrayList<Group> cachedTasks) {
        var vertexBuffer = new VertexBufferObject();
        vertexBuffer.upload(byteBuffer);
        for (var cachedTask : cachedTasks) {
            cachedTask.bufferObject = vertexBuffer;
            cachedTask.retain();
        }
        vertexBuffer.release();
    }

    private int createOptions(boolean isOutline) {
        int options = 0;
        if (isOutline) {
            options |= 0x01;
        }
        return options;
    }

    private BufferBuilder createBufferBuilder(RenderType renderType, int total, Group task) {
        // outline requires a special builder.
        if (task.isOutline() && renderType.outline().isPresent()) {
            return new OutlineBufferBuilder(renderType.outline().get(), total);
        }
        return new BufferBuilder(renderType, total);
    }

    public static class Group extends ReferenceCounted {

        private final BakedSkinPart part;
        private final BakedSkin skin;

        private final int options;
        private final SkinPaintScheme scheme;

        private ArrayList<Pass> mergedTasks;
        private ArrayList<ReferenceCounted> usingTypes;

        private VertexBufferObject bufferObject;

        private boolean isComplied = false;

        public Group(BakedSkinPart part, BakedSkin skin, int options, SkinPaintScheme scheme) {
            this.skin = skin;
            this.part = part;
            this.options = options;
            this.scheme = scheme;
        }

        @Override
        protected void init() {
            RenderSystem.assertOnRenderThread();
            if (mergedTasks == null || usingTypes == null) {
                return; // is released or not init.
            }
            this.bufferObject.retain();
            this.mergedTasks.forEach(it -> it.upload(bufferObject));
            this.usingTypes.forEach(ReferenceCounted::retain);
            this.isComplied = true;
        }

        @Override
        protected void dispose() {
            RenderSystem.assertOnRenderThread();
            if (this.bufferObject == null || this.mergedTasks == null || usingTypes == null) {
                return; // is release
            }
            this.isComplied = false;
            this.usingTypes.forEach(ReferenceCounted::release);
            this.mergedTasks.forEach(Pass::close);
            this.bufferObject.release();
        }

        public List<Pass> getPasses() {
            return mergedTasks;
        }

        public boolean isEmpty() {
            return mergedTasks == null || mergedTasks.isEmpty();
        }

        public boolean isCompiled() {
            return isComplied;
        }

        public boolean isOutline() {
            return (options & 0x01) != 0;
        }
    }

    public static class Pass {

        final boolean isGrowing;
        final boolean isTranslucent;
        final boolean isOutline;
        final boolean isUsingIndex;

        final float polygonOffset;
        final SkinPartType partType;
        final RenderType renderType;

        int vertexCount;
        int vertexOffset;

        IRenderedBuffer bufferBuilder;
        VertexFormat format;

        VertexArrayObject arrayObject;
        VertexBufferObject bufferObject;
        VertexIndexObject indexObject;

        boolean isCompiled = false;

        Pass(RenderType renderType, IRenderedBuffer bufferBuilder, float polygonOffset, SkinPartType partType, boolean isOutline) {
            this.partType = partType;
            this.renderType = renderType;
            this.bufferBuilder = bufferBuilder;
            this.polygonOffset = polygonOffset;
            this.isGrowing = SkinRenderType.isGrowing(renderType);
            this.isTranslucent = SkinRenderType.isTranslucent(renderType);
            this.isOutline = isOutline;
            this.isUsingIndex = SkinRenderType.isUsingIndex(renderType);
        }

        public void upload(VertexBufferObject bufferObject) {
            this.indexObject = isUsingIndex ? INDEXER : null;
            this.bufferObject = bufferObject;
            this.arrayObject = AbstractVertexArrayObject.create(format, vertexOffset, bufferObject, indexObject);
            this.isCompiled = true;
        }

        public void close() {
            this.isCompiled = false;
            this.arrayObject.close();
            this.bufferObject = null;
            this.indexObject = null;
            this.arrayObject = null;
        }
    }

    public static class Key {

        protected static final ObjectPool<Key> POOL = ObjectPool.create(Key::new);

        private int p1;
        private int p2;
        private Object p3;
        private int hash;

        private Key set(int hash, int p1, int p2, Object p3) {
            this.hash = hash;
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            return this;
        }

        public static Key of(int p1, int p2, Object p3) {
            int hash = p1;
            hash = 31 * hash + p2;
            hash = 31 * hash + (p3 == null ? 0 : p3.hashCode());
            return POOL.get().set(hash, p1, p2, p3);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key that)) return false;
            return p1 == that.p1 && p2 == that.p2 && Objects.equals(p3, that.p3);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        public Key copy() {
            return new Key().set(hash, p1, p2, p3);
        }
    }
}
