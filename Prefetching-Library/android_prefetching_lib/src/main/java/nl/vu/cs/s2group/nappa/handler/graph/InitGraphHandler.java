package nl.vu.cs.s2group.nappa.handler.graph;

import java.util.List;
import java9.util.function.Consumer;

import nl.vu.cs.s2group.nappa.graph.ActivityGraph;
import nl.vu.cs.s2group.nappa.prefetch.PrefetchingStrategy;
import nl.vu.cs.s2group.nappa.room.ActivityData;
import nl.vu.cs.s2group.nappa.util.NappaThreadPool;

/**
 * Defines a Handler to initialize the ENG graph for a new session.
 */
public class InitGraphHandler {
    /**
     * Execute the handler
     *
     * @param strategy                    The strategy selected for this session
     * @param callbackOnFetchedActivities A callback with a list of known activities
     * @param callbackOnInitializedGraph  A callback with the initialized graph
     */
    public static void run(PrefetchingStrategy strategy,
                           Consumer<List<ActivityData>> callbackOnFetchedActivities,
                           Consumer<ActivityGraph> callbackOnInitializedGraph) {
        NappaThreadPool.submit(new InitGraphRunnable(strategy, callbackOnFetchedActivities, callbackOnInitializedGraph));
    }
}
