package moe.plushie.armourers_workshop.builder.blockentity;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.blockentity.UpdatableContainerBlockEntity;
import moe.plushie.armourers_workshop.utils.BlockUtils;
import moe.plushie.armourers_workshop.core.utils.NonNullItemList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.util.Strings;

public class OutfitMakerBlockEntity extends UpdatableContainerBlockEntity {

    private String itemName = "";
    private String itemFlavour = "";

    private final NonNullItemList items = new NonNullItemList(getContainerSize());

    public OutfitMakerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    public void readAdditionalData(IDataSerializer serializer) {
        items.deserialize(serializer);
        itemName = serializer.read(CodingKeys.MAKER_NAME);
        itemFlavour = serializer.read(CodingKeys.MAKER_FLAVOUR);
    }

    public void writeAdditionalData(IDataSerializer serializer) {
        items.serialize(serializer);
        if (Strings.isNotEmpty(itemName)) {
            serializer.write(CodingKeys.MAKER_NAME, itemName);
        }
        if (Strings.isNotEmpty(itemFlavour)) {
            serializer.write(CodingKeys.MAKER_FLAVOUR, itemFlavour);
        }
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String name) {
        this.itemName = name;
        BlockUtils.combine(this, this::sendBlockUpdates);
    }

    public String getItemFlavour() {
        return itemFlavour;
    }

    public void setItemFlavour(String flavour) {
        this.itemFlavour = flavour;
        BlockUtils.combine(this, this::sendBlockUpdates);
    }

    @Override
    protected NonNullItemList getItems() {
        return items;
    }

    @Override
    public int getContainerSize() {
        return 21;
    }

    private static class CodingKeys {

        public static final IDataSerializerKey<String> MAKER_NAME = IDataSerializerKey.create("Name", IDataCodec.STRING, "");
        public static final IDataSerializerKey<String> MAKER_FLAVOUR = IDataSerializerKey.create("Flavour", IDataCodec.STRING, "");
    }
}


