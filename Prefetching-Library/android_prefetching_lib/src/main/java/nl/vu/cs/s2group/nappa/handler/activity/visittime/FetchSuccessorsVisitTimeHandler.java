package nl.vu.cs.s2group.nappa.handler.activity.visittime;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.handler.SessionBasedSelectQueryType;
import nl.vu.cs.s2group.nappa.prefetch.AbstractPrefetchingStrategy;
import nl.vu.cs.s2group.nappa.prefetch.PrefetchingStrategyConfigKeys;
import nl.vu.cs.s2group.nappa.room.NappaDB;
import nl.vu.cs.s2group.nappa.room.activity.visittime.AggregateVisitTimeByActivity;
import nl.vu.cs.s2group.nappa.util.NappaConfigMap;
import nl.vu.cs.s2group.nappa.util.NappaThreadPool;

/**
 * Defines a handler to fetch in the database a the object containing the aggregate
 * {@link nl.vu.cs.s2group.nappa.room.activity.visittime.ActivityVisitTime ActivityVisitTime}
 * for the provided node. After fetching the data, this handler will register the LiveData
 * object in the provided node.
 */
public final class FetchSuccessorsVisitTimeHandler {
    private static final String LOG_TAG = FetchSuccessorsVisitTimeHandler.class.getSimpleName();

    private FetchSuccessorsVisitTimeHandler() {
        throw new IllegalStateException("FetchSuccessorsVisitTimeHandler is a handler class and should not be instantiated!");
    }

    public static void run(@NotNull ActivityNode activity) {
        if (activity.isAggregateVisitTimeInstantiated()) return;
        if (Looper.getMainLooper().isCurrentThread())
            NappaThreadPool.scheduler.execute(() -> runQuery(activity));
        else runQuery(activity);
    }

    private static void runQuery(ActivityNode activity) {
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
                        .getAggregateVisitTimeByActivity(activity.activityName);
                break;
            case LAST_N_SESSIONS_FROM_ENTITY_SESSION:
                visitTime = NappaDB.getInstance()
                        .activityVisitTimeDao()
                        .getAggregateVisitTimeByActivityWithinLastNSessionsInEntitySession(activity.activityName, lastNSessions);
                break;
            case LAST_N_SESSIONS_FROM_QUERIED_ENTITY:
                visitTime = NappaDB.getInstance()
                        .activityVisitTimeDao()
                        .getAggregateVisitTimeByActivityWithinLastNSessionsInThisEntity(activity.activityName, lastNSessions);
                break;
            default:
                throw new IllegalArgumentException("Unknown query type " + queryType);
        }

        new Handler(Looper.getMainLooper()).post(() -> activity.setAggregateVisitTimeLiveData(visitTime));
    }
}
