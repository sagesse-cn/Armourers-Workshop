package moe.plushie.armourers_workshop.core.client.other.thirdparty;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.compatibility.client.model.AbstractModelHolder;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmatureTransformer;
import moe.plushie.armourers_workshop.core.client.model.LinkedModel;
import moe.plushie.armourers_workshop.core.data.DataContainerKey;
import net.minecraft.client.model.Model;

public class EpicFlightModel extends LinkedModel {

    private static final DataContainerKey<EpicFlightModel> KEY = DataContainerKey.of("EpicFlightModel", EpicFlightModel.class);

    private Object childRef;

    private boolean isValid = false;
    private BakedArmatureTransformer transformer;

    public EpicFlightModel(IModel parent) {
        super(parent);
    }

    public static <V extends Model> EpicFlightModel ofNullable(V model) {
        var model1 = AbstractModelHolder.ofNullable(model);
        if (model1 == null) {
            return null;
        }
        var model2 = model1.getAssociatedObject(KEY);
        if (model2 != null) {
            return model2;
        }
        model2 = new EpicFlightModel(model1);
        model1.setAssociatedObject(KEY, model2);
        return model2;
    }

    public void linkTo(Object mesh) {
        if (childRef != mesh) {
            childRef = mesh;
            linkTo(EpicFlightModelTransformer.create(mesh));
        }
    }

    public void setTransformer(BakedArmatureTransformer transformer) {
        this.transformer = transformer;
    }

    public BakedArmatureTransformer getTransformer() {
        return transformer;
    }

    public void setInvalid(boolean valid) {
        isValid = valid;
    }

    public boolean isInvalid() {
        return isValid;
    }
}
