package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerBuilder;
import moe.plushie.armourers_workshop.core.armature.JointModifier;

public class DefaultArmatureTransformerBuilder extends ArmatureTransformerBuilder {

    public DefaultArmatureTransformerBuilder(IResourceLocation name) {
        super(name);
    }

    @Override
    protected JointModifier buildJointTarget(String name) {
        return new DefaultJointBinder(name);
    }
}
