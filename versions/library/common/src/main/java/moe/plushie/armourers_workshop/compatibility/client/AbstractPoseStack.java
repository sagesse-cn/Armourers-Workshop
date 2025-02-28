package moe.plushie.armourers_workshop.compatibility.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.core.math.IMatrix3f;
import moe.plushie.armourers_workshop.api.core.math.IMatrix4f;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.core.math.IQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.math.OpenPoseStack;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AbstractPoseStack extends AbstractPoseStackImpl implements IPoseStack {

    private final PoseStack stack;

    public AbstractPoseStack() {
        this(new PoseStack());
    }

    public AbstractPoseStack(PoseStack poseStack) {
        this.stack = poseStack;
    }

    public static OpenPoseStack create(IPoseStack poseStack) {
        var poseStack1 = REUSABLE_QUEUE.get();
        poseStack1.last().set(poseStack.last());
        return poseStack1;
    }

    public static OpenPoseStack create(PoseStack poseStack) {
        var poseStack1 = REUSABLE_QUEUE.get();
        poseStack1.last().set(DataContainer.of(poseStack.last(), Pose::new));
        return poseStack1;
    }

    public static void reset(PoseStack poseStack) {
        //poseStack.setIdentity();
        poseStack.scale(1e-9f, 1e-9f, 1e-9f);
    }

    public static IPoseStack wrap(PoseStack poseStack) {
        return DataContainer.of(poseStack, AbstractPoseStack::new);
    }

    public static PoseStack unwrap(IPoseStack poseStack) {
        if (poseStack instanceof AbstractPoseStack poseStack1) {
            return poseStack1.stack;
        }
        var poseStack1 = new PoseStack();
        var poseStack2 = wrap(poseStack1);
        poseStack2.last().set(poseStack.last());
        return poseStack1;
    }

    public void pushPose() {
        stack.pushPose();
    }

    public void popPose() {
        stack.popPose();
    }

    public void translate(float x, float y, float z) {
        stack.translate(x, y, z);
    }

    public void scale(float x, float y, float z) {
        // https://web.archive.org/web/20240125142900/http://www.songho.ca/opengl/gl_normaltransform.html
        var entry = last();
        entry.pose.scale(x, y, z);
        if (Math.abs(x) == Math.abs(y) && Math.abs(y) == Math.abs(z)) {
            if (x < 0.0f || y < 0.0f || z < 0.0f) {
                entry.normal.scale(Math.signum(x), Math.signum(y), Math.signum(z));
            }
        } else {
            entry.normal.scale(1.0f / x, 1.0f / y, 1.0f / z);
            entry.properties |= 0x02;
        }
    }

    public void rotate(IQuaternionf quaternion) {
        stack.mulPose(quaternion);
    }

    @Override
    public void multiply(IMatrix3f matrix) {
        var entry = last();
        entry.normal.multiply(matrix);
    }

    @Override
    public void multiply(IMatrix4f matrix) {
        var entry = last();
        entry.pose.multiply(matrix);
//        if (!MatrixUtil.isTranslation(matrix)) {
//            if (MatrixUtil.isOrthonormal(matrix)) {
//                entry.normalMatrix.mul(new Matrix3f(matrix));
//            } else {
//                entry.computeNormal();
//            }
//        }
    }

    @Override
    public void setIdentity() {
        stack.setIdentity();
        last().setProperties(0);
    }

    @Override
    public Pose last() {
        return DataContainer.of(stack.last(), Pose::new);
    }

    public static class Pose implements IPoseStack.Pose {

        private final AbstractMatrix4f pose;
        private final AbstractMatrix3f normal;
        private int properties;

        public Pose(PoseStack.Pose pose) {
            this.pose = new AbstractMatrix4f(pose.pose());
            this.normal = new AbstractMatrix3f(pose.normal());
            this.properties = 0;
        }

        @Override
        public void transformPose(float[] values) {
            pose.multiply(values);
        }

        @Override
        public void transformNormal(float[] values) {
            normal.multiply(values);
            if ((properties & 0x02) != 0) {
                OpenMath.normalize(values);
            }
        }

        @Override
        public void set(IPoseStack.Pose entry) {
            pose.set(entry.pose());
            normal.set(entry.normal());
            properties = entry.properties();
        }

//        @Override
//        public void normalized() {
//            // when pose have non-uniform scale, we need to recalculate the normals.
//            if ((properties & 0x02) == 0) {
//                return;
//            }
//            normal.set(pose);
//            normal.invert();
//            normal.transpose();
//            properties &= ~0x02;
//        }

        @Override
        public IMatrix4f pose() {
            return pose;
        }

        @Override
        public IMatrix3f normal() {
            return normal;
        }

        public void setProperties(int properties) {
            this.properties = properties;
        }

        @Override
        public int properties() {
            return properties;
        }
    }
}
