package com.victorgponce.permadeath_mod.util.tick_counter;

/**
 * A utility class that counts down ticks and executes a task when the countdown reaches zero.
 */
public class TickCounter {
    private int ticksRemaining;
    private final Runnable task;

    /**
     * Constructs a TickCounter with the specified number of ticks and a task to execute.
     *
     * @param ticks The number of ticks to count down.
     * @param task  The task to execute when the countdown reaches zero.
     */
    public TickCounter(int ticks, Runnable task) {
        this.ticksRemaining = ticks;
        this.task = task;
    }

    /**
     * Ticks down the counter and executes the task if the countdown reaches zero.
     *
     * @return true if the task was executed, false otherwise.
     */
    public boolean tick() {
        if (--ticksRemaining <= 0) {
            task.run();
            return true; // The task has been executed
        }
        return false;
    }
}