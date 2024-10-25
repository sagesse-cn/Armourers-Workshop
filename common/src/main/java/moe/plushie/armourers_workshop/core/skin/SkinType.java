package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.skin.ISkinArmorType;
import moe.plushie.armourers_workshop.api.skin.ISkinEquipmentSlot;
import moe.plushie.armourers_workshop.api.skin.ISkinToolType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.part.ISkinPartType;
import moe.plushie.armourers_workshop.core.utils.Objects;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public class SkinType implements ISkinType {

    protected final String name;
    protected final int id;
    protected IResourceLocation registryName;
    protected List<? extends ISkinPartType> parts;

    public SkinType(String name, int id, List<? extends ISkinPartType> parts) {
        this.parts = parts;
        this.name = name;
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return registryName.toString();
    }

    @Override
    public IResourceLocation getRegistryName() {
        return registryName;
    }

    public void setRegistryName(IResourceLocation registryName) {
        this.registryName = registryName;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "id", id, "name", registryName);
    }

    @Override
    public List<? extends ISkinPartType> getParts() {
        return parts;
    }


    public static class Armor extends SkinType implements ISkinArmorType {
        protected ISkinEquipmentSlot slotType;

        public Armor(String name, int id, ISkinEquipmentSlot slotType, List<? extends ISkinPartType> parts) {
            super(name, id, parts);
            this.slotType = slotType;
        }

        @Override
        public ISkinEquipmentSlot getSlotType() {
            return slotType;
        }
    }

    public static class Tool extends SkinType implements ISkinToolType {

        protected Predicate<ItemStack> predicate;

        public Tool(String name, int id, List<? extends ISkinPartType> parts, Predicate<ItemStack> predicate) {
            super(name, id, parts);
            this.predicate = predicate;
        }

        @Override
        public boolean contains(ItemStack itemStack) {
            return predicate.test(itemStack);
        }
    }
}
