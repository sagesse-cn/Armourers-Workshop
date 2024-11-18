package moe.plushie.armourers_workshop.core.data;

import java.util.ArrayList;
import java.util.List;

public class EntityActionTarget {

    private final float priority;
    private final String name;
    private final List<EntityAction> actions;
    private final float transitionDuration;
    private final int playCount;

    public EntityActionTarget(String name, float priority, List<EntityAction> actions, float transitionDuration, int playCount) {
        this.priority = priority;
        this.name = name;
        this.actions = actions;
        this.transitionDuration = transitionDuration;
        this.playCount = playCount;
    }

    public String getName() {
        return name;
    }

    public float getPriority() {
        return priority;
    }

    public List<EntityAction> getActions() {
        return actions;
    }

    public float getTransitionDuration() {
        return transitionDuration;
    }

    public int getPlayCount() {
        return playCount;
    }

    public static class Builder {

        private ArrayList<EntityAction> actions = new ArrayList<>();
        private float priority = 0;
        private float transitionDuration = 0.1f;
        private int playCount = 0;

        public Builder action(EntityAction action) {
            this.actions.add(action);
            return this;
        }

        public Builder priority(float priority) {
            this.priority = priority;
            return this;
        }

        public Builder repeat(int playCount) {
            this.playCount = playCount;
            return this;
        }

        public Builder transition(float transitionDuration) {
            this.transitionDuration = transitionDuration;
            return this;
        }

        public EntityActionTarget build(String name) {
            return new EntityActionTarget(name, priority, actions, transitionDuration, playCount);
        }
    }
}
