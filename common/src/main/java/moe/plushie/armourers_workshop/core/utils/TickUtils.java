package moe.plushie.armourers_workshop.core.utils;

public class TickUtils {

    private static final DeltaTracker TRACKER = DeltaTracker.client();

    public static void tick(boolean isPaused) {
        TRACKER.update(isPaused);
    }

    public static float animationTicks() {
        return TRACKER.animationTicks();
    }
}
