package nl.vu.cs.s2group.nappa.handler.graph;

import java.util.List;
import java.util.function.Consumer;

import nl.vu.cs.s2group.nappa.graph.ActivityGraph;
import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.handler.activity.FetchActivityLiveDataInfoHandler;
import nl.vu.cs.s2group.nappa.prefetch.PrefetchingStrategy;
import nl.vu.cs.s2group.nappa.room.ActivityData;
import nl.vu.cs.s2group.nappa.room.NappaDB;

/**
 * Defines a Runnable to initialize the ENG graph. This runnable fetches all known
 * activities registered in the database, inserts a {@link ActivityNode} in the
 * {@link ActivityGraph} for each fetched activity and invokes the handler {@link
 * FetchActivityLiveDataInfoHandler} to register LiveData objects.
 * <p>
 * If successful, the list of fetches activities and the initialized graph are returned
 * via callbacks.
 */
public class InitGraphRunnable implements Runnable {
    PrefetchingStrategy strategy;
    Consumer<List<ActivityData>> callbackOnFetchedActivities;
    Consumer<ActivityGraph> callbackOnInitializedGraph;

    public InitGraphRunnable(PrefetchingStrategy strategy,
                             Consumer<List<ActivityData>> callbackOnFetchedActivities,
                             Consumer<ActivityGraph> callbackOnInitializedGraph) {
        this.strategy = strategy;
        this.callbackOnFetchedActivities = callbackOnFetchedActivities;
        this.callbackOnInitializedGraph = callbackOnInitializedGraph;
    }

    @Override
    public void run() {
        List<ActivityData> activities = NappaDB.getInstance()
                .activityDao()
                .getListActivity();

        callbackOnFetchedActivities.accept(activities);

        ActivityGraph graph = new ActivityGraph();
        for (ActivityData activity : activities) {
            ActivityNode node = graph.initNode(activity.activityName);
            FetchActivityLiveDataInfoHandler.run(node, strategy);
        }

        callbackOnInitializedGraph.accept(graph);
    }
}
