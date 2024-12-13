package moe.plushie.armourers_workshop.core.skin.particle.component.particle.appearance;

import moe.plushie.armourers_workshop.core.math.Size2i;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleBuilder;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleFacing;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

public class ParticleBillboardAppearance extends SkinParticleComponent {

    private final OpenPrimitive width;
    private final OpenPrimitive height;
    private final SkinParticleFacing facingCameraMode;
    private final Size2i textureSize;
    private final OpenPrimitive textureCoordsX;
    private final OpenPrimitive textureCoordsY;
    private final OpenPrimitive textureCoordsWidth;
    private final OpenPrimitive textureCoordsHeight;
    private final OpenPrimitive stepX;
    private final OpenPrimitive stepY;
    private final boolean isUseAnimation;
    private final int fps;
    private final OpenPrimitive maxFrame;
    private final boolean isStretchToLifetime;
    private final boolean isLoop;

    public ParticleBillboardAppearance(OpenPrimitive width, OpenPrimitive height, SkinParticleFacing facingCameraMode, Size2i textureSize, OpenPrimitive textureCoordsX, OpenPrimitive textureCoordsY, OpenPrimitive textureCoordsWidth, OpenPrimitive textureCoordsHeight, OpenPrimitive stepX, OpenPrimitive stepY, boolean isUseAnimation, int fps, OpenPrimitive maxFrame, boolean isStretchToLifetime, boolean isLoop) {
        this.width = width;
        this.height = height;
        this.facingCameraMode = facingCameraMode;
        this.textureSize = textureSize;
        this.textureCoordsX = textureCoordsX;
        this.textureCoordsY = textureCoordsY;
        this.textureCoordsWidth = textureCoordsWidth;
        this.textureCoordsHeight = textureCoordsHeight;
        this.stepX = stepX;
        this.stepY = stepY;
        this.isUseAnimation = isUseAnimation;
        this.fps = fps;
        this.maxFrame = maxFrame;
        this.isStretchToLifetime = isStretchToLifetime;
        this.isLoop = isLoop;
    }

    public ParticleBillboardAppearance(IInputStream stream) throws IOException {
        this.width = stream.readPrimitiveObject();
        this.height = stream.readPrimitiveObject();
        this.facingCameraMode = stream.readEnum(SkinParticleFacing.class);
        int textureWidth = stream.readInt();
        int textureHeight = stream.readInt();
        this.textureSize = new Size2i(textureWidth, textureHeight);
        this.textureCoordsX = stream.readPrimitiveObject();
        this.textureCoordsY = stream.readPrimitiveObject();
        this.textureCoordsWidth = stream.readPrimitiveObject();
        this.textureCoordsHeight = stream.readPrimitiveObject();
        this.stepX = stream.readPrimitiveObject();
        this.stepY = stream.readPrimitiveObject();
        this.isUseAnimation = stream.readBoolean();
        this.fps = stream.readInt();
        this.maxFrame = stream.readPrimitiveObject();
        this.isStretchToLifetime = stream.readBoolean();
        this.isLoop = stream.readBoolean();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(width);
        stream.writePrimitiveObject(height);
        stream.writeEnum(facingCameraMode);
        stream.writeInt(textureSize.getWidth());
        stream.writeInt(textureSize.getHeight());
        stream.writePrimitiveObject(textureCoordsX);
        stream.writePrimitiveObject(textureCoordsY);
        stream.writePrimitiveObject(textureCoordsWidth);
        stream.writePrimitiveObject(textureCoordsHeight);
        stream.writePrimitiveObject(stepX);
        stream.writePrimitiveObject(stepY);
        stream.writeBoolean(isUseAnimation);
        stream.writeInt(fps);
        stream.writePrimitiveObject(maxFrame);
        stream.writeBoolean(isStretchToLifetime);
        stream.writeBoolean(isLoop);
    }

    @Override
    public void applyToBuilder(SkinParticleBuilder builder) throws Exception {

        // TODO: NO IMPL @SAGESSE
    }

