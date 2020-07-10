package nl.vu.cs.s2group.nappa.handler.activity.visittime;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.handler.SessionBasedSelectQueryType;
import nl.vu.cs.s2group.nappa.prefetch.AbstractPrefetchingStrategy;
import nl.vu.cs.s2group.nappa.prefetch.PrefetchingStrategyConfigKeys;
import nl.vu.cs.s2group.nappa.room.NappaDB;
import nl.vu.cs.s2group.nappa.room.activity.visittime.ActivityVisitTime;
import nl.vu.cs.s2group.nappa.room.activity.visittime.AggregateVisitTimeByActivity;
import nl.vu.cs.s2group.nappa.util.NappaConfigMap;

/**
 * Defines a handler to fetch in the database a object containing the aggregate
 * {@link ActivityVisitTime} for the provided node. After fetching the data,
 * this handler will register the fetched LiveData object in the provided node.
 */
public class FetchVisitTimeRunnable implements Runnable {
    private static final String LOG_TAG = FetchVisitTimeRunnable.class.getSimpleName();

    ActivityNode activity;

    public FetchVisitTimeRunnable(ActivityNode activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        LiveData<AggregateVisitTimeByActivity> visitTime;
        SessionBasedSelectQueryType queryType = NappaConfigMap.getSessionBasedSelectQueryType();
        int lastNSessions = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.LAST_N_SESSIONS,
                AbstractPrefetchingStrategy.DEFAULT_LAST_N_SESSIONS);

        Log.d(LOG_TAG, "Fetching successors visit time for " + queryType);

        switch (queryType) {
            case ALL_SESSIONS:
                visitTime = NappaDB.getInstance()
                        .activityVisitTimeDao()
                        .getAggregateVisitTimeByActivity(activity.getActivityId());
                break;
            case LAST_N_SESSIONS_FROM_ENTITY_SESSION:
                visitTime = NappaDB.getInstance()
                        .activityVisitTimeDao()
                        .getAggregateVisitTimeByActivityWithinLastNSessionsInEntitySession(activity.getActivityId(), lastNSessions);
                break;
            case LAST_N_SESSIONS_FROM_QUERIED_ENTITY:
                visitTime = NappaDB.getInstance()
                        .activityVisitTimeDao()
                        .getAggregateVisitTimeByActivityWithinLastNSessionsInThisEntity(activity.getActivityId(), lastNSessions);
                break;
            default:
                throw new IllegalArgumentException("Unknown query type " + queryType);
        }

        new Handler(Looper.getMainLooper()).post(() -> activity.setAggregateVisitTimeLiveData(visitTime));
    }
}
