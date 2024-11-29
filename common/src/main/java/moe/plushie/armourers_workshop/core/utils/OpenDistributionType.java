package moe.plushie.armourers_workshop.core.utils;

public enum OpenDistributionType {
    CLIENT, INTEGRATED_SERVER, DEDICATED_SERVER;

    public boolean isClient() {
        return this == CLIENT;
    }

    public boolean isServer() {
        return this == INTEGRATED_SERVER || this == DEDICATED_SERVER;
    }

    public boolean isIntegratedServer() {
        return this == INTEGRATED_SERVER;
    }

    public boolean isDedicatedServer() {
        return this == DEDICATED_SERVER;
    }
}
