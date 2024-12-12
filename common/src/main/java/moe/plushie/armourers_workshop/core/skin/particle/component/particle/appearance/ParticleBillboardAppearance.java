package moe.plushie.armourers_workshop.core.skin.particle.component.particle.appearance;

import moe.plushie.armourers_workshop.core.math.Size2i;
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
    private final OpenPrimitive u;
    private final OpenPrimitive v;
    private final OpenPrimitive uvWidth;
    private final OpenPrimitive uvHeight;
    private final OpenPrimitive stepX;
    private final OpenPrimitive stepY;
    private final boolean isUseAnimation;
    private final int fps;
    private final OpenPrimitive maxFrame;
    private final boolean isStretchToLifetime;
    private final boolean isLoop;

    public ParticleBillboardAppearance(OpenPrimitive width, OpenPrimitive height, SkinParticleFacing facingCameraMode, Size2i textureSize, OpenPrimitive u, OpenPrimitive v, OpenPrimitive uvWidth, OpenPrimitive uvHeight, OpenPrimitive stepX, OpenPrimitive stepY, boolean isUseAnimation, int fps, OpenPrimitive maxFrame, boolean isStretchToLifetime, boolean isLoop) {
        this.width = width;
        this.height = height;
        this.facingCameraMode = facingCameraMode;
        this.textureSize = textureSize;
        this.u = u;
        this.v = v;
        this.uvWidth = uvWidth;
        this.uvHeight = uvHeight;
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
        this.u = stream.readPrimitiveObject();
        this.v = stream.readPrimitiveObject();
        this.uvWidth = stream.readPrimitiveObject();
        this.uvHeight = stream.readPrimitiveObject();
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
        stream.writePrimitiveObject(u);
        stream.writePrimitiveObject(v);
        stream.writePrimitiveObject(uvWidth);
        stream.writePrimitiveObject(uvHeight);
        stream.writePrimitiveObject(stepX);
        stream.writePrimitiveObject(stepY);
        stream.writeBoolean(isUseAnimation);
        stream.writeInt(fps);
        stream.writePrimitiveObject(maxFrame);
        stream.writeBoolean(isStretchToLifetime);
        stream.writeBoolean(isLoop);
    }
}
