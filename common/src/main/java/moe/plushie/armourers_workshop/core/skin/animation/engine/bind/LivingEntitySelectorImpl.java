package moe.plushie.armourers_workshop.core.skin.animation.engine.bind;

import moe.plushie.armourers_workshop.compatibility.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.EffectSelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.ItemSelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.bind.selector.LivingEntitySelector;
import moe.plushie.armourers_workshop.core.skin.animation.molang.runtime.MathHelper;
import moe.plushie.armourers_workshop.core.utils.Collections;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LivingEntitySelectorImpl<T extends LivingEntity> extends EntitySelectorImpl<T> implements LivingEntitySelector {

    private static final Map<String, Optional<EquipmentSlot>> NAMED_SLOTS = new ConcurrentHashMap<>();

    private static final Map<String, EquipmentSlot> FIXED_SLOTS = Collections.immutableMap(builder -> {
        builder.put("chest", EquipmentSlot.CHEST);
        builder.put("feet", EquipmentSlot.FEET);
        builder.put("head", EquipmentSlot.HEAD);
        builder.put("legs", EquipmentSlot.LEGS);
        builder.put("mainhand", EquipmentSlot.MAINHAND);
        builder.put("offhand", EquipmentSlot.OFFHAND);
    });

    private final ItemSelectorImpl itemSelector = new ItemSelectorImpl();
    private final EffectSelectorImpl effectSelector = new EffectSelectorImpl();

    @Override
    public LivingEntitySelectorImpl<T> apply(T entity, ContextSelectorImpl context) {
        super.apply(entity, context);
        return this;
    }

    @Override
    public double getBodyYaw() {
        return MathHelper.lerp(getPartialTick(), entity.xRotO, entity.getXRot());
    }

    @Override
    public double getBodyPitch() {
        return MathHelper.wrapDegrees(MathHelper.lerp(getPartialTick(), entity.yRotO, entity.getYRot()));
    }

    @Override
    public double getHealth() {
        return entity.getHealth();
    }

    @Override
    public double getMaxHealth() {
        return entity.getMaxHealth();
    }

    @Override
    public double getArmorValue() {
        return entity.getArmorValue();
    }

    @Override
    public double getHurtTime() {
        return entity.hurtTime;
    }

    @Override
    public boolean isDeadOrDying() {
        return entity.isDeadOrDying();
    }

    @Override
    public boolean isEating() {
        return entity.getUseItem().getUseAnimation() == UseAnim.EAT;
    }

    @Override
    public boolean isSleeping() {
        return entity.isSleeping();
    }

    @Override
    public boolean isUsingItem() {
        return entity.isUsingItem();
    }

    @Override
    public boolean isAutoSpinAttack() {
        return entity.isAutoSpinAttack();
    }

    @Override
    public boolean isOnClimbable() {
        return entity.onClimbable();
    }

    @Override
    public double getUsingItemDuration() {
        return entity.getTicksUsingItem() / 20.0;
    }

    @Override
    public double getUsingItemMaxDuration() {
        var item = entity.getUseItem();
        if (!item.isEmpty()) {
            return item.getUseDuration(entity) / 20.0;
        }
        return 0;
    }

    @Override
    public double getUsingItemRemainingDuration() {
        return entity.getUseItemRemainingTicks() / 20.0;
    }

    @Override
    public int getArrowCount() {
        return entity.getArrowCount();
    }

    @Override
    public int getStingerCount() {
        return entity.getStingerCount();
    }

    @Override
    public double getAttributeValue(String name) {
        return AbstractRegistryManager.getAttribute(entity, name);
    }

    @Nullable
    @Override
    public EffectSelector getEffect(String name) {
        var effect = AbstractRegistryManager.getEffect(entity, name);
        if (effect != null) {
            return effectSelector.apply(effect);
        }
        return null;
    }

    @Nullable
    @Override
    public ItemSelector getEquippedItem(String name) {
        var slot = NAMED_SLOTS.computeIfAbsent(name, LivingEntitySelectorImpl::findSlot);
        var itemStack = slot.map(it -> entity.getItemBySlot(it)).orElse(ItemStack.EMPTY);
        if (!itemStack.isEmpty()) {
            return itemSelector.apply(itemStack);
        }
        return null;
    }


    @Override
    public int getEquipmentCount() {
        int count = 0;
        for (var slot : EquipmentSlot.values()) {
            if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
                continue;
            }
            var stack = entity.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getLastClimbableFacing() {
        var level = entity.getLevel();
        if (level == null) {
            return 0;
        }
        var climbablePos = entity.getLastClimbablePos();
        if (climbablePos.isEmpty()) {
            return 0;
        }
        var blockState = level.getBlockState(climbablePos.get());
        var facing = blockState.getOptionalValue(HorizontalDirectionalBlock.FACING);
        //  0 south, 1 west, 2 north, 3 east
        return facing.map(Direction::get2DDataValue).orElse(0);
    }


    // https://learn.microsoft.com/en-us/minecraft/creator/scriptapi/minecraft/server/equipmentslot?view=minecraft-bedrock-stable
    private static Optional<EquipmentSlot> findSlot(String name) {
        // [slot.]Head -> head
        return Optional.ofNullable(FIXED_SLOTS.get(name.toLowerCase()));
    }
}
