package nl.vu.cs.s2group.nappa.util;

import android.util.Log;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Provides a single shared instance of a thread pool responsible for scheduling
 * commands to run outside the main thread or after some delay.
 */
public final class NappaThreadPool {
    private static final String LOG_TAG = NappaThreadPool.class.getSimpleName();
    private static final ScheduledThreadPoolExecutor scheduler;

    static {
        scheduler = new ScheduledThreadPoolExecutor(1);
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
