package moe.plushie.armourers_workshop.core.client.bake;

import com.google.common.collect.Range;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import moe.plushie.armourers_workshop.api.client.IBakedSkin;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.part.features.ICanUse;
import moe.plushie.armourers_workshop.core.client.animation.AnimationContext;
import moe.plushie.armourers_workshop.core.client.animation.AnimationController;
import moe.plushie.armourers_workshop.core.client.animation.AnimationEngine;
import moe.plushie.armourers_workshop.core.client.model.ItemTransform;
import moe.plushie.armourers_workshop.core.client.other.PlaceholderManager;
import moe.plushie.armourers_workshop.core.client.other.SkinItemSource;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderContext;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderHelper;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRenderer;
import moe.plushie.armourers_workshop.core.client.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.core.data.cache.PrimaryKey;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenQuaternion3f;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.math.Rectangle3f;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.math.Vector3i;
import moe.plushie.armourers_workshop.core.math.Vector4f;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.paint.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.skin.part.wings.WingPartTransform;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinUsedCounter;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.core.utils.OpenSequenceSource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class BakedSkin implements IBakedSkin {

    private final int id = OpenSequenceSource.nextInt(BakedSkinPart.class);

    private final String identifier;
    private final Skin skin;
    private final ISkinType skinType;
    private final HashMap<Object, Rectangle3f> cachedBounds = new HashMap<>();
    private final HashMap<Vector3i, Rectangle3f> cachedBlockBounds = new HashMap<>();

    private final ArrayList<WingPartTransform> cachedWingsTransforms = new ArrayList<>();
    private final ArrayList<BakedLocatorTransform> cachedLocatorTransforms = new ArrayList<>();

    private final Range<Integer> useTickRange;
    private final List<BakedSkinPart> skinParts;

    private final AnimationContext animationContext;
    private final List<AnimationController> animationControllers;

    private final ColorDescriptor colorDescriptor;
    private final SkinUsedCounter usedCounter;

    private final BakedItemTransform itemTransform;

    private final SkinPaintScheme paintScheme;
    private final Int2ObjectMap<SkinPaintScheme> resolvedColorSchemes = new Int2ObjectOpenHashMap<>();

    public BakedSkin(String identifier, ISkinType skinType, ArrayList<BakedSkinPart> bakedParts, Skin skin, SkinPaintScheme paintScheme, ColorDescriptor colorDescriptor, SkinUsedCounter usedCounter) {
        this.identifier = identifier;
        this.skin = skin;
        this.skinType = skinType;
        this.animationControllers = resolveAnimationControllers(bakedParts, skin.getAnimations(), skin.getProperties());
        this.animationContext = resolveAnimationContext(bakedParts);
        this.skinParts = BakedSkinPartCombiner.apply(bakedParts); // depends `resolveAnimationControllers`
        this.paintScheme = paintScheme;
        this.colorDescriptor = colorDescriptor;
        this.usedCounter = usedCounter;
        this.useTickRange = getUseTickRange(skinParts);
        this.itemTransform = resolvedItemTransform(skinParts, skin);
        this.loadBlockBounds(skinParts);
        this.loadPartTransforms(skinParts);
    }

    public void setupAnim(Entity entity, BakedArmature bakedArmature, SkinRenderContext context) {
        cachedWingsTransforms.forEach(it -> it.setup(entity, context.getAnimationTicks()));
        AnimationEngine.apply(entity, this, context);
        SkinRenderHelper.apply(entity, this, bakedArmature, context.getItemSource());
        cachedLocatorTransforms.forEach(it -> it.setup(entity, bakedArmature, context));
    }

    public SkinPaintScheme resolve(Entity entity, SkinPaintScheme scheme) {
        if (colorDescriptor.isEmpty()) {
            return SkinPaintScheme.EMPTY;
        }
        var resolvedColorScheme = resolvedColorSchemes.computeIfAbsent(entity.getId(), k -> paintScheme.copy());
        // we can't bind textures to skin when the item stack rendering.
        if (PlaceholderManager.isPlaceholder(entity)) {
            var resolvedTexture = PlayerTextureLoader.getInstance().getTextureLocation(entity);
            if (!Objects.equals(resolvedColorScheme.getTexture(), resolvedTexture)) {
                resolvedColorScheme.setTexture(resolvedTexture);
            }
        }
        resolvedColorScheme.setReference(scheme);
        return resolvedColorScheme;
    }

    public int getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Skin getSkin() {
        return skin;
    }

    public ISkinType getType() {
        return skinType;
    }

    public List<BakedSkinPart> getParts() {
        return skinParts;
    }

    public SkinProperties getProperties() {
        return skin.getProperties();
    }

    public AnimationContext getAnimationContext() {
        return animationContext;
    }

    public List<AnimationController> getAnimationControllers() {
        return animationControllers;
    }

    public SkinPaintScheme getPaintScheme() {
        return paintScheme;
    }

    public ColorDescriptor getColorDescriptor() {
        return colorDescriptor;
    }

    public BakedItemTransform getItemTransform() {
        return itemTransform;
    }

    public Range<Integer> getUseTickRange() {
        return useTickRange;
    }

    public SkinUsedCounter getUsedCounter() {
        return usedCounter;
    }

    public Map<Vector3i, Rectangle3f> getBlockBounds() {
        return cachedBlockBounds;
    }

    public Rectangle3f getRenderBounds() {
        return getRenderBounds(ItemTransform.NO_TRANSFORM, OpenItemDisplayContext.NONE);
    }

    public Rectangle3f getRenderBounds(ItemTransform itemTransform, OpenItemDisplayContext displayContext) {
        var rotation = itemTransform.getRotation();
        var key = PrimaryKey.of(rotation, displayContext);
        var bounds = cachedBounds.get(key);
        if (bounds != null) {
            return bounds;
        }
        var entity = PlaceholderManager.MANNEQUIN.get();
        var matrix = OpenMatrix4f.createScaleMatrix(1, 1, 1);
        var shape = getRenderShape(entity, BakedArmature.defaultBy(skinType), displayContext);
        if (!rotation.equals(Vector3f.ZERO)) {
            matrix.rotate(new OpenQuaternion3f(rotation.getX(), rotation.getY(), rotation.getZ(), true));
            shape.mul(matrix);
        }
        bounds = shape.bounds().copy();
        if (!rotation.equals(Vector3f.ZERO)) {
            var center = new Vector4f(bounds.getCenter());
            matrix.invert();
            center.transform(matrix);
            bounds.setX(center.getX() - bounds.getWidth() / 2);
            bounds.setY(center.getY() - bounds.getHeight() / 2);
            bounds.setZ(center.getZ() - bounds.getDepth() / 2);
        }
        cachedBounds.put(key.copy(), bounds);
        return bounds;
    }

    private OpenVoxelShape getRenderShape(Entity entity, BakedArmature armature, OpenItemDisplayContext displayContext) {
        if (armature == null) {
            return OpenVoxelShape.empty();
        }
        var context = new SkinRenderContext();
        context.setItemSource(SkinItemSource.EMPTY);
        context.setDisplayContext(displayContext);
        context.setAnimationTicks(0);
        //context.setTransforms(entity, model);
        setupAnim(entity, armature, context);
        return SkinRenderer.getShape(entity, armature, this, context.pose());
    }

    private void loadPartTransforms(List<BakedSkinPart> skinParts) {
        // search all transform
        skinParts.forEach(it -> it.getTransform().getChildren().forEach(transform -> {
            if (transform instanceof WingPartTransform transform1) {
                cachedWingsTransforms.add(transform1);
            }
        }));
        // attach locator transform.
        cachedLocatorTransforms.addAll(BakedLocatorTransform.create(skinParts));
    }

    private void loadBlockBounds(List<BakedSkinPart> skinParts) {
        if (skinType != SkinTypes.BLOCK) {
            return;
        }
        for (var skinPart : skinParts) {
            var bounds = skinPart.getPart().getBlockBounds();
            if (bounds != null) {
                cachedBlockBounds.putAll(bounds);
            }
        }
    }

    private Range<Integer> getUseTickRange(List<BakedSkinPart> skinParts) {
        int count = 0;
        int maxUseTick = Integer.MIN_VALUE;
        int minUseTick = Integer.MAX_VALUE;
        for (var bakedPart : skinParts) {
            if (bakedPart.getType() instanceof ICanUse partType) {
                var range = partType.getUseRange();
                maxUseTick = Math.max(maxUseTick, range.upperEndpoint());
                minUseTick = Math.min(minUseTick, range.lowerEndpoint());
                count += 1;
            }
        }
        if (count == 0) {
            return Range.closed(0, 0);
        }
        return Range.closed(minUseTick, maxUseTick);
    }

    private BakedItemTransform resolvedItemTransform(List<BakedSkinPart> skinParts, Skin skin) {
        var itemTransforms = skin.getItemTransforms();
        return BakedItemTransform.create(skinParts, itemTransforms, skin.getType());
    }

    private List<AnimationController> resolveAnimationControllers(List<BakedSkinPart> skinParts, Collection<SkinAnimation> animations, SkinProperties properties) {
        var results = new ArrayList<AnimationController>();
        if (animations.isEmpty()) {
            return results;
        }
        var namedParts = new HashMap<String, SkinPartTransform>();
        Collections.eachTree(skinParts, BakedSkinPart::getChildren, part -> {
            var partType = part.getType();
            var partName = partType.getName();
            if (partType == SkinPartTypes.ADVANCED) {
                partName = part.getName();
            }
            namedParts.put(partName, part.getTransform());
        });
        animations.forEach(animation -> {
            var controller = new AnimationController(animation, namedParts);
            results.add(controller);
        });
        results.removeIf(AnimationController::isEmpty);
        return results;
    }

    private AnimationContext resolveAnimationContext(List<BakedSkinPart> skinParts) {
        // find all animated transform and add into context.
        var builder = new AnimationContext.Builder();
        Collections.eachTree(skinParts, BakedSkinPart::getChildren, it -> builder.add(it.getTransform()));
        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BakedSkin that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "id", id, "skin", identifier, "type", skinType);
    }
}
