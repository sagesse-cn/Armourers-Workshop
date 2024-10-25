package moe.plushie.armourers_workshop.core.client.other;

import com.apple.library.uikit.UIColor;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.math.Vector3f;
import moe.plushie.armourers_workshop.core.skin.paint.SkinPaintScheme;

public interface ConcurrentBufferBuilder {

    void addPart(BakedSkinPart part, BakedSkin skin, SkinPaintScheme scheme, ConcurrentRenderingContext context);

    void addShape(Vector3f origin, ConcurrentRenderingContext context);

    void addShape(OpenVoxelShape shape, UIColor color, ConcurrentRenderingContext context);

    void addShape(BakedArmature armature, ConcurrentRenderingContext context);
}
