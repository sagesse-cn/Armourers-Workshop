package moe.plushie.armourers_workshop.compatibility.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IRenderTypeBuilder;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderFormat;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Available("[1.18, )")
@Environment(EnvType.CLIENT)
public class AbstractRenderType extends RenderType {

    private static final Map<SkinRenderFormat, Supplier<IRenderTypeBuilder>> MAPPER = _make(it -> {

        it.put(SkinRenderFormat.LINE, () -> _builder(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINES, POSITION_COLOR_SHADER));
        it.put(SkinRenderFormat.LINE_STRIP, () -> _builder(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.DEBUG_LINE_STRIP, POSITION_COLOR_SHADER));

        it.put(SkinRenderFormat.IMAGE, () -> _builder(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, POSITION_COLOR_TEX_LIGHTMAP_SHADER).overlay().lightmap());

        it.put(SkinRenderFormat.BLIT_MASK, () -> _builder(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, POSITION_COLOR_SHADER));

        it.put(SkinRenderFormat.GUI_COLOR, () -> _builder(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, POSITION_COLOR_SHADER));
        it.put(SkinRenderFormat.GUI_IMAGE, () -> _builder(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, POSITION_TEX_SHADER));
        it.put(SkinRenderFormat.GUI_HIGHLIGHTED_TEXT, () -> _builder(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, POSITION_SHADER));

        it.put(SkinRenderFormat.BLOCK, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_SOLID_SHADER));
        it.put(SkinRenderFormat.BLOCK_CUTOUT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_CUTOUT_SHADER));

