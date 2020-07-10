package nl.vu.cs.s2group.nappa.handler.graph;

import java.util.List;
import java.util.function.Consumer;

import nl.vu.cs.s2group.nappa.graph.ActivityGraph;
import nl.vu.cs.s2group.nappa.prefetch.PrefetchingStrategy;
import nl.vu.cs.s2group.nappa.room.ActivityData;
import nl.vu.cs.s2group.nappa.util.NappaThreadPool;

public class InitGraphHandler {
    public static void run(PrefetchingStrategy strategy,
                           Consumer<List<ActivityData>> callbackOnFetchedActivities,
                           Consumer<ActivityGraph> callbackOnInitializedGraph) {
        NappaThreadPool.submit(new InitGraphRunnable(strategy, callbackOnFetchedActivities, callbackOnInitializedGraph));
    }
}
