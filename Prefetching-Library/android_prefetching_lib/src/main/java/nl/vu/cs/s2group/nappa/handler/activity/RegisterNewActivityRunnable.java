package nl.vu.cs.s2group.nappa.handler.activity;

import android.util.Log;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java9.util.function.Consumer;

import nl.vu.cs.s2group.nappa.graph.ActivityGraph;
import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.prefetch.PrefetchingStrategy;
import nl.vu.cs.s2group.nappa.room.ActivityData;
import nl.vu.cs.s2group.nappa.room.NappaDB;

/**
 * Defines a Runnable to register a new activity into the database. After inserting
 * the new data to the database, it invokes the handler {@link FetchActivityLiveDataInfoHandler}
 * to register the LiveData objects.
 * <p>
 * If successful, return the new {@link ActivityData} via callback.
 */
public class RegisterNewActivityRunnable implements Runnable {
    private static final String LOG_TAG = RegisterNewActivityRunnable.class.getSimpleName();

    String activityName;
    PrefetchingStrategy strategy;
    ActivityGraph graph;
    Consumer<ActivityData> callback;
    ScheduledFuture<?> liveDataFetcherScheduler;

    public RegisterNewActivityRunnable(String activityName,
                                       PrefetchingStrategy strategy,
                                       ActivityGraph graph,
                                       Consumer<ActivityData> callback) {
        this.activityName = activityName;
        this.strategy = strategy;
        this.graph = graph;
        this.callback = callback;
    }

    @Override
    public void run() {
        long start = new Date().getTime();
        ActivityData activity = new ActivityData(activityName);
        activity.id = NappaDB.getInstance().activityDao().insert(activity);
        ActivityNode node = graph.getCurrent();

        /*
         * When using the app for the first time, the first activity accessed will trigger
         * this runnable to register it. However, due to the implementation design, the
         * current node is set in the graph only after registering the activity. Due to
         * time constraints, this is a quick fix to schedule the LiveData fetching to a
         * later time when the method `graph.getCurrent()` return a non-null object.
         * Since this is a quick fix, we don;t make use of NappaThreadPool class to
         * ensure that this scheduler will be isolated and encourage to refactor this in
         * the future.
         */
        if (node != null) FetchActivityLiveDataInfoHandler.run(node, strategy);
        else {
            liveDataFetcherScheduler = new ScheduledThreadPoolExecutor(1)
                    .scheduleAtFixedRate(
                            () -> {
                                if (graph.getCurrent() != null) {
                                    FetchActivityLiveDataInfoHandler.run(graph.getCurrent(), strategy);
                                    liveDataFetcherScheduler.cancel(false);
                                    Log.d(LOG_TAG, "Current node is now available after " +
                                            (new Date().getTime() - start) +
                                            " ms\n" +
                                            graph.getCurrent());
                                }
                            },
                            0,
                            200,
                            TimeUnit.MILLISECONDS);
        }
        callback.accept(activity);

        Log.d(LOG_TAG, "Activity registered " + activity.activityName + " (#" + activity.id + ")");
    }
}