        it.put(SkinRenderFormat.ENTITY_CUTOUT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_CUTOUT_SHADER));
        it.put(SkinRenderFormat.ENTITY_CUTOUT_NO_CULL, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER));
        it.put(SkinRenderFormat.ENTITY_TRANSLUCENT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER));
        it.put(SkinRenderFormat.ENTITY_ALPHA, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_ALPHA_SHADER));

        it.put(SkinRenderFormat.SKIN_BLOCK_FACE_SOLID, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_SOLID_SHADER).overlay().lightmap());
        it.put(SkinRenderFormat.SKIN_BLOCK_FACE_TRANSLUCENT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_SOLID_SHADER).overlay().lightmap());
        it.put(SkinRenderFormat.SKIN_BLOCK_FACE_LIGHTING, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_SHADOW_SHADER).overlay().lightmap());
        it.put(SkinRenderFormat.SKIN_BLOCK_FACE_LIGHTING_TRANSLUCENT, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_SHADOW_SHADER).overlay().lightmap());

        it.put(SkinRenderFormat.SKIN_CUBE_FACE, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENTITY_CUTOUT_SHADER).overlay().lightmap());
        it.put(SkinRenderFormat.SKIN_CUBE_FACE_LIGHTING, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, RENDERTYPE_ENERGY_SWIRL_SHADER).overlay().lightmap());

        it.put(SkinRenderFormat.SKIN_MESH_FACE, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, RENDERTYPE_ENTITY_CUTOUT_SHADER).overlay().lightmap());
        it.put(SkinRenderFormat.SKIN_MESH_FACE_LIGHTING, () -> _builder(DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.TRIANGLES, RENDERTYPE_ENERGY_SWIRL_SHADER).overlay().lightmap());
    });

    public AbstractRenderType(String name, RenderType delegate, boolean affectsCrumbling, boolean sortUpload, Runnable setupRenderState, Runnable clearRenderState) {
        super(name, delegate.format(), delegate.mode(), delegate.bufferSize(), affectsCrumbling, sortUpload, () -> {
            delegate.setupRenderState();
            setupRenderState.run();
        }, () -> {
            clearRenderState.run();
            delegate.clearRenderState();
        });
    }

    public static IRenderTypeBuilder builder(SkinRenderFormat format) {
        var provider = MAPPER.get(format);
        if (provider != null) {
            var builder = provider.get();
            if (builder instanceof Builder builder1) {
                builder1.renderFormat = format;
            }
            return builder;
        }
        throw new RuntimeException("can't supported render mode");
    }

    private static Builder _builder(VertexFormat format, VertexFormat.Mode mode, ShaderStateShard shader) {
        var builder = new Builder(format, mode);
        builder.stateBuilder.setShaderState(shader);
        return builder;
    }

    private static <T, U> HashMap<T, U> _make(Consumer<HashMap<T, U>> consumer) {
        var map = new HashMap<T, U>();
        consumer.accept(map);
        return map;
    }

    public static class Builder implements IRenderTypeBuilder {

        private static final Map<Texturing, TexturingStateShard> TABLE_TEXTURING = _make(it -> {
//            it.put(Texturing.ENTITY_COLOR_OFFSET, new TexturingStateShard("aw_offset_texturing", RenderSystem::setupColorOffsetState, RenderSystem::clearColorOffsetState));
        });


        private static final Map<Target, OutputStateShard> TABLE_OUTPUT = _make(it -> {
            it.put(Target.MAIN, MAIN_TARGET);
            it.put(Target.OUTLINE, OUTLINE_TARGET);
            it.put(Target.TRANSLUCENT, TRANSLUCENT_TARGET);
            it.put(Target.CLOUDS, CLOUDS_TARGET);
            it.put(Target.WEATHER, WEATHER_TARGET);
            it.put(Target.PARTICLES, PARTICLES_TARGET);
            it.put(Target.ITEM_ENTITY, ITEM_ENTITY_TARGET);
        });

        private static final Map<Transparency, TransparencyStateShard> TABLE_TRANSPARENCY = _make(it -> {
            it.put(Transparency.DEFAULT, TRANSLUCENT_TRANSPARENCY);
            it.put(Transparency.TRANSLUCENT, TRANSLUCENT_TRANSPARENCY);
            it.put(Transparency.NONE, NO_TRANSPARENCY);
        });

        private static final Map<WriteMask, WriteMaskStateShard> TABLE_WRITE_MASK = _make(it -> {
            it.put(WriteMask.NONE, new WriteMaskStateShard(false, false));
            it.put(WriteMask.COLOR_DEPTH_WRITE, COLOR_DEPTH_WRITE);
            it.put(WriteMask.COLOR_WRITE, COLOR_WRITE);
            it.put(WriteMask.DEPTH_WRITE, DEPTH_WRITE);
        });

        private static final Map<DepthTest, DepthTestStateShard> TABLE_DEPTH_TEST = _make(it -> {
            it.put(DepthTest.NONE, NO_DEPTH_TEST);
            it.put(DepthTest.EQUAL, EQUAL_DEPTH_TEST);
            it.put(DepthTest.LESS_EQUAL, LEQUAL_DEPTH_TEST);
        });

        boolean isOutline = false;
        boolean affectsCrumbling = false;
        boolean sortOnUpload = false;

        SkinRenderFormat renderFormat;
        CompositeState.CompositeStateBuilder stateBuilder = CompositeState.builder();
        ArrayList<Consumer<RenderType>> updater = new ArrayList<>();

        final VertexFormat format;
        final VertexFormat.Mode mode;

        private Builder(VertexFormat format, VertexFormat.Mode mode) {
            this.format = format;
            this.mode = mode;
            this.setupDefault();
        }

        private void setupDefault() {
            stateBuilder = stateBuilder.setCullState(NO_CULL);
            // stateBuilder.setAlphaState(DEFAULT_ALPHA);
        }

        @Override
        public IRenderTypeBuilder texture(IResourceLocation texture, boolean blur, boolean mipmap) {
            this.stateBuilder = stateBuilder.setTextureState(new TextureStateShard(texture.toLocation(), blur, mipmap));
            return this;
        }

        @Override
        public IRenderTypeBuilder texturing(Texturing texturing) {
            this.stateBuilder = stateBuilder.setTexturingState(TABLE_TEXTURING.getOrDefault(texturing, DEFAULT_TEXTURING));
            return this;
        }

        @Override
        public IRenderTypeBuilder target(Target target) {
            this.stateBuilder = stateBuilder.setOutputState(TABLE_OUTPUT.getOrDefault(target, MAIN_TARGET));
            return this;
        }

        @Override
        public IRenderTypeBuilder transparency(Transparency transparency) {
            this.stateBuilder = stateBuilder.setTransparencyState(TABLE_TRANSPARENCY.getOrDefault(transparency, NO_TRANSPARENCY));
            return this;
        }

        @Override
        public IRenderTypeBuilder writeMask(WriteMask mask) {
            this.stateBuilder = stateBuilder.setWriteMaskState(TABLE_WRITE_MASK.getOrDefault(mask, COLOR_DEPTH_WRITE));
            return this;
        }

        @Override
        public IRenderTypeBuilder depthTest(DepthTest test) {
            this.stateBuilder = stateBuilder.setDepthTestState(TABLE_DEPTH_TEST.getOrDefault(test, NO_DEPTH_TEST));
            return this;
        }

        @Override
        public IRenderTypeBuilder colorLogic(ColorLogic state) {
            this.stateBuilder = stateBuilder.setColorLogicState(state);
            return this;
        }

        @Override
        public IRenderTypeBuilder polygonOffset(float factor, float units) {
            this.stateBuilder = stateBuilder.setLayeringState(new LayeringStateShard("aw_polygon_offset_" + units, () -> {
                RenderSystem.enablePolygonOffset();
                RenderSystem.polygonOffset(factor, units);
            }, () -> {
                RenderSystem.polygonOffset(0, 0);
                RenderSystem.disablePolygonOffset();
            }));
            return this;
        }

        @Override
        public IRenderTypeBuilder lineWidth(float width) {
            this.stateBuilder = stateBuilder.setLineState(new LineStateShard(OptionalDouble.of(width)));
            return this;
        }

        @Override
        public IRenderTypeBuilder stroke(float width) {
            this.stateBuilder = stateBuilder.setLayeringState(new LayeringStateShard("aw_custom_line", () -> {
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                GL11.glLineWidth(width);
            }, () -> {
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            }));
            return this;
        }

        @Override
        public IRenderTypeBuilder cull() {
            this.stateBuilder = stateBuilder.setCullState(CULL);
            return this;
        }

        @Override
        public IRenderTypeBuilder lightmap() {
            this.stateBuilder = stateBuilder.setLightmapState(LIGHTMAP);
            return this;
        }

        @Override
        public IRenderTypeBuilder overlay() {
            this.stateBuilder = stateBuilder.setOverlayState(OVERLAY);
            return this;
        }

        @Override
        public IRenderTypeBuilder outline() {
            this.isOutline = true;
            return this;
        }

        @Override
        public IRenderTypeBuilder crumbling() {
            this.affectsCrumbling = true;
            return this;
        }

        @Override
        public IRenderTypeBuilder sortOnUpload() {
            this.sortOnUpload = true;
            return this;
        }

        @Override
        public <T> IRenderTypeBuilder property(IAssociatedContainerKey<T> key, T value) {
            this.updater.add(it -> DataContainer.set(it, key, value));
            return this;
        }

        @Override
        public RenderType build(String name) {
            var renderType = (RenderType) RenderType.create(name, format, mode, 256, affectsCrumbling, sortOnUpload, stateBuilder.createCompositeState(isOutline));
            updater.forEach(it -> it.accept(renderType));
            return renderType;
        }

        public Builder or(Function<CompositeState.CompositeStateBuilder, CompositeState.CompositeStateBuilder> builder) {
            this.stateBuilder = builder.apply(stateBuilder);
            return this;
        }
    }
}
