package moe.plushie.armourers_workshop.core.skin.animation.engine.bind;

import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.LevelSelector;
import net.minecraft.world.level.Level;

public class LevelSelectorImpl<T extends Level> implements LevelSelector {

    protected T level;

    public LevelSelectorImpl<T> apply(T level) {
        this.level = level;
        return this;
    }

    @Override
    public int getMoonPhase() {
        return level.getMoonPhase();
    }

    @Override
    public double getDays() {
        // ((float) (level.getDayTime() + 6000L) / 24000) % 1;
        return (level.getDayTime() + 6000L) / 24000d;
    }

    @Override
    public double getTimestamp() {
        return level.getDayTime();
    }

    @Override
    public int getWeather() {
        if (level.isThundering()) {
            return 2;
        }
        if (level.isRaining()) {
            return 1;
        }
        return 0; // sunny
    }

    @Override
    public String getDimensionId() {
        return level.dimension().location().toString();
    }
}
