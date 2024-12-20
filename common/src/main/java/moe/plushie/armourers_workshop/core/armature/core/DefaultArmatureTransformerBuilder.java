package moe.plushie.armourers_workshop.core.armature.core;

import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.armature.ArmatureTransformerBuilder;
import moe.plushie.armourers_workshop.core.armature.JointModifier;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;

public class DefaultArmatureTransformerBuilder extends ArmatureTransformerBuilder {

    public DefaultArmatureTransformerBuilder(IResourceLocation name) {
        super(name);
    }

    @Override
    protected JointModifier buildJointTarget(String name, IODataObject parameters) {
        return new DefaultJointBinder(name, parameters);
    }
}
