package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.armature.IJoint;
import moe.plushie.armourers_workshop.core.armature.Armatures;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;

import java.util.HashSet;
import java.util.Map;

public class BakedFirstPersonArmature extends BakedArmature {

    private static final BakedFirstPersonArmature DEFAULT = new BakedFirstPersonArmature();
    private static final Map<OpenItemDisplayContext, BakedFirstPersonArmature> VARIANTS = Collections.immutableMap(builder -> {
        builder.put(OpenItemDisplayContext.FIRST_PERSON_LEFT_HAND, new BakedFirstPersonArmature("Arm_L", "Hand_L"));
        builder.put(OpenItemDisplayContext.FIRST_PERSON_RIGHT_HAND, new BakedFirstPersonArmature("Arm_R", "Hand_R"));
    });

    private final HashSet<String> jointNames;

    public BakedFirstPersonArmature(String... names) {
        super(Armatures.HAND);
        this.jointNames = Collections.newSet(names);
    }

    public static BakedFirstPersonArmature defaultBy(OpenItemDisplayContext transformType) {
        return VARIANTS.getOrDefault(transformType, DEFAULT);
    }

    @Override
    public IJoint getJoint(SkinPartType partType) {
        var joint = super.getJoint(partType);
        if (joint != null && !jointNames.isEmpty() && !jointNames.contains(joint.getName())) {
            return null;
        }
        return joint;
    }
}
