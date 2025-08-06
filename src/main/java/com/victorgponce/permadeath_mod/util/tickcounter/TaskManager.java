package com.victorgponce.permadeath_mod.util.tickcounter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A utility class that manages a list of tasks that can be executed on each tick.
 * It allows adding tasks and ticking through them, executing each task when its countdown reaches zero.
 */
public class TaskManager {
    private static final List<TickCounter> ACTIVE_TASKS = new ArrayList<>();

    /**
     * Adds a new task to the task manager.
     *
     * @param task The TickCounter task to be added.
     */
    public static void addTask(TickCounter task) {
        ACTIVE_TASKS.add(task);
    }

    /**
     * Ticks through all active tasks, executing each one when its countdown reaches zero.
     * This method should be called on each game tick.
     */
    public static void tick() {
        Iterator<TickCounter> iterator = ACTIVE_TASKS.iterator();
        while (iterator.hasNext()) {
            TickCounter task = iterator.next();
            if (task.tick()) {
                iterator.remove(); // Remove the task if it has been executed
            }
        }
    }
}