package nl.vu.cs.s2group.nappa.handler.activity.visittime;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.handler.SessionBasedSelectQueryType;
import nl.vu.cs.s2group.nappa.room.PrefetchingDatabase;
import nl.vu.cs.s2group.nappa.room.activity.visittime.AggregateVisitTimeByActivity;
import nl.vu.cs.s2group.nappa.util.NappaThreadPool;

/**
 * Defines a handler to fetch in the database a list containing the aggregate
 * {@link nl.vu.cs.s2group.nappa.room.activity.visittime.ActivityVisitTime ActivityVisitTime}
 * for each successor of the provided node. After fetching the data, this handler will
 * invoke the method {@link ActivityNode#setSuccessorsAggregateVisitTimeLiveData(LiveData)}
 * for the provided node.
 */
public class FetchSuccessorsVisitTime {
    private static final String LOG_TAG = FetchSuccessorsVisitTime.class.getSimpleName();

    public static void run(ActivityNode activity, SessionBasedSelectQueryType queryType) {
        run(activity, queryType, 0);
    }

    public static void run(ActivityNode activity, @NotNull SessionBasedSelectQueryType queryType, int lastNSessions) {
        if (Looper.getMainLooper().isCurrentThread())
            NappaThreadPool.scheduler.execute(() -> runQuery(activity, queryType, lastNSessions));
        else runQuery(activity, queryType, lastNSessions);
    }

    private static void runQuery(ActivityNode activity, @NotNull SessionBasedSelectQueryType queryType, int lastNSessions) {
        LiveData<List<AggregateVisitTimeByActivity>> successorsVisitTimeList;

        Log.d(LOG_TAG, "Fetching successors visit time for " + queryType);

        switch (queryType) {
            case ALL_SESSIONS:
                successorsVisitTimeList = PrefetchingDatabase.getInstance()
                        .activityVisitTimeDao()
                        .getSuccessorAggregateVisitTime(activity.activityName);
                break;
            case LAST_N_SESSIONS_FROM_ENTITY_SESSION:
                successorsVisitTimeList = PrefetchingDatabase.getInstance()
                        .activityVisitTimeDao()
                        .getSuccessorAggregateVisitTimeWithinLastNSessionsInEntitySession(activity.activityName, lastNSessions);
                break;
            case LAST_N_SESSIONS_FROM_QUERIED_ENTITY:
                successorsVisitTimeList = PrefetchingDatabase.getInstance()
                        .activityVisitTimeDao()
                        .getSuccessorAggregateVisitTimeWithinLastNSessionsInThisEntity(activity.activityName, lastNSessions);
                break;
            default:
                throw new IllegalArgumentException("Unknown query type " + queryType);
        }

        new Handler(Looper.getMainLooper()).post(() -> activity.setSuccessorsAggregateVisitTimeLiveData(successorsVisitTimeList));
    }
}
