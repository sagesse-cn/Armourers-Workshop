package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.core.data.DataContainer;
import moe.plushie.armourers_workshop.core.data.DataContainerKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

import java.util.IdentityHashMap;

@Environment(EnvType.CLIENT)
public class EmbeddedItemModels {

    private static final DataContainerKey<EmbeddedItemModels> KEY = DataContainerKey.of("EmbeddedItemModels", EmbeddedItemModels.class);

    private final IdentityHashMap<BakedModel, EmbeddedItemModel> models = new IdentityHashMap<>();

    public static EmbeddedItemModels of(ItemStack itemStack) {
        return DataContainer.of(itemStack, KEY, it -> new EmbeddedItemModels());
    }

    public void put(BakedModel bakedModel, EmbeddedItemModel model) {
        models.put(bakedModel, model);
    }

    public EmbeddedItemModel get(BakedModel bakedModel) {
        return models.get(bakedModel);
    }
}
