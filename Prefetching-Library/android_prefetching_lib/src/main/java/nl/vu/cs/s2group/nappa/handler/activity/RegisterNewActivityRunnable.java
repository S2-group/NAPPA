package nl.vu.cs.s2group.nappa.handler.activity;

import android.util.Log;

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

        ActivityData activity = new ActivityData(activityName);
        activity.id = NappaDB.getInstance().activityDao().insert(activity);
        callback.accept(activity);

        Log.d(LOG_TAG, String.format("Activity registered %s (#%d)", activity.activityName, activity.id));

        /*
         * Due to how the graph flow is designed, when registering the activity its node will often
         * not be available when executing this line. Therefore, we schedule to trigger the handler
         * to fetch and assign the node LiveData at a later point in time.
         */

        long start = System.currentTimeMillis();
        liveDataFetcherScheduler = new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(() -> {
                    ActivityNode node = graph.getByName(activityName);

                    // The have not been registered in the graph yet
                    if (node == null) return;

                    liveDataFetcherScheduler.cancel(false);
                    FetchActivityLiveDataInfoHandler.run(node, strategy);
                    Log.d(LOG_TAG, String.format("LiveData for node %s was triggered after %d ms", node.getActivitySimpleName(), (System.currentTimeMillis() - start)));

                },
                50,
                100,
                TimeUnit.MILLISECONDS);

    }
}
