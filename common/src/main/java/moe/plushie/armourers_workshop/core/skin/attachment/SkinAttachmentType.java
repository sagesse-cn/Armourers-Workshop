package moe.plushie.armourers_workshop.core.skin.attachment;

import moe.plushie.armourers_workshop.api.core.IRegistryEntry;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.core.math.Vector3f;

import java.util.Objects;

public class SkinAttachmentType implements IRegistryEntry {

    private IResourceLocation registryName;

    public void setRegistryName(IResourceLocation registryName) {
        this.registryName = registryName;
    }

    @Override
    public IResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkinAttachmentType that)) return false;
        return Objects.equals(registryName, that.registryName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(registryName);
    }

    @Override
    public String toString() {
        return registryName.toString();
    }
}
