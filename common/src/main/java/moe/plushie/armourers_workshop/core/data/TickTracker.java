package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.init.ModDebugger;

public class TickTracker implements IDataSerializable.Mutable {

    private static final TickTracker CLIENT = new TickTracker();
    private static final TickTracker SERVER = new TickTracker();

    private long lastTime = System.nanoTime();
    private float animationTicks = 0.0f;

    public static TickTracker client() {
        return CLIENT;
    }

    public static TickTracker server() {
        return SERVER;
    }

    public void update(boolean isPaused) {
        // when tick is pause, ignore any time advance.
        long time = System.nanoTime();
        if (!isPaused) {
            float delta = (time - lastTime) / 1e9f;
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

    private static class CodingKeys {

        public static final IDataSerializerKey<Float> TIME = IDataSerializerKey.create("Time", IDataCodec.FLOAT, 0f);
    }
}
