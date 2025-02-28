package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.core.data.MannequinHitResult;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.skin.texture.EntityTextureDescriptor;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import moe.plushie.armourers_workshop.init.ModEntityTypes;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MannequinItem extends FlavouredItem {

    public MannequinItem(Properties properties) {
        super(properties);
    }

    public static boolean isSmall(ItemStack itemStack) {
        var entityTag = itemStack.get(ModDataComponents.ENTITY_DATA.get());
        if (entityTag != null) {
            var entityData = new MannequinEntity.EntityData(entityTag);
            return entityData.isSmall();
        }
        return false;
    }

    public static float getScale(ItemStack itemStack) {
        var entityTag = itemStack.get(ModDataComponents.ENTITY_DATA.get());
        if (entityTag != null) {
            var entityData = new MannequinEntity.EntityData(entityTag);
            return entityData.getScale();
        }
        return 1.0f;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getHand() != InteractionHand.MAIN_HAND) {
            return InteractionResult.FAIL;
        }
        var player = context.getPlayer();
        if (player == null) {
            return InteractionResult.FAIL;
        }
        var level = context.getLevel();
        var origin = new Vec3(player.getX(), player.getY(), player.getZ());
        var rayTraceResult = MannequinHitResult.test(player, origin, context.getClickLocation(), context.getClickedPos());
        var itemStack = context.getItemInHand();
        if (level instanceof ServerLevel serverLevel) {
            var entity = ModEntityTypes.MANNEQUIN.get().create(serverLevel, rayTraceResult.getBlockPos(), itemStack, MobSpawnType.SPAWN_EGG);
            if (entity == null) {
                return InteractionResult.FAIL;
            }
            var clickedLocation = rayTraceResult.getLocation();
            entity.absMoveTo(clickedLocation.x(), clickedLocation.y(), clickedLocation.z(), 0.0f, 0.0f);
            entity.setYBodyRot(rayTraceResult.getRotation());

            serverLevel.addFreshEntity(entity);
            serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);

            itemStack.shrink(1);
            return InteractionResult.sidedSuccess(serverLevel.isClientSide());
        }
        return InteractionResult.FAIL;
    }

    @Override
    public String getDescriptionId(ItemStack itemStack) {
        var scale = getScale(itemStack);
        if (scale <= 0.5f) {
            return super.getDescriptionId(itemStack) + ".small";
        }
        if (scale >= 2.0f) {
            return super.getDescriptionId(itemStack) + ".big";
        }
        return super.getDescriptionId(itemStack);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        super.appendHoverText(itemStack, tooltips, context);
        var descriptor = EntityTextureDescriptor.of(itemStack);
        if (descriptor.getName() != null) {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.user", descriptor.getName()));
        }
        if (descriptor.getURL() != null) {
            tooltips.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.url", descriptor.getURL()));
        }
    }
}
