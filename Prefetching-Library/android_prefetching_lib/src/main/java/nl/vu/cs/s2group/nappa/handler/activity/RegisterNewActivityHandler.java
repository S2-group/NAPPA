package nl.vu.cs.s2group.nappa.handler.activity;

import java.util.function.Consumer;

import nl.vu.cs.s2group.nappa.graph.ActivityGraph;
import nl.vu.cs.s2group.nappa.prefetch.PrefetchingStrategy;
import nl.vu.cs.s2group.nappa.room.ActivityData;
import nl.vu.cs.s2group.nappa.util.NappaThreadPool;

/**
 * Defines a Handler to register a new activity unknown to Nappa.  If successful, the
 * registered session is returned via the callback.
 */
public class RegisterNewActivityHandler {
    /**
     * Execute the handler
     *
     * @param activityName The name of the activity to register
     * @param strategy     The strategy selected for this session
     * @param graph        The ENG graph
     * @param callback     A callback with the registered activity
     */
    public static void run(String activityName,
                           PrefetchingStrategy strategy,
                           ActivityGraph graph,
                           Consumer<ActivityData> callback) {
        NappaThreadPool.submit(new RegisterNewActivityRunnable(activityName, strategy, graph, callback));
    }
}
