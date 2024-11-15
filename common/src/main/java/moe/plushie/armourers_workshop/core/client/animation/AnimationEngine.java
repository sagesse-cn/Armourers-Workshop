package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.skin.animation.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Variable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class AnimationEngine {

    private static final MolangVirtualMachine VM = MolangVirtualMachine.get();

    private static final Variable ANIM_TIME = VM.animTime;
    private static final Variable LIFE_TIME = VM.lifeTime;
    private static final Variable ACTOR_COUNT = VM.actorCount;
    private static final Variable TIME_OF_DAY = VM.timeOfDay;
    private static final Variable MOON_PHASE = VM.moonPhase;
    private static final Variable DISTANCE_FROM_CAMERA = VM.distanceFromCamera;
    private static final Variable IS_ON_GROUND = VM.isOnGround;
    private static final Variable IS_IN_WATER = VM.isInWater;
    private static final Variable IS_IN_WATER_OR_RAIN = VM.isInWaterOrRain;
    private static final Variable HEALTH = VM.health;
    private static final Variable MAX_HEALTH = VM.maxHealth;
    private static final Variable IS_ON_FIRE = VM.isOnFire;
    private static final Variable GROUND_SPEED = VM.groundSpeed;
    private static final Variable YAW_SPEED = VM.yawSpeed;
    private static final Variable HEAD_PITCH = VM.headPitch;

    public static void start() {
    }

    public static void stop() {
        // clear all binding.
        VM.getVariables().forEach((name, variable) -> {
            variable.set(0);
        });
    }

    public static void apply(@Nullable Object source, BakedSkin skin, float partialTick, float animationTicks, AnimationContext context) {
        context.begin(animationTicks);
        for (var animationController : skin.getAnimationControllers()) {
            // query the current play state of the animation controller.
            var playState = context.getPlayState(animationController);
            if (playState == null) {
                continue;
            }
            // we only bind it when transformer use the molang environment.
            var adjustedTicks = playState.getAdjustedTicks(animationTicks);
            if (animationController.isRequiresVirtualMachine()) {
                upload(source, adjustedTicks, playState.getBeginTime(), partialTick);
            }
            // check/switch frames of animation and write to applier.
            animationController.process(adjustedTicks, playState);
        }
        context.commit();
    }

    public static void upload(@Nullable Object source, double animTime, double startAnimTime, float partialTick) {
        var mc = Minecraft.getInstance();
        var level = mc.level;
        if (level == null) {
            return;
        }

        ANIM_TIME.set(animTime);
        LIFE_TIME.set(startAnimTime);
        TIME_OF_DAY.set(level.getDayTime() / 24000d);

        ACTOR_COUNT.set(level.getEntityCount());
        MOON_PHASE.set(level.getMoonPhase());

        if (source instanceof Entity entity) {
            uploadEntity(entity, animTime, startAnimTime, partialTick);
        }
        if (source instanceof LivingEntity livingEntity) {
            uploadLivingEntity(livingEntity, animTime, startAnimTime);
        }
    }

    private static void uploadEntity(Entity entity, double animTime, double startAnimTime, float partialTick) {
        DISTANCE_FROM_CAMERA.set(() -> Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().distanceTo(entity.position()));

        IS_ON_GROUND.set(entity.onGround());
        IS_IN_WATER.set(entity.isInWater());
        IS_IN_WATER_OR_RAIN.set(entity.isInWaterRainOrBubble());

        HEAD_PITCH.set(OpenMath.lerp(partialTick, entity.xRotO, entity.getXRot()));
//        entityModelData.headPitch = -headPitch;
//        entityModelData.netHeadYaw = -Mth.clamp(Mth.wrapDegrees(netHeadYaw), -85, 85);
    }

    private static void uploadLivingEntity(LivingEntity livingEntity, double animTime, double startAnimTime) {
        HEALTH.set(livingEntity.getHealth());
        MAX_HEALTH.set(livingEntity.getMaxHealth());
        IS_ON_FIRE.set(livingEntity.isOnFire());

        GROUND_SPEED.set(() -> {
            var velocity = livingEntity.getDeltaMovement();
            return OpenMath.sqrt((velocity.x * velocity.x) + (velocity.z * velocity.z));
        });
        YAW_SPEED.set(() -> {
            float a = livingEntity.getViewYRot((float) animTime - 0.1f);
            return livingEntity.getViewYRot((float) animTime - a);
        });
    }
}
