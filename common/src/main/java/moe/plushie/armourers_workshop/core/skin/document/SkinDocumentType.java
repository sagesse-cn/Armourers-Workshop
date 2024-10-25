package moe.plushie.armourers_workshop.core.skin.document;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import moe.plushie.armourers_workshop.api.core.IRegistryEntry;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.api.skin.ISkinEquipmentType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.part.ISkinPartType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SkinDocumentType implements IRegistryEntry {

    private static final ImmutableMap<ISkinPartType, Collection<ISkinPartType>> LINKED_PARTS = new ImmutableMap.Builder<ISkinPartType, Collection<ISkinPartType>>()
            .put(SkinPartTypes.BIPPED_CHEST, Collections.newList(SkinPartTypes.BIPPED_TORSO))
            .put(SkinPartTypes.BIPPED_LEFT_ARM, Collections.newList(SkinPartTypes.BIPPED_LEFT_HAND))
            .put(SkinPartTypes.BIPPED_RIGHT_ARM, Collections.newList(SkinPartTypes.BIPPED_RIGHT_HAND))
            .put(SkinPartTypes.BIPPED_LEFT_THIGH, Collections.newList(SkinPartTypes.BIPPED_LEFT_LEG))
            .put(SkinPartTypes.BIPPED_RIGHT_THIGH, Collections.newList(SkinPartTypes.BIPPED_RIGHT_LEG))
            .put(SkinPartTypes.BIPPED_RIGHT_WING, Collections.newList(SkinPartTypes.BIPPED_RIGHT_PHALANX))
            .put(SkinPartTypes.BIPPED_LEFT_WING, Collections.newList(SkinPartTypes.BIPPED_LEFT_PHALANX))
            .put(SkinPartTypes.ITEM_SHIELD, Collections.newList(SkinPartTypes.ITEM_SHIELD1))
            .put(SkinPartTypes.ITEM_TRIDENT, Collections.newList(SkinPartTypes.ITEM_TRIDENT1))
            .put(SkinPartTypes.ITEM_FISHING_ROD, Collections.newList(SkinPartTypes.ITEM_FISHING_ROD1))
            .build();

    private static final ImmutableSet<ISkinPartType> DISABLED_PARTS = new ImmutableSet.Builder<ISkinPartType>()
            .add(SkinPartTypes.BLOCK_MULTI)
            .build();

    private final String category;
    private final ISkinType skinType;
    private final ArrayList<ISkinPartType> skinPartTypes;

    private IResourceLocation registryName;

    public SkinDocumentType(String category, ISkinType type) {
        this.category = category;
        this.skinType = type;
        this.skinPartTypes = generatePartTypes(type);
    }

    public String getName() {
        return registryName.toString();
    }

    public String getCategory() {
        return category;
    }

    public ISkinType getSkinType() {
        return skinType;
    }

    public List<? extends ISkinPartType> getSkinPartTypes() {
        return skinPartTypes;
    }

    public void setRegistryName(IResourceLocation registryName) {
        this.registryName = registryName;
    }

    @Override
    public IResourceLocation getRegistryName() {
        return registryName;
    }

    public boolean usesItemTransforms() {
        return skinType instanceof ISkinEquipmentType || skinType == SkinTypes.ITEM;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "name", category, "id", registryName);
    }

    private ArrayList<ISkinPartType> generatePartTypes(ISkinType type) {
        var partTypes = new ArrayList<ISkinPartType>();
        for (var partType : type.getParts()) {
            // manually disabled parts.
            if (DISABLED_PARTS.contains(partType)) {
                continue;
            }
            var linkedParts = LINKED_PARTS.get(partType);
            partTypes.add(partType);
            if (linkedParts != null) {
                partTypes.addAll(linkedParts);
            }
        }
        return partTypes;
    }
}
