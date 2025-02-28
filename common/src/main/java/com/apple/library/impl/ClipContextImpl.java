package com.apple.library.impl;

import com.apple.library.coregraphics.CGRect;
import com.google.common.base.Objects;
import com.mojang.blaze3d.platform.Window;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;

public class ClipContextImpl {

    private static final CGRect EMPTY = new CGRect(-4000000, -4000000, 8000000, 8000000);
    private static final ClipContextImpl INSTANCE = new ClipContextImpl();

    private final Stack<CGRect> clipBoxes = new Stack<>();
    private final ScissorRenderer scissorRenderer = new ScissorRenderer();
    private final OffscreenRenderer offscreenRenderer = new OffscreenRenderer();

    public static ClipContextImpl getInstance() {
        return INSTANCE;
    }

    public void addClip(Rectangle rect) {
        var newClipBox = rect.rect;
        if (!clipBoxes.isEmpty()) {
            newClipBox = clipBoxes.peek().intersection(newClipBox);
        }
        clipBoxes.push(newClipBox);
        scissorRenderer.render(newClipBox);
        offscreenRenderer.push(rect.offscreenPasses());
    }

    public void removeClip() {
        offscreenRenderer.pop();
        if (!clipBoxes.isEmpty()) {
            clipBoxes.pop();
            scissorRenderer.render(lastClipBox());
        }
    }

    public CGRect boundingBoxOfClipPath() {
        if (scissorRenderer.clipBox != null) {
            return scissorRenderer.clipBox;
        }
        return EMPTY;
    }

    private CGRect lastClipBox() {
        if (!clipBoxes.isEmpty()) {
            return clipBoxes.peek();
        }
        return null;
    }

    public static class Rectangle {

        protected final CGRect rect;

        public Rectangle(CGRect rect) {
            this.rect = rect;
        }

        @Nullable
        public List<CGRect> offscreenPasses() {
            return null;
        }
    }

    public static class RoundRectangle extends Rectangle {

        protected final float cornerRadius;

        public RoundRectangle(CGRect rect, float cornerRadius) {
            super(rect);
            this.cornerRadius = cornerRadius;
        }

        @Override
        public List<CGRect> offscreenPasses() {
            var passes = new ArrayList<CGRect>();
            passes.add(new CGRect(rect.minX(), rect.minY(), cornerRadius, cornerRadius));
            passes.add(new CGRect(rect.maxX(), rect.minY(), -cornerRadius, cornerRadius));
            passes.add(new CGRect(rect.maxX(), rect.maxY(), -cornerRadius, -cornerRadius));
            passes.add(new CGRect(rect.minX(), rect.maxY(), cornerRadius, -cornerRadius));
            return passes;
        }
    }

    public static class ScissorRenderer {

        private CGRect clipBox;
        private final Window window = Minecraft.getInstance().getWindow();

        public void render(@Nullable CGRect rect) {
            if (Objects.equal(clipBox, rect)) {
                return;
            }
            clipBox = rect;
            if (rect != null) {
                double scale = window.getGuiScale();
                int x = (int) (rect.x() * scale);
                int y = (int) (window.getHeight() - rect.maxY() * scale);
                int width = (int) (rect.width() * scale);
                int height = (int) (rect.height() * scale);
                RenderSystem.enableScissor(x, y, width, height);
            } else {
                RenderSystem.disableScissor();
            }
        }
    }

    public static class OffscreenRenderer {

        private final Window window = Minecraft.getInstance().getWindow();
        private final Stack<Buffer> allBuffers = new Stack<>();
        private final Stack<Buffer> reusableBuffers = new Stack<>();
        private final Stack<Group> allGroups = new Stack<>();

        public void push(@Nullable List<CGRect> rects) {
            var group = appendList(rects);
            if (group == null) {
                return;
            }
            GL30.glDisable(GL30.GL_SCISSOR_TEST);
            int mainTargetId = GL30.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
            group.forEach((buffer, passes) -> {
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, buffer.frameBufferId);
                buffer.init();
                passes.forEach(it -> buffer.blit(it.source, it.destination));
            });
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, mainTargetId);
            GL30.glEnable(GL30.GL_SCISSOR_TEST);
        }

        public void pop() {
            var group = removeLast();
            if (group == null) {
                return;
            }
            int readTargetId = GL30.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
            int drawTargetId = GL30.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);

