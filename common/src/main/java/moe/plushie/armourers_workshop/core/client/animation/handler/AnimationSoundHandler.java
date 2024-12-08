package moe.plushie.armourers_workshop.core.client.animation.handler;

import moe.plushie.armourers_workshop.core.client.animation.AnimatedPointValue;
import moe.plushie.armourers_workshop.core.client.sound.SmartSoundManager;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationPoint;
import moe.plushie.armourers_workshop.core.skin.animation.engine.bind.BlockEntitySelectorImpl;
import moe.plushie.armourers_workshop.core.skin.animation.engine.bind.EntitySelectorImpl;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;

public class AnimationSoundHandler implements AnimatedPointValue.Effect {

    private final String name;
    private final SoundEvent soundEvent;

    public AnimationSoundHandler(SkinAnimationPoint.Sound sound) {
        this.name = sound.getEffect();
        this.soundEvent = SmartSoundManager.getInstance().register(sound.getProvider());
    }

    @Override
    public Runnable apply(ExecutionContext context) {
        var sound = createSound(context, 1.0f, 1.0f);
        open();
        playSound(sound);
        return () -> {
            stopSound(sound);
            RenderSystem.recordRenderCall(this::close);
        };
    }

    private void playSound(SoundInstance sound) {
        getSoundManager().play(sound);
        if (ModConfig.Client.enableAnimationDebug) {
            ModLog.debug("start play {}", this);
        }
    }

    private void stopSound(SoundInstance sound) {
        getSoundManager().stop(sound);
        if (ModConfig.Client.enableAnimationDebug) {
            ModLog.debug("stop play {}", this);
        }
    }

    private void open() {
        SmartSoundManager.getInstance().open(soundEvent);
    }

    private void close() {
        SmartSoundManager.getInstance().close(soundEvent);
    }

    private SoundInstance createSound(ExecutionContext context, float volume, float pitch) {
        // this current entity is block entity?
        if (context instanceof BlockEntitySelectorImpl<?> entity) {
            return SoundInstance.forBlockEntity(soundEvent, entity.getEntity(), volume, pitch);
        }
        // the current entity is entity?
        if (context instanceof EntitySelectorImpl<?> entity) {
            return SoundInstance.forEntity(soundEvent, entity.getEntity(), volume, pitch);
        }
        // the fallback is gui sounds, maybe?
        return SoundInstance.forUI(soundEvent, volume, pitch);
    }

    private SoundManager getSoundManager() {
        return Minecraft.getInstance().getSoundManager();
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", name);
    }
}
