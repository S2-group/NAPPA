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

public class FetchVisitTimeHandler {
    private static final String LOG_TAG = FetchVisitTimeHandler.class.getSimpleName();

    private FetchVisitTimeHandler() {
        throw new IllegalStateException("FetchVisitTimeHandler is a handler class and should not be instantiated!");
    }

    public static void run(@NotNull ActivityNode activity) {
        if (activity.isSuccessorVisitTimeInstantiated()) return;
        if (Looper.getMainLooper().isCurrentThread())
            NappaThreadPool.scheduler.execute(() -> runQuery(activity));
        else runQuery(activity);
    }

    private static void runQuery(ActivityNode activity) {
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
                        .getSuccessorAggregateVisitTime(activity.activityName);
                break;
            case LAST_N_SESSIONS_FROM_ENTITY_SESSION:
                successorsVisitTimeList = NappaDB.getInstance()
                        .activityVisitTimeDao()
                        .getSuccessorAggregateVisitTimeWithinLastNSessionsInEntitySession(activity.activityName, lastNSessions);
                break;
            case LAST_N_SESSIONS_FROM_QUERIED_ENTITY:
                successorsVisitTimeList = NappaDB.getInstance()
                        .activityVisitTimeDao()
                        .getSuccessorAggregateVisitTimeWithinLastNSessionsInThisEntity(activity.activityName, lastNSessions);
                break;
            default:
                throw new IllegalArgumentException("Unknown query type " + queryType);
        }

        new Handler(Looper.getMainLooper()).post(() -> activity.setSuccessorsAggregateVisitTimeLiveData(successorsVisitTimeList));
    }
}
