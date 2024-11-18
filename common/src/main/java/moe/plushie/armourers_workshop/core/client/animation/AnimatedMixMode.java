package moe.plushie.armourers_workshop.core.client.animation;

public enum AnimatedMixMode {

    PRE_MAIN, MAIN, POST_MAIN;

    public static AnimatedMixMode byName(String name) {
        name = name.toLowerCase();
        if (name.matches("^(.+\\.)?pre_parallel(\\d+)$")) {
            return PRE_MAIN;
        }
        if (name.matches("^(.+\\.)?parallel(\\d+)$")) {
            return POST_MAIN;
        }
        return MAIN;
    }
}
