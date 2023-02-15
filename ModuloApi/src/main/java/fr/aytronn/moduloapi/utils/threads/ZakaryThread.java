package fr.aytronn.moduloapi.utils.threads;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author HookWoods
 */
public enum ZakaryThread {

    TASK_THREAD("Task Thread", true, Thread.NORM_PRIORITY, 1),
    FILE_EXECUTOR("File I/O Thread", true, Thread.NORM_PRIORITY + 2, 2);

    private final ExecutorService executor;

    ZakaryThread(String name, boolean daemon, int priority, int cores) {
        if (cores <= 1) {
            this.executor = Executors.newSingleThreadScheduledExecutor(new ZakaryThreadClass(name, daemon, priority));
        } else {
            this.executor = new ScheduledThreadPoolExecutor(cores, new ZakaryThreadClass(name, daemon, priority));
        }
    }

    /**
     * Get the executor service
     *
     * @return The executor service
     */
    public ExecutorService get() {
        return this.executor;
    }

    /**
     * Stop the scheduled executor service
     */
    public void stop() {
        this.executor.shutdown();
    }

    /**
     * Submit a task
     *
     * @param runnable The task to submit
     *
     * @return The future of the task
     */
    public Future<?> submit(Runnable runnable) {
        return this.executor.submit(runnable);
    }

    /**
     * Submit a task
     *
     * @param runnable The task to submit
     *
     * @return The future of the task
     */
    public Future<?> submit(Callable<?> runnable) {
        return this.executor.submit(runnable);
    }

    /**
     * Execute a task
     *
     * @param runnable The task to execute
     */
    public void execute(Runnable runnable) {
        this.executor.execute(runnable);
    }

    /**
     * Schedule a task to be executed after a delay
     *
     * @param cmd      The task to execute
     * @param time     The delay
     * @param timeUnit The time unit
     *
     * @return The future of the task
     */
    public ScheduledFuture<?> scheduleLater(Runnable cmd, long time, TimeUnit timeUnit) {
        return ((ScheduledExecutorService) this.executor).schedule(cmd, time, timeUnit);
    }

    /**
     * Schedule a task to be executed repeatedly
     *
     * @param cmd      The task to execute
     * @param initial  The initial delay
     * @param time     The delay
     * @param timeUnit The time unit
     *
     * @return The future of the task
     */
    public ScheduledFuture<?> scheduleRepeated(Runnable cmd, long initial, long time, TimeUnit timeUnit) {
        return ((ScheduledExecutorService) this.executor).scheduleAtFixedRate(cmd, initial, time, timeUnit);
    }

    /**
     * Schedule a task to be executed after a delay
     *
     * @param cmd      The task to execute
     * @param initial  The initial delay
     * @param time     The delay
     * @param timeUnit The time unit
     *
     * @return The future of the task
     */
    public ScheduledFuture<?> scheduleDelay(Runnable cmd, long initial, long time, TimeUnit timeUnit) {
        return ((ScheduledExecutorService) this.executor).scheduleWithFixedDelay(cmd, initial, time, timeUnit);
    }

    /**
     * Shutdown the thread
     */
    public void shutdown() {
        this.executor.shutdown();
        while (!this.executor.isTerminated()) {
            try {
                if (!this.executor.awaitTermination(1L, TimeUnit.MINUTES)) {
                    this.executor.shutdownNow();
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * Shutdown all threads
     */
    public static void shutdownAll() {
        for (final ZakaryThread thread : values()) {
            thread.shutdown();
        }
    }

}
