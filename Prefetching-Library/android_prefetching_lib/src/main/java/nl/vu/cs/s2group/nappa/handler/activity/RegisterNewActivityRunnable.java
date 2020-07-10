package nl.vu.cs.s2group.nappa.handler.activity;

import android.util.Log;

import java.util.function.Consumer;

import nl.vu.cs.s2group.nappa.graph.ActivityGraph;
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
        FetchActivityLiveDataInfoHandler.run(graph.getCurrent(), strategy);

        callback.accept(activity);

        Log.d(LOG_TAG, "Activity registered " + activity.activityName + " (#" + activity.id + ")");
    }
}
