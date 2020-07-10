package nl.vu.cs.s2group.nappa.handler.activity.visittime;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.handler.SessionBasedSelectQueryType;
import nl.vu.cs.s2group.nappa.prefetch.AbstractPrefetchingStrategy;
import nl.vu.cs.s2group.nappa.prefetch.PrefetchingStrategyConfigKeys;
import nl.vu.cs.s2group.nappa.room.NappaDB;
import nl.vu.cs.s2group.nappa.room.activity.visittime.AggregateVisitTimeByActivity;
import nl.vu.cs.s2group.nappa.util.NappaConfigMap;
import nl.vu.cs.s2group.nappa.util.NappaThreadPool;

/**
 * Defines a handler to fetch in the database a list containing the aggregate
 * {@link nl.vu.cs.s2group.nappa.room.activity.visittime.ActivityVisitTime ActivityVisitTime}
 * for each successor of the provided node. After fetching the data, this handler will
 * invoke the method {@link ActivityNode#setSuccessorsAggregateVisitTimeLiveData(LiveData)}
 * for the provided node.
 */
public final class FetchSuccessorsVisitTimeHandler {
    private static final String LOG_TAG = FetchSuccessorsVisitTimeHandler.class.getSimpleName();

    private FetchSuccessorsVisitTimeHandler() {
        throw new IllegalStateException("FetchSuccessorsVisitTimeHandler is a handler class and should not be instantiated!");
    }

    public static void run(@NotNull ActivityNode activity) {
        if (activity.isSuccessorVisitTimeInstantiated()) return;
        if (Looper.getMainLooper().isCurrentThread())
            NappaThreadPool.submit(() -> runQuery(activity));
        else runQuery(activity);
    }

    private static void runQuery(@NotNull ActivityNode activity) {
        LiveData<List<AggregateVisitTimeByActivity>> successorsVisitTimeList;
        SessionBasedSelectQueryType queryType = NappaConfigMap.getSessionBasedSelectQueryType();
        int lastNSessions = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.LAST_N_SESSIONS,
                AbstractPrefetchingStrategy.DEFAULT_LAST_N_SESSIONS);

        Log.d(LOG_TAG, "Fetching successors visit time for " + queryType);

        switch (queryType) {
            case ALL_SESSIONS:
                successorsVisitTimeList = NappaDB.getInstance()
                        .activityVisitTimeDao()
                        .getSuccessorAggregateVisitTime(activity.getActivityId());
                break;
            case LAST_N_SESSIONS_FROM_ENTITY_SESSION:
                successorsVisitTimeList = NappaDB.getInstance()
                        .activityVisitTimeDao()
                        .getSuccessorAggregateVisitTimeWithinLastNSessionsInEntitySession(activity.getActivityId(), lastNSessions);
                break;
            case LAST_N_SESSIONS_FROM_QUERIED_ENTITY:
                successorsVisitTimeList = NappaDB.getInstance()
                        .activityVisitTimeDao()
                        .getSuccessorAggregateVisitTimeWithinLastNSessionsInThisEntity(activity.getActivityId(), lastNSessions);
                break;
            default:
                throw new IllegalArgumentException("Unknown query type " + queryType);
        }

        new Handler(Looper.getMainLooper()).post(() -> activity.setSuccessorsAggregateVisitTimeLiveData(successorsVisitTimeList));
    }
}
