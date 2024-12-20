package moe.plushie.armourers_workshop.core.client.model;

import moe.plushie.armourers_workshop.api.client.model.IModel;
import moe.plushie.armourers_workshop.api.client.model.IModelBabyPose;
import moe.plushie.armourers_workshop.api.client.model.IModelPart;
import moe.plushie.armourers_workshop.api.data.IAssociatedContainerKey;
import moe.plushie.armourers_workshop.core.data.DataContainer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class LinkedModel implements IModel {

    private final IModel parent;
    private final DataContainer storage = new DataContainer();
    private final HashMap<String, LinkedModelPart> namedParts = new HashMap<>();
    private final ArrayList<LinkedModelPart> allParts = new ArrayList<>();

    private IModel target;

    public LinkedModel(IModel parent) {
        this.parent = parent;
    }

    public void linkTo(IModel target) {
        if (this.target == target) {
            return;
        }
        HashSet<IModelPart> exists = new HashSet<>();
        this.target = target;
        this.allParts.clear();
        // link named parts.
        this.namedParts.forEach((key, value) -> {
            IModelPart part = target.getPart(key);
            value.linkTo(part);
            allParts.add(value);
            exists.add(part);
        });
        // link unnamed parts.
        for (IModelPart part : target.getAllParts()) {
            if (!exists.contains(part)) {
                LinkedModelPart linkedPart = new LinkedModelPart(part);
                linkedPart.linkTo(part);
                allParts.add(linkedPart);
            }
        }
    }

    @Nullable
    @Override
    public IModelBabyPose getBabyPose() {
        if (target != null) {
            return target.getBabyPose();
        }
        if (parent != null) {
            return parent.getBabyPose();
        }
        return null;
    }

    @Override
    public LinkedModelPart getPart(String name) {
        return namedParts.computeIfAbsent(name, it -> {
            IModelPart part = null;
            if (parent != null) {
                part = parent.getPart(name);
            }
            LinkedModelPart linkedPart = new LinkedModelPart(part);
            allParts.add(linkedPart);
            return linkedPart;
        });
    }

    @Override
    public Collection<? extends IModelPart> getAllParts() {
        return allParts;
    }

    @Override
    public Class<?> getType() {
        if (parent != null) {
            return parent.getType();
        }
        return getClass();
    }

    public IModel getParent() {
        return parent;
    }

    @Override
    public <T> T getAssociatedObject(IAssociatedContainerKey<T> key) {
        if (parent != null) {
            return parent.getAssociatedObject(key);
        } else {
            return storage.getAssociatedObject(key);
        }
    }

    @Override
    public <T> void setAssociatedObject(IAssociatedContainerKey<T> key, T value) {
        if (parent != null) {
            parent.setAssociatedObject(key, value);
        } else {
            storage.setAssociatedObject(key, value);
        }
    }
}
