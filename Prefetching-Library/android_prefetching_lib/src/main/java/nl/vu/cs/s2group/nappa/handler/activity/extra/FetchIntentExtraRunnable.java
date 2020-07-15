package nl.vu.cs.s2group.nappa.handler.activity.extra;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.room.NappaDB;
import nl.vu.cs.s2group.nappa.room.data.ActivityExtraData;

/**
 * Defines a runnable to fetch in the database a object containing the aggregate
 * {@link ActivityExtraData} for the provided node. After fetching the data,
 * this handler will register the fetched LiveData object in the provided node.
 */
public class FetchIntentExtraRunnable implements Runnable {
    private static final String LOG_TAG = FetchIntentExtraRunnable.class.getSimpleName();

    ActivityNode activity;

    public FetchIntentExtraRunnable(ActivityNode activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        Log.d(LOG_TAG, activity.getActivitySimpleName() + " Fetching intent extras");

        LiveData<List<ActivityExtraData>> liveData = NappaDB.getInstance()
                .activityExtraDao()
                .getActivityExtraLiveData(activity.getActivityId());

        new Handler(Looper.getMainLooper()).post(() -> activity.setListActivityExtraLiveData(liveData));
    }
}
