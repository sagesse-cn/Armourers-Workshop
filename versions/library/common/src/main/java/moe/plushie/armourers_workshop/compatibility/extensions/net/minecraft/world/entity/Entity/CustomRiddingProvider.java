package moe.plushie.armourers_workshop.compatibility.extensions.net.minecraft.world.entity.Entity;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentPose;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentTypes;
import moe.plushie.armourers_workshop.utils.DataContainer;
import moe.plushie.armourers_workshop.utils.DataContainerKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Horse;
import org.jetbrains.annotations.Nullable;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Available("[1.16, )")
@Extension
public class CustomRiddingProvider {

    private static final DataContainerKey<Vector3f> KEY = DataContainerKey.of("Passengers", Vector3f.class);

    @Nullable
    public static Vector3f getCustomRidding(@This Entity entity, int index) {
        var renderData = EntityRenderData.of(entity);
        if (renderData == null || index == -1) {
            return null; // can't found custom ridding position.
        }
        var attachmentPos = renderData.getAttachmentPose(SkinAttachmentTypes.RIDING);
        if (attachmentPos == null) {
            return null; // not provided.
        }
        return DataContainer.getValue(entity, KEY);
    }

    public static void setCustomRidding(@This Entity entity, int index, @Nullable Vector3f position) {
        DataContainer.setValue(entity, KEY, position);
    }


    public static Vector3f getCustomRidding(@This Entity entity, float partialTicks, SkinAttachmentPose pose) {
        var scale = entity.getCustomRiddingScale();
        var mat = OpenMatrix4f.createScaleMatrix(1, 1, 1);
        mat.scale(1, -1, -1);
        mat.scale(1 / 16f, 1 / 16f, 1 / 16f);
        mat.rotate(Vector3f.YP.rotationDegrees(entity.getViewYRot(partialTicks)));
        mat.scale(scale, scale, scale);
        mat.multiply(pose.last().pose());
        mat.translate(0, -1.5f, 0);
        return Vector3f.ZERO.transforming(mat);
    }

    public static void setCustomRidding(@This Entity entity, Entity passenger, Vector3f offset) {
        double tx = passenger.getX();
        double ty = passenger.getY();
        double tz = passenger.getZ();

        int index = entity.getPassengers().indexOf(passenger);
        entity.setCustomRidding(index, offset);
        entity.positionRider(passenger);

        double dx = passenger.getX() - tx;
        double dy = passenger.getY() - ty;
        double dz = passenger.getZ() - tz;

        passenger.xo += dx;
        passenger.yo += dy;
        passenger.zo += dz;
        passenger.xOld += dx;
        passenger.yOld += dy;
        passenger.zOld += dz;
    }

    public static float getCustomRiddingScale(@This Entity entity) {
        if (entity instanceof Horse) {
            return 1.1f;
        }
        return 1.0f;
    }
}
