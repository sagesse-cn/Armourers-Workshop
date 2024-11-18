package moe.plushie.armourers_workshop.core.skin.animation.molang.core;

public class NamedObject {

    private String name;
    private NamedObject owner;

    public void setName(String name, NamedObject owner) {
        this.name = name;
        this.owner = owner;
    }

    public String getName() {
        // this is root node.
        if (owner == null) {
            return name;
        }
        // the parent not name.
        var parentName = owner.getName();
        if (parentName == null) {
            return name;
        }
        return parentName + "." + name;
    }

    @Override
    public String toString() {
        var name = getName();
        if (name != null) {
            return name;
        }
        return "<unnamed>";
    }
}
