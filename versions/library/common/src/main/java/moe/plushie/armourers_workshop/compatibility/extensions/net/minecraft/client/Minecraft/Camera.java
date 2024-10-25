package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.client.Minecraft;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.math.OpenQuaternion3f;
import net.minecraft.client.Minecraft;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.20, )")
@Extension
public class Camera {

    public static OpenQuaternion3f getCameraOrientation(@This Minecraft minecraft) {
        var quat = minecraft.getEntityRenderDispatcher().cameraOrientation();
        return new OpenQuaternion3f(quat.x, quat.y, quat.z, quat.w);
    }
}
