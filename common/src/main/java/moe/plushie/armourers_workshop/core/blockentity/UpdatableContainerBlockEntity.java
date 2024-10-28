package moe.plushie.armourers_workshop.core.blockentity;

import moe.plushie.armourers_workshop.api.common.IHasInventory;
import moe.plushie.armourers_workshop.core.utils.NonNullItemList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class UpdatableContainerBlockEntity extends UpdatableBlockEntity implements Container, IHasInventory {

    public UpdatableContainerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public boolean isEmpty() {
        return getItems().stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int i) {
        return getItems().get(i);
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        var itemStack = ContainerHelper.removeItem(getItems(), i, j);
        if (!itemStack.isEmpty()) {
            this.setContainerChanged();
        }
        return itemStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        return ContainerHelper.takeItem(getItems(), i);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        getItems().set(i, itemStack);
        if (itemStack.getCount() > this.getMaxStackSize()) {
            itemStack.setCount(this.getMaxStackSize());
        }
        this.setContainerChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (getLevel() == null) {
            return false;
        }
        var pos = getBlockPos();
        var blockEntity = getLevel().getBlockEntity(pos);
        if (blockEntity != this) {
            return false;
        }
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        getItems().clear();
    }

    protected void setContainerChanged() {
        setChanged();
    }

    protected abstract NonNullItemList getItems();

    @Override
    public Container getInventory() {
        return this;
    }
}
