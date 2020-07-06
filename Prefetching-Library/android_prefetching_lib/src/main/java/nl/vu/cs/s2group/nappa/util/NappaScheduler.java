package nl.vu.cs.s2group.nappa.util;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Provides a single shared instance of the scheduler responsible for scheduling
 * commands to run outside the main thread or after some delay.
 */
public final class NappaScheduler {
    public static final ScheduledThreadPoolExecutor scheduler;

    static {
        scheduler = new ScheduledThreadPoolExecutor(1);
    }

    private NappaScheduler() {
        throw new IllegalStateException("NappaScheduler is a utility class and should not be instantiated!");
    }

}
