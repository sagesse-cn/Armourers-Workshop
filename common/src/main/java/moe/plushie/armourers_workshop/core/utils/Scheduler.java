package moe.plushie.armourers_workshop.core.utils;

import java.util.List;

public class Scheduler {

    public static final Scheduler SERVER = new Scheduler();
    public static final Scheduler CLIENT = new Scheduler();

    private List<Runnable> nextTasks;


    public void begin() {
        // ignore when no task
        if (nextTasks == null) {
            return;
        }
        var tasks = nextTasks;
        nextTasks = null;
        tasks.forEach(Runnable::run);
    }

    public void next(Runnable handler) {
        if (nextTasks == null) {
            nextTasks = Collections.newList(handler);
        } else {
            nextTasks.add(handler);
        }
    }

    public void end() {
    }
}