    //    public void calculateUVs(BedrockParticle particle, float partialTicks)
//    {
//        /* Update particle's UVs and size */
//        this.w = (float) this.sizeW.get() * 2.25F;
//        this.h = (float) this.sizeH.get() * 2.25F;
//
//        float u = (float) this.uvX.get();
//        float v = (float) this.uvY.get();
//        float w = (float) this.uvW.get();
//        float h = (float) this.uvH.get();
//
//        if (this.flipbook)
//        {
//            int index = (int) (particle.getAge(partialTicks) * this.fps);
//            int max = (int) this.maxFrame.get();
//
//            if (this.stretchFPS)
//            {
//                float lifetime = particle.lifetime <= 0 ? 0 : (particle.age + partialTicks) / particle.lifetime;
//
//                index = (int) (lifetime * max);
//            }
//
//            if (this.loop && max != 0)
//            {
//                index = index % max;
//            }
//
//            if (index > max)
//            {
//                index = max;
//            }
//
//            u += this.stepX * index;
//            v += this.stepY * index;
//        }
//
//        this.u1 = u;
//        this.v1 = v;
//        this.u2 = u + w;
//        this.v2 = v + h;
//    }


//    @Override
//    public void render(RenderableBedrockEmitter emitter, BedrockParticle particle, BufferBuilder builder, float partialTicks)
//    {
//        this.calculateUVs(particle, partialTicks);
//
//        /* Render the particle */
//        double px = Interpolations.lerp(particle.prevPosition.x, particle.position.x, partialTicks);
//        double py = Interpolations.lerp(particle.prevPosition.y, particle.position.y, partialTicks);
//        double pz = Interpolations.lerp(particle.prevPosition.z, particle.position.z, partialTicks);
//        float angle = Interpolations.lerp(particle.prevRotation, particle.rotation, partialTicks);
//
//        if (particle.relativePosition && particle.relativeRotation)
//        {
//            this.vector.set((float) px, (float) py, (float) pz);
//            emitter.rotation.transform(this.vector);
//
//            px = this.vector.x;
//            py = this.vector.y;
//            pz = this.vector.z;
//
//            px += emitter.lastGlobal.x;
//            py += emitter.lastGlobal.y;
//            pz += emitter.lastGlobal.z;
//        }
//
//        /* Calculate yaw and pitch based on the facing mode */
//        float entityYaw = emitter.cYaw;
//        float entityPitch = emitter.cPitch;
//        double entityX = emitter.cX;
//        double entityY = emitter.cY;
//        double entityZ = emitter.cZ;
//        boolean lookAt = this.facing == CameraFacing.LOOKAT_XYZ || this.facing == CameraFacing.LOOKAT_Y;
//
//        /* Flip width when frontal perspective mode */
//        if (emitter.perspective == 2)
//        {
//            this.w = -this.w;
//        }
//        /* In GUI renderer */
//        else if (emitter.perspective == 100 && !lookAt)
//        {
//            entityYaw = 180 - entityYaw;
//
//            this.w = -this.w;
//            this.h = -this.h;
//        }
//
//        if (lookAt)
//        {
//            double dX = entityX - px;
//            double dY = entityY - py;
//            double dZ = entityZ - pz;
//            double horizontalDistance = MathHelper.sqrt(dX * dX + dZ * dZ);
//
//            entityYaw = 180 - (float) (MathHelper.atan2(dZ, dX) * (180D / Math.PI)) - 90.0F;
//            entityPitch = (float) (-(MathHelper.atan2(dY, horizontalDistance) * (180D / Math.PI))) + 180;
//        }
//
//        /* Calculate the geometry for billboards using cool matrix math */
//        int light = emitter.getBrightnessForRender(partialTicks, px, py, pz);
//        int lightX = light >> 16 & 65535;
//        int lightY = light & 65535;
//
//        this.vertices[0].set(-this.w / 2, -this.h / 2, 0, 1);
//        this.vertices[1].set(this.w / 2, -this.h / 2, 0, 1);
//        this.vertices[2].set(this.w / 2, this.h / 2, 0, 1);
//        this.vertices[3].set(-this.w / 2, this.h / 2, 0, 1);
//        this.transform.setIdentity();
//
//        if (this.facing == CameraFacing.ROTATE_XYZ || this.facing == CameraFacing.LOOKAT_XYZ)
//        {
//            this.rotation.rotY(entityYaw / 180 * (float) Math.PI);
//            this.transform.mul(this.rotation);
//            this.rotation.rotX(entityPitch / 180 * (float) Math.PI);
//            this.transform.mul(this.rotation);
//        }
//        else if (this.facing == CameraFacing.ROTATE_Y || this.facing == CameraFacing.LOOKAT_Y) {
//            this.rotation.rotY(entityYaw / 180 * (float) Math.PI);
//            this.transform.mul(this.rotation);
//        }
//
//        this.rotation.rotZ(angle / 180 * (float) Math.PI);
//        this.transform.mul(this.rotation);
//        this.transform.setTranslation(new Vector3f((float) px, (float) py, (float) pz));
//
//        for (Vector4f vertex : this.vertices)
//        {
//            this.transform.transform(vertex);
//        }
//
//        float u1 = this.u1 / (float) this.textureWidth;
//        float u2 = this.u2 / (float) this.textureWidth;
//        float v1 = this.v1 / (float) this.textureHeight;
//        float v2 = this.v2 / (float) this.textureHeight;
//
//        builder.pos(this.vertices[0].x, this.vertices[0].y, this.vertices[0].z).tex(u1, v1).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
//        builder.pos(this.vertices[1].x, this.vertices[1].y, this.vertices[1].z).tex(u2, v1).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
//        builder.pos(this.vertices[2].x, this.vertices[2].y, this.vertices[2].z).tex(u2, v2).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
//        builder.pos(this.vertices[3].x, this.vertices[3].y, this.vertices[3].z).tex(u1, v2).lightmap(lightX, lightY).color(particle.r, particle.g, particle.b, particle.a).endVertex();
//    }
//
//    @Override
//    public void renderOnScreen(BedrockParticle particle, int x, int y, float scale, float partialTicks)
//    {
//        this.calculateUVs(particle, partialTicks);
//
//        this.w = this.h = 0.5F;
//        float angle = Interpolations.lerp(particle.prevRotation, particle.rotation, partialTicks);
//
//        /* Calculate the geometry for billboards using cool matrix math */
//        this.vertices[0].set(-this.w / 2, -this.h / 2, 0, 1);
//        this.vertices[1].set(this.w / 2, -this.h / 2, 0, 1);
//        this.vertices[2].set(this.w / 2, this.h / 2, 0, 1);
//        this.vertices[3].set(-this.w / 2, this.h / 2, 0, 1);
//        this.transform.setIdentity();
//        this.transform.setScale(scale * 2.75F);
//        this.transform.setTranslation(new Vector3f(x, y - scale / 2, 0));
//
//        this.rotation.rotZ(angle / 180 * (float) Math.PI);
//        this.transform.mul(this.rotation);
//
//        for (Vector4f vertex : this.vertices)
//        {
//            this.transform.transform(vertex);
//        }
//
//        float u1 = this.u1 / (float) this.textureWidth;
//        float u2 = this.u2 / (float) this.textureWidth;
//        float v1 = this.v1 / (float) this.textureHeight;
//        float v2 = this.v2 / (float) this.textureHeight;
//
//        BufferBuilder builder = Tessellator.getInstance().getBuffer();
//
//        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
//        builder.pos(this.vertices[0].x, this.vertices[0].y, this.vertices[0].z).tex(u1, v1).color(particle.r, particle.g, particle.b, particle.a).endVertex();
//        builder.pos(this.vertices[1].x, this.vertices[1].y, this.vertices[1].z).tex(u2, v1).color(particle.r, particle.g, particle.b, particle.a).endVertex();
//        builder.pos(this.vertices[2].x, this.vertices[2].y, this.vertices[2].z).tex(u2, v2).color(particle.r, particle.g, particle.b, particle.a).endVertex();
//        builder.pos(this.vertices[3].x, this.vertices[3].y, this.vertices[3].z).tex(u1, v2).color(particle.r, particle.g, particle.b, particle.a).endVertex();
//
//        Tessellator.getInstance().draw();
//    }
}