//            var buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            IBufferSource buffers = null;
            int mainTextureId = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_READ_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);

            GL30.glDisable(GL30.GL_SCISSOR_TEST);
            GL30.glEnable(GL30.GL_STENCIL_TEST);
            GL30.glEnable(GL30.GL_MULTISAMPLE);

            group.forEach((buffer, passes) -> {
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, buffer.frameBufferId);

                GL30.glStencilMask(0xFF);
                GL30.glStencilFunc(GL30.GL_ALWAYS, 1, 0xFF);
                GL30.glStencilOp(GL30.GL_REPLACE, GL30.GL_REPLACE, GL30.GL_REPLACE);

                var maskBuilder = buffers.getBuffer(SkinRenderType.BLIT_MASK);
                passes.forEach(it -> buffer.mask(it.destination, maskBuilder));
                buffers.endBatch();

                GL30.glStencilFunc(GL30.GL_EQUAL, 1, 0xFF);
                GL30.glStencilOp(GL30.GL_KEEP, GL30.GL_KEEP, GL30.GL_KEEP);

                RenderSystem.setShaderTexture(0, mainTextureId);
                var blitBuilder = buffers.getBuffer(SkinRenderType.BLIT_IMAGE);
                passes.forEach(it -> buffer.blit(it.source, it.destination, blitBuilder));
                buffers.endBatch();

                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, drawTargetId);
                GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, buffer.frameBufferId);

                passes.forEach(it -> buffer.blit(it.destination, it.source));

                GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, readTargetId);
            });
            group.clear();

            GL30.glDisable(GL30.GL_MULTISAMPLE);
            GL30.glDisable(GL30.GL_STENCIL_TEST);
            GL30.glEnable(GL30.GL_SCISSOR_TEST);
        }


        @Nullable
        private Group appendList(@Nullable List<CGRect> rects) {
            Group group = null;
            if (rects != null && !rects.isEmpty()) {
                group = new Group();
                rects.forEach(group::add);
            }
            allGroups.add(group);
            return group;
        }

        @Nullable
        private Group removeLast() {
            if (!allGroups.isEmpty()) {
                return allGroups.pop();
            }
            return null;
        }


        private Buffer getBuffer() {
            if (allBuffers.isEmpty()) {
                return createBuffer();
            }
            return allBuffers.peek();
        }

        private void releaseBuffer(Buffer buffer) {
            allBuffers.remove(buffer);
            if (buffer.frameBufferId < 0) {
                return;
            }
            reusableBuffers.push(buffer);
        }

        private Buffer createBuffer() {
            int width = window.getWidth();
            int height = window.getHeight();
            while (!reusableBuffers.isEmpty()) {
                Buffer buffer = reusableBuffers.pop();
                if (buffer.frameWidth != width || height != buffer.frameHeight) {
                    buffer.release();
                    continue;
                }
                buffer.isDirty = true;
                allBuffers.push(buffer);
                return buffer;
            }
            float scale = (float) window.getGuiScale();
            Buffer buffer = new Buffer(width, height, scale);
            allBuffers.push(buffer);
            return buffer;
        }

        public static class Pass {

            public final CGRect source;
            public final CGRect destination;

            public final int lineId;

            public Pass(int lineNo, CGRect source, CGRect destination) {
                this.lineId = lineNo;
                this.source = source;
                this.destination = destination;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Pass that)) return false;
                return destination.equals(that.destination);
            }

            @Override
            public int hashCode() {
                return destination.hashCode();
            }
        }

        public class Group {

            private Buffer defaultBuffer;
            private ArrayList<Pass> defaultPasses;
            private LinkedHashMap<Buffer, ArrayList<Pass>> otherPasses;

            public void add(CGRect rect) {
                Buffer buffer = getBuffer();
                Pass pass = buffer.add(rect);
                while (pass == null) {
                    buffer = createBuffer();
                    pass = buffer.add(rect);
                }
                getPassQueue(buffer).add(pass);
            }

            public void clear() {
                if (defaultBuffer != null) {
                    clearPassQueue(defaultBuffer, defaultPasses);
                    defaultPasses = null;
                    defaultBuffer = null;
                }
                if (otherPasses != null) {
                    otherPasses.forEach(this::clearPassQueue);
                    otherPasses = null;
                }
            }

            public void forEach(BiConsumer<Buffer, List<Pass>> consumer) {
                if (defaultBuffer != null) {
                    consumer.accept(defaultBuffer, defaultPasses);
                }
                if (otherPasses != null) {
                    otherPasses.forEach(consumer);
                }
            }

            private void clearPassQueue(Buffer buffer, ArrayList<Pass> passes) {
                passes.forEach(buffer::remove);
                if (buffer.isEmpty()) {
                    releaseBuffer(buffer);
                }
            }

            private ArrayList<Pass> getPassQueue(Buffer buffer) {
                if (defaultBuffer == null || defaultBuffer == buffer) {
                    if (defaultPasses == null) {
                        defaultBuffer = buffer;
                        defaultPasses = new ArrayList<>();
                    }
                    return defaultPasses;
                }
                if (otherPasses == null) {
                    otherPasses = new LinkedHashMap<>();
                }
                return otherPasses.computeIfAbsent(buffer, it -> new ArrayList<>());
            }
        }

        public static class Buffer {

            private final int frameWidth;
            private final int frameHeight;
            private final float frameScale;

            private boolean isDirty = true;

            private int frameBufferId = -1;
            private int colorBufferId = -1;
            private int renderBufferId = -1;

            private final BufferLine line;
            private final ArrayList<Pass> passes = new ArrayList<>();

            public Buffer(int width, int height, float scale) {
                this.frameWidth = width;
                this.frameHeight = height;
                this.line = new BufferLine(0, 0, 0, width / scale, height / scale);
                this.frameScale = scale;
                this.create(width, height);
            }

            public void blit(CGRect src, CGRect dst) {
                var fh = frameHeight;
                var sx0 = (int) (src.minX() * frameScale);
                var sx1 = (int) (src.maxX() * frameScale);
                var sy0 = (int) (src.minY() * frameScale);
                var sy1 = (int) (src.maxY() * frameScale);
                var dx0 = (int) (dst.minX() * frameScale);
                var dx1 = (int) (dst.maxX() * frameScale);
                var dy0 = (int) (dst.minY() * frameScale);
                var dy1 = (int) (dst.maxY() * frameScale);
                GL30.glBlitFramebuffer(sx0, fh - sy0, sx1, fh - sy1, dx0, fh - dy0, dx1, fh - dy1, GL30.GL_COLOR_BUFFER_BIT, GL30.GL_NEAREST);
            }

            public void blit(CGRect src, CGRect dst, IVertexConsumer buffer) {
                var sw = 1 / line.maxWidth;
                var sh = 1 / line.maxHeight;
                buffer.vertex(dst.minX(), dst.minY(), 1f).uv(src.minX() * sw, 1f - src.minY() * sh).color(255, 255, 255, 255).endVertex();
                buffer.vertex(dst.minX(), dst.maxY(), 1f).uv(src.minX() * sw, 1f - src.maxY() * sh).color(255, 255, 255, 255).endVertex();
                buffer.vertex(dst.maxX(), dst.maxY(), 1f).uv(src.maxX() * sw, 1f - src.maxY() * sh).color(255, 255, 255, 255).endVertex();
                buffer.vertex(dst.maxX(), dst.minY(), 1f).uv(src.maxX() * sw, 1f - src.minY() * sh).color(255, 255, 255, 255).endVertex();
            }

            public void mask(CGRect src, IVertexConsumer buffer) {
                var x = src.maxX();
                var y = src.maxY();
                var width = src.width();
                var height = src.height();

                var tx1 = 0f;
                var tx2 = 0f;
                var ty1 = 0f;
                var ty2 = 0f;

                // some small triangle with length of 1 pixel.
                var pi2 = OpenMath.PIHalf_f;
                var ts = (int) (pi2 * Math.abs(width)); // 2 * pi * r / 4
                for (var idx = 0; idx <= ts; idx++) {
                    var ap = pi2 * idx / ts;
                    tx1 = tx2;
                    ty1 = ty2;
                    tx2 = width * OpenMath.cos(ap);
                    ty2 = height * OpenMath.sin(ap);
                    if (idx < 1) {
                        continue;
                    }
                    buffer.vertex(x, y, 0).color(255, 255, 255, 255).endVertex();
                    buffer.vertex(x - tx1, y - ty1, 0).color(255, 255, 255, 255).endVertex();
                    buffer.vertex(x - tx2, y - ty2, 0).color(255, 255, 255, 255).endVertex();
                }
            }

            public void init() {
                if (!isDirty) {
                    return;
                }
                isDirty = false;
                GL30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                GL30.glClearStencil(0);
                GL30.glStencilMask(0xFF);
                GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT);
            }

            public void release() {
                GL30.glDeleteRenderbuffers(renderBufferId);
                GL30.glDeleteTextures(colorBufferId);
                GL30.glDeleteFramebuffers(frameBufferId);
                renderBufferId = -1;
                colorBufferId = -1;
                frameBufferId = -1;
            }

            @Nullable
            public Pass add(CGRect rect) {
                var width = Math.min(Math.abs(rect.width()), line.maxWidth);
                var height = Math.min(Math.abs(rect.height()), line.maxHeight);
                var pass = line.add(width, height, rect);
                if (pass != null) {
                    passes.add(pass);
                }
                return pass;
            }

            public void remove(Pass obj) {
                passes.remove(obj);
                line.remove(obj, null);
            }


            public boolean isEmpty() {
                return passes.isEmpty();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Buffer that)) return false;
                return frameBufferId == that.frameBufferId;
            }

            @Override
            public int hashCode() {
                return frameBufferId;
            }

            private void create(int width, int height) {
                var oldFrameBufferId = GL30.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
                frameBufferId = GL30.glGenFramebuffers();
                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBufferId);

                // create color buffers.
                colorBufferId = GL30.glGenTextures();
                GL30.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, colorBufferId);
                GL32.glTexImage2DMultisample(GL32.GL_TEXTURE_2D_MULTISAMPLE, 4, GL30.GL_RGB, width, height, true);
                GL30.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, 0);
                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL32.GL_TEXTURE_2D_MULTISAMPLE, colorBufferId, 0);

                // create render buffers.
                renderBufferId = GL30.glGenRenderbuffers();
                GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderBufferId);
                GL30.glRenderbufferStorageMultisample(GL30.GL_RENDERBUFFER, 4, GL30.GL_DEPTH24_STENCIL8, width, height);
                GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
                GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, renderBufferId);

                int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
                if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
                    ModLog.debug("Framebuffer is not complete!");
                }

                GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, oldFrameBufferId);
            }
        }

        public static class BufferLine {

            private float contentTop;
            private float contentLeft;
            private float contentWidth = 0;
            private float contentHeight = 0;

            private BufferLine next;

            private final int id;
            private final float maxWidth;
            private final float maxHeight;

            private final ArrayList<Pass> passes = new ArrayList<>();

            public BufferLine(int id, float left, float top, float maxWidth, float maxHeight) {
                this.id = id;
                this.contentTop = top;
                this.contentLeft = left;
                this.maxWidth = maxWidth;
                this.maxHeight = maxHeight;
            }

            @Nullable
            public Pass add(float width, float height, CGRect source) {
                // when line is lock height, we can only add it in next line.
                if (next != null && height > contentHeight) {
                    return next.add(width, height, source);
                }
                // we have not enough remaining height.
                if (contentTop + height > maxHeight) {
                    return null;
                }
                // we first add content to the end, for reduces overlay checks.
                if (contentLeft + width <= maxWidth) {
                    var destination = new CGRect(contentLeft, contentTop, width, height);
                    var pass = new Pass(id, source, destination);
                    contentLeft += width;
                    contentWidth += width;
                    contentHeight = Math.max(height, contentHeight);
                    passes.add(pass);
                    return pass;
                }
                // we need check the gap width, and try to reuse it when has enough gap.
                //if (contentLeft - contentWidth >= width) {
                // TODO: @SAGESSE IMPL
                //}
                // we have not enough remaining height add to next line.
                if (contentTop + contentHeight + height > maxHeight) {
                    return null;
                }
                // we don't have enough content of width, so let next line to add it.
                if (next == null) {
                    next = new BufferLine(id + 1, 0, contentTop + contentHeight, maxWidth, maxHeight);
                }
                return next.add(width, height, source);
            }

            public void remove(Pass pass, @Nullable BufferLine parent) {
                if (pass.lineId != id) {
                    if (next != null) {
                        next.remove(pass, this);
                    }
                    return;
                }
                passes.remove(pass);
                if (passes.isEmpty()) {
                    contentLeft = 0;
                    contentWidth = 0;
                    contentHeight = 0;
                    if (parent != null) {
                        parent.next = null;
                    }
                    return;
                }
                var rect = pass.destination;
                contentWidth -= rect.width;
            }
        }
    }
}
