package moe.plushie.armourers_workshop.core.skin.animation.engine.bind;

import moe.plushie.armourers_workshop.compatibility.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.core.data.EntityDataStorage;
import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.BiomeSelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.BlockSelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.EntitySelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.animation.molang.core.VariableStorage;
import moe.plushie.armourers_workshop.core.skin.animation.molang.runtime.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

public class EntitySelectorImpl<T extends Entity> implements EntitySelector, VariableStorage {

    protected T entity;
    protected VariableStorage variableStorage;
    protected ContextSelectorImpl contextSelector;

    private final BiomeSelectorImpl biomeSelector = new BiomeSelectorImpl();
    private final BlockSelectorImpl blockSelector = new BlockSelectorImpl();

    public EntitySelectorImpl<T> apply(T entity, ContextSelectorImpl contextSelector) {
        this.entity = entity;
        this.contextSelector = contextSelector;
        this.variableStorage = EntityDataStorage.of(entity).getVariableStorage().map(it -> it.get(contextSelector)).orElse(null);
        return this;
    }

    public T getEntity() {
        return entity;
    }

    @Override
    public double getEyeYaw() {
        return entity.getViewXRot(getPartialTick());
    }

    @Override
    public double getEyePitch() {
        return entity.getViewYRot(getPartialTick());
    }

    @Override
    public double getHeadYaw() {
        return entity.getHeadYaw(getPartialTick());
    }

    @Override
    public double getHeadPitch() {
        return entity.getHeadPatch(getPartialTick());
    }

    @Override
    public double getX(double partialTicks) {
        return MathHelper.lerp(partialTicks, entity.xo, entity.getX());
    }

    @Override
    public double getY(double partialTicks) {
        return MathHelper.lerp(partialTicks, entity.yo, entity.getY());
    }

    @Override
    public double getZ(double partialTicks) {
        return MathHelper.lerp(partialTicks, entity.zo, entity.getZ());
    }

    @Override
    public int getCardinalFacing() {
        // 2 north, 3 south, 4 west, 5 east
        return entity.getDirection().get3DDataValue();
    }

    @Override
    public double distanceFromCamera() {
        return contextSelector.getCameraDistanceFormEntity(entity);
    }

    @Override
    public double distanceFromMove() {
        return entity.walkDist;
    }

    @Override
    public double distanceFromWalk() {
        return entity.moveDist;
    }

    @Override
    public double getYawSpeed() {
//        float a = entity.getViewYRot((float) animTime - 0.1f);
//        return entity.getViewYRot((float) animTime - a);
        return 20 * (entity.getYRot() - entity.yRotO);
    }

    @Override
    public double getGroundSpeed() {
        var velocity = entity.getDeltaMovement();
        return 20 * Math.sqrt(((velocity.x * velocity.x) + (velocity.z * velocity.z)));
    }

    @Override
    public double getVerticalSpeed() {
        return 20 * (entity.position().y - entity.yo);
    }

    @Override
    public boolean isVehicle() {
        return entity.isVehicle();
    }

    @Override
    public boolean isPassenger() {
        return entity.isPassenger();
    }

    @Override
    public boolean isInWater() {
        return entity.isInWater();
    }

    @Override
    public boolean isInWaterRainOrBubble() {
        return entity.isInWaterRainOrBubble();
    }

    @Override
    public boolean isOnFire() {
        return entity.isOnFire();
    }

    @Override
    public boolean isOnGround() {
        return entity.onGround();
    }

    @Override
    public boolean isSneaking() {
        return entity.onGround() && entity.getPose() == Pose.CROUCHING;
    }

    @Override
    public boolean isJumping() {
        return !entity.isPassenger() && !entity.onGround() && !entity.isInWater();
    }

    @Override
    public boolean isSprinting() {
        return entity.isSprinting();
    }

    @Override
    public boolean isSwimming() {
        return entity.isSwimming();
    }

    @Override
    public boolean isSleeping() {
        return entity.getPose() == Pose.SLEEPING;
    }

    @Override
    public boolean isSpectator() {
        return entity.isSpectator();
    }

    @Override
    public boolean isUnderWater() {
        return entity.isUnderWater();
    }

    @Override
    public boolean isCloseEyes() {
        if (isSleeping()) {
            return true;
        }
        float noise = (entity.getId() * 0.05f);
        float time = (contextSelector.getAnimationTicks() + noise) % 4.5f;
        return time > 4.25f;
    }

    @Override
    public boolean canSeeSky() {
        var level = entity.getLevel();
        var pos = entity.blockPosition();
        if (!level.canSeeSky(pos)) {
            return false;
        }
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() <= pos.getY();
    }

    @Override
    public double getTicksFrozen() {
        return entity.getTicksFrozen();
    }

    @Override
    public double getAirSupply() {
        return entity.getAirSupply();
    }

    @Nullable
    @Override
    public BiomeSelector getBiome() {
        var level = entity.getLevel();
        var biome = AbstractRegistryManager.getBiome(level, entity.blockPosition());
        return biomeSelector.apply(biome);
    }

    @Override
    public BlockSelector getRelativeBlock(int offsetX, int offsetY, int offsetZ) {
        var level = entity.getLevel();
        double x = entity.getX() + offsetX;
        double y = entity.getX() + offsetX;
        double z = entity.getX() + offsetX;
        var blockState = level.getBlockState(new BlockPos((int) x, (int) y, (int) z));
        return blockSelector.apply(blockState);
    }

    @Override
    public float getPartialTick() {
        return contextSelector.getPartialTick();
    }


    @Override
    public void setVariable(Name name, Result value) {
        variableStorage.setVariable(name, value);
    }

    @Override
    public Result getVariable(Name name) {
        return variableStorage.getVariable(name);
    }
}
