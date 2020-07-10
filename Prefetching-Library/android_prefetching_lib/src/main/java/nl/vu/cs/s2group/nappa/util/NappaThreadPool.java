package nl.vu.cs.s2group.nappa.util;

import android.util.Log;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Provides a single shared instance of a thread pool responsible for scheduling
 * commands to run outside the main thread or after some delay.
 */
public final class NappaThreadPool {
    private static final String LOG_TAG = NappaThreadPool.class.getSimpleName();
    private static final ScheduledThreadPoolExecutor scheduler;

    /**
     * Defines the number of threads to keep in the pool, even if they are idle.
     * With the Handler/Runnable design, we might have more than 1 threat at a
     * tme. Furthermore, to log the Exceptions we need to use the method {@link
     * ScheduledThreadPoolExecutor#submit(Runnable)} instead of directly using
     * the method {@link ScheduledThreadPoolExecutor#execute(Runnable)}. The
     * submit method would freeze the computation waiting forever on {@link
     * Future#get()}.
     * <p>
     * A possibility would be to set a timeout using the method
     * {@link Future#get(long, TimeUnit)} but if the runnable fails to run,
     * the library might break as a result of failing to complete an action.
     * <p>
     * Another option would be run the runnable on the same thread if we are not
     * running on the main thread
     * <p>
     * The current solution was to increase the core pool size.
     */
    private static final int CORE_POOL_SIZE = 2;

    static {
        scheduler = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE);
    }

    private NappaThreadPool() {
        throw new IllegalStateException("NappaThreadPool is a utility class and should not be instantiated!");
    }

    /**
     * This method ensures that we handle in the main thread the exceptions that occurs in
     * the worker thread
     *
     * @param task The task to run in the worker thread
     */
    public static void submit(Runnable task) {
        Future<?> future = scheduler.submit(task);
        try {
            future.get();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception caught on worker thread", e);
        }
    }

}
