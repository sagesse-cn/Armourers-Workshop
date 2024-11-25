package moe.plushie.armourers_workshop.core.client.bake;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.core.client.model.ItemModel;
import moe.plushie.armourers_workshop.core.client.model.ItemModelManager;
import moe.plushie.armourers_workshop.core.client.model.ItemOverride;
import moe.plushie.armourers_workshop.core.client.model.ItemTransform;
import moe.plushie.armourers_workshop.core.client.other.SkinItemProperties;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.math.OpenTransform3f;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.OpenItemTransforms;
import moe.plushie.armourers_workshop.init.ModDebugger;
import moe.plushie.armourers_workshop.utils.ShapeTesselator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Environment(EnvType.CLIENT)
public abstract class BakedItemTransform {

    protected final ItemModel itemModel;
    protected final OpenTransform3f offsetTransform;

    protected BakedItemTransform(ItemModel itemModel, OpenTransform3f offsetTransform) {
        this.itemModel = itemModel;
        this.offsetTransform = offsetTransform;
    }

    public static BakedItemTransform create(List<BakedSkinPart> skinParts, OpenItemTransforms itemTransforms, ISkinType skinType) {
        // ..
        if (itemTransforms == null) {
            var itemModel = ItemModelManager.getInstance().getModel(skinType);
            return new Builtin(itemModel, null);
        }
        return create(skinParts, itemTransforms);
    }

    private static BakedItemTransform create(List<BakedSkinPart> skinParts, OpenItemTransforms itemTransforms) {
        var transforms = new EnumMap<OpenItemDisplayContext, ItemTransform>(OpenItemDisplayContext.class);
        for (var value : OpenItemDisplayContext.values()) {
            var itemTransform = itemTransforms.get(value);
            if (itemTransform != null) {
                transforms.put(value, ItemTransform.create(itemTransform));
            }
        }
        var overrides = new ArrayList<ItemOverride>();
        for (var part : skinParts) {
            //overrides.addAll(SkinUtils.getItemOverrides(part.getType()));
            // we need search child part?
            //

        }
        var itemModel = new ItemModel(null, overrides, transforms);
        return new Custom(itemModel, itemTransforms.getOffset());
    }

    public void apply(IPoseStack poseStack, @Nullable Entity entity, BakedSkin skin, SkinRenderContext context) {
        var itemSource = context.getItemSource();
        var itemModel = resolve(entity, itemSource.getItem(), itemSource.getProperties());
        if (itemModel == null) {
            return; // can't found a item model, ignore.
        }

        if (ModDebugger.targetBounds) {
            var tesselator = AbstractBufferSource.tesselator();
            ShapeTesselator.vector(0, 0, 0, 16, 16, 16, poseStack, tesselator);
            ShapeTesselator.stroke(-8, -8, -8, 8, 8, 8, UIColor.CYAN, poseStack, tesselator);
            tesselator.endBatch();
        }

        var displayContext = itemSource.getDisplayContext();
        var itemTransform = itemModel.getTransform(displayContext);
        applyItemTransform(itemTransform, displayContext, poseStack);

        if (ModDebugger.targetBounds) {
            var tesselator = AbstractBufferSource.tesselator();
            ShapeTesselator.vector(0, 0, 0, 16, 16, 16, poseStack, tesselator);
            ShapeTesselator.stroke(-8, -8, -8, 8, 8, 8, UIColor.YELLOW, poseStack, tesselator);
            tesselator.endBatch();
        }

        var displayBox = context.getDisplayBox();
        if (displayBox != null) {
            applyScaleInBox(itemTransform, displayContext, skin, displayBox, poseStack);
        }
    }

    protected void applyItemTransform(ItemTransform itemTransform, OpenItemDisplayContext displayContext, IPoseStack poseStack) {
        // apply left item transform.
        itemTransform.apply(displayContext.isLeftHand(), poseStack);
        ModDebugger.translate(poseStack);
        ModDebugger.rotate(poseStack);
        // apply right item transform.
        if (offsetTransform != null) {
            offsetTransform.apply(poseStack);
        }
    }

    protected void applyScaleInBox(ItemTransform itemTransform, OpenItemDisplayContext displayContext, BakedSkin skin, Vector3f displayBox, IPoseStack poseStack) {
        var renderBounds = skin.getRenderBounds(itemTransform, displayContext);
        // calculate and apply skin scale.
        float dx = displayBox.getX() * 16;
        float dy = displayBox.getY() * 16;
        float dz = displayBox.getZ() * 16;
        float scale = Math.min(Math.min(dx / renderBounds.getWidth(), dy / renderBounds.getHeight()), dz / renderBounds.getDepth());
        //poseStack.scale(scale / scale.getX(), scale / scale.getY(), scale / scale.getZ());
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-renderBounds.getMidX(), -renderBounds.getMidY(), -renderBounds.getMidZ());
    }

    protected ItemModel resolve(Entity entity, ItemStack itemStack, SkinItemProperties itemProperties) {
        // not provided!
        if (entity == null) {
            return null;
        }
        // in some cases we need to disable item overrides, users:
        //  Epic Fight Mod (Shield Render)
        if (itemProperties != null && !itemProperties.isAllowOverrides()) {
            return itemModel;
        }
        return itemModel.resolve(itemStack, entity, entity.getLevel(), 0);
    }

    private static class Builtin extends BakedItemTransform {

        protected Builtin(ItemModel itemModel, OpenTransform3f offsetTransform) {
            super(itemModel, offsetTransform);
        }
    }

    private static class Custom extends BakedItemTransform {

        protected Custom(ItemModel itemModel, OpenTransform3f afterTransform) {
            super(itemModel, afterTransform);
        }

        @Override
        protected void applyItemTransform(ItemTransform itemTransform, OpenItemDisplayContext displayContext, IPoseStack poseStack) {
            // in normal case will be provided by the custom item transforms.
            if (displayContext != OpenItemDisplayContext.NONE) {
                super.applyItemTransform(itemTransform, displayContext, poseStack);
            }
        }

        @Override
        protected void applyScaleInBox(ItemTransform itemTransform, OpenItemDisplayContext displayContext, BakedSkin skin, Vector3f displayBox, IPoseStack poseStack) {
            // in the none case, we need render it in display box inside if specified.
            if (displayContext == OpenItemDisplayContext.NONE) {
                super.applyScaleInBox(itemTransform, displayContext, skin, displayBox, poseStack);
            }
        }
    }
}
