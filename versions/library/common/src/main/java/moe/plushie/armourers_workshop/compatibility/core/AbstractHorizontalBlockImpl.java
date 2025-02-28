package moe.plushie.armourers_workshop.compatibility.core;

import com.mojang.serialization.MapCodec;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.ILootContext;
import moe.plushie.armourers_workshop.api.common.ILootContextParam;
import moe.plushie.armourers_workshop.api.common.ITooltipContext;
import moe.plushie.armourers_workshop.compatibility.core.data.AbstractTooltipContext;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Available("[1.21, )")
public abstract class AbstractHorizontalBlockImpl extends HorizontalDirectionalBlock {

    public AbstractHorizontalBlockImpl(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<? extends AbstractHorizontalBlockImpl> codec() {
        return null;
    }

    @Override
    public final List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
        return this.getDrops(blockState, new LootContextBuilder(builder));
    }

    public List<ItemStack> getDrops(BlockState blockState, ILootContext context) {
        return super.getDrops(blockState, ((LootContextBuilder) context).builder);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        return destroyByPlayer(level, blockPos, blockState, player);
    }

    public BlockState destroyByPlayer(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        return super.playerWillDestroy(level, blockPos, blockState, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        // If user is rewritten, forward it.
        if (blockState.getBlock() != this) {
            return blockState.useWithoutItem(level, player, blockHitResult.withPosition(blockPos));
        }
        return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
    }

//    @Override
//    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
//        return super.useItemOn(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
//    }

    public void appendHoverText(ItemStack itemStack, List<Component> tooltips, ITooltipContext context) {
        AbstractTooltipContext<Item.TooltipContext> context1 = Objects.unsafeCast(context);
        super.appendHoverText(itemStack, context1.context, tooltips, context1.flag);
    }

    @Override
    public final void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> tooltips, TooltipFlag tooltipFlag) {
        this.appendHoverText(itemStack, tooltips, new AbstractTooltipContext<>(tooltipContext, tooltipFlag));
    }

    public static class LootContextBuilder implements ILootContext {

        private static final Map<ILootContextParam<?>, LootContextParam<?>> KEYS = Collections.immutableMap(builder -> {
            builder.put(ILootContextParam.THIS_ENTITY, LootContextParams.THIS_ENTITY);
            builder.put(ILootContextParam.LAST_DAMAGE_PLAYER, LootContextParams.LAST_DAMAGE_PLAYER);
            builder.put(ILootContextParam.DAMAGE_SOURCE, LootContextParams.DAMAGE_SOURCE);
            builder.put(ILootContextParam.ATTACKING_ENTITY, LootContextParams.ATTACKING_ENTITY);
            builder.put(ILootContextParam.DIRECT_ATTACKING_ENTITY, LootContextParams.DIRECT_ATTACKING_ENTITY);
            builder.put(ILootContextParam.ORIGIN, LootContextParams.ORIGIN);
            builder.put(ILootContextParam.BLOCK_STATE, LootContextParams.BLOCK_STATE);
            builder.put(ILootContextParam.BLOCK_ENTITY, LootContextParams.BLOCK_ENTITY);
            builder.put(ILootContextParam.TOOL, LootContextParams.TOOL);
            builder.put(ILootContextParam.EXPLOSION_RADIUS, LootContextParams.EXPLOSION_RADIUS);
        });

        private final LootParams.Builder builder;

        public LootContextBuilder(LootParams.Builder builder) {
            this.builder = builder;
        }

        @Override
        public <T> T getParameter(ILootContextParam<T> param) {
            var value = builder.getParameter(KEYS.get(param));
            return param.getValueClass().cast(convert(value));
        }

        @Override
        @Nullable
        public <T> T getOptionalParameter(ILootContextParam<T> param) {
            var value = builder.getOptionalParameter(KEYS.get(param));
            if (value != null) {
                return param.getValueClass().cast(convert(value));
            }
            return null;
        }

        private Object convert(Object value) {
            var pos = Objects.safeCast(value, Vec3.class);
            if (pos != null) {
                return new OpenVector3f(pos.x, pos.y, pos.z);
            }
            return value;
        }
    }
}
