package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SkinItemSource {

    public static final SkinItemSource EMPTY = new SkinItemSource();

    private float renderPriority;
    private ItemStack itemStack;
    private SkinItemProperties itemProperties;

    private Vector3f itemScale;
    private Vector3f itemRotation;
    private OpenItemDisplayContext itemDisplayContext;

    public SkinItemSource() {
        this(0, ItemStack.EMPTY, OpenItemDisplayContext.NONE);
    }

    public SkinItemSource(float renderPriority, ItemStack itemStack, OpenItemDisplayContext itemDisplayContext) {
        this.renderPriority = renderPriority;
        this.itemStack = itemStack;
        this.itemDisplayContext = itemDisplayContext;
    }

    public static SkinItemSource create(ItemStack itemStack) {
        return create(0, itemStack, OpenItemDisplayContext.NONE, null);
    }

    public static SkinItemSource create(ItemStack itemStack, OpenItemDisplayContext transformType) {
        return create(0, itemStack, transformType, null);
    }

    public static SkinItemSource create(float renderPriority, ItemStack itemStack) {
        return create(renderPriority, itemStack, OpenItemDisplayContext.NONE, null);
    }

    public static SkinItemSource create(float renderPriority, ItemStack itemStack, OpenItemDisplayContext transformType) {
        return create(renderPriority, itemStack, transformType, null);
    }

    public static SkinItemSource create(float renderPriority, ItemStack itemStack, OpenItemDisplayContext transformType, @Nullable SkinItemProperties itemProperties) {
        var itemSource = new SkinItemSource();
        itemSource.setItem(itemStack);
        itemSource.setRenderPriority(renderPriority);
        itemSource.setDisplayContext(transformType);
        itemSource.setScale(null);
        itemSource.setRotation(null);
        itemSource.setProperties(itemProperties);
        return itemSource;
    }

    public void setItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItem() {
        return this.itemStack;
    }

    public void setRenderPriority(float renderPriority) {
        this.renderPriority = renderPriority;
    }

    public float getRenderPriority() {
        return renderPriority;
    }

    public void setRotation(Vector3f rotation) {
        this.itemRotation = rotation;
    }

    public Vector3f getRotation() {
        return itemRotation;
    }

    public void setScale(Vector3f scale) {
        this.itemScale = scale;
    }

    public Vector3f getScale() {
        return itemScale;
    }

    public void setDisplayContext(OpenItemDisplayContext itemDisplayContext) {
        this.itemDisplayContext = itemDisplayContext;
    }

    public OpenItemDisplayContext getDisplayContext() {
        return itemDisplayContext;
    }

    public void setProperties(SkinItemProperties itemProperties) {
        this.itemProperties = itemProperties;
    }

    public SkinItemProperties getProperties() {
        return itemProperties;
    }
}
