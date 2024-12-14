package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.init.ModDebugger;

public class DeltaTracker implements IDataSerializable.Mutable {

    private static final DeltaTracker CLIENT = new DeltaTracker();
    private static final DeltaTracker SERVER = new DeltaTracker();

    private long lastTime = currentTimeMillis();
    private float animationTicks = 0.0f;

    public static DeltaTracker client() {
        return CLIENT;
    }

    public static DeltaTracker server() {
        return SERVER;
    }

    public void update(boolean isPaused) {
        // when tick is pause, ignore any time advance.
        long time = currentTimeMillis();
        if (!isPaused) {
            float delta = (time - lastTime) / 1000f;
            animationTicks += delta * ModDebugger.animationSpeed;
        }
        lastTime = time;
    }

    public float animationTicks() {
        return animationTicks;
    }

    public void setAnimationTicks(float animationTicks) {
        this.animationTicks = animationTicks;
    }

    @Override
    public void serialize(IDataSerializer serializer) {
        serializer.write(CodingKeys.TIME, animationTicks);
    }

    @Override
    public void deserialize(IDataSerializer serializer) {
        animationTicks = serializer.read(CodingKeys.TIME);
    }

    private static long currentTimeMillis() {
        // we use System.nanoTime() instead of System.currentTimeMillis(),
        // because System.currentTimeMillis() have big fluctuations (>5ms).
        return System.nanoTime() / 1000000L;
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<Float> TIME = IDataSerializerKey.create("Time", IDataCodec.FLOAT, 0f);
    }
}
