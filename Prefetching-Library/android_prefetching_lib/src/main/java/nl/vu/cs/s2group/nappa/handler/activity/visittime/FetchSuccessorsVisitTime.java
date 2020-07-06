package nl.vu.cs.s2group.nappa.handler.activity.visittime;

import android.os.Looper;

import org.jetbrains.annotations.NotNull;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.handler.SessionBasedSelectQueryType;
import nl.vu.cs.s2group.nappa.room.PrefetchingDatabase;
import nl.vu.cs.s2group.nappa.util.NappaScheduler;

public class FetchSuccessorsVisitTime {
    private static final String LOG_TAG = FetchSuccessorsVisitTime.class.getSimpleName();

    public static void run(ActivityNode activity, SessionBasedSelectQueryType queryType) {
        run(activity, queryType, 0);
    }

    public static void run(ActivityNode activity, @NotNull SessionBasedSelectQueryType queryType, int lastNSessions) {
        if (Looper.getMainLooper().isCurrentThread())
            NappaScheduler.scheduler.execute(() -> runQuery(activity, queryType, lastNSessions));
        else runQuery(activity, queryType, lastNSessions);
    }

    private static void runQuery(ActivityNode activity, @NotNull SessionBasedSelectQueryType queryType, int lastNSessions) {
        switch (queryType) {
            case ALL_SESSIONS:
                PrefetchingDatabase.getInstance()
                        .activityVisitTimeDao()
                        .getSuccessorAggregateVisitTime(activity.activityName);
                break;
            case LAST_N_SESSIONS_FROM_ENTITY_SESSION:
                PrefetchingDatabase.getInstance()
                        .activityVisitTimeDao()
                        .getSuccessorAggregateVisitTimeWithinLastNSessionsInEntitySession(activity.activityName, lastNSessions);
                break;
            case LAST_N_SESSIONS_FROM_QUERIED_ENTITY:
                PrefetchingDatabase.getInstance()
                        .activityVisitTimeDao()
                        .getSuccessorAggregateVisitTimeWithinLastNSessionsInThisEntity(activity.activityName, lastNSessions);
                break;
            default:
                throw new IllegalArgumentException("Unknown query type " + queryType);
        }
    }
}
