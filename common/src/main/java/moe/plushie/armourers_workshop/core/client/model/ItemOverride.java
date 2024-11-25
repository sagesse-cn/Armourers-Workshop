package moe.plushie.armourers_workshop.core.client.model;

public class ItemOverride {

    private ItemModel model;

    private final ItemProperty[] properties;
    private final float[] values;

    public ItemOverride(ItemProperty[] properties, float[] values) {
        this.properties = properties;
        this.values = values;
    }

    public void setModel(ItemModel model) {
        this.model = model;
    }

    public ItemModel getModel() {
        return model;
    }

    public ItemProperty[] getProperties() {
        return properties;
    }

    public float[] getValues() {
        return values;
    }
}
