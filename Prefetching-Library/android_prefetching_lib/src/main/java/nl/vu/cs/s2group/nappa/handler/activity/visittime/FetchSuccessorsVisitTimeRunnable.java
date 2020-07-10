package nl.vu.cs.s2group.nappa.handler.activity.visittime;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.handler.SessionBasedSelectQueryType;
import nl.vu.cs.s2group.nappa.room.NappaDB;
import nl.vu.cs.s2group.nappa.room.activity.visittime.ActivityVisitTime;
import nl.vu.cs.s2group.nappa.room.activity.visittime.AggregateVisitTimeByActivity;

/**
 * Defines a handler to fetch in the database a list containing the aggregate {@link
 * ActivityVisitTime} for each successor of the provided node. After fetching the data,
 * this handler will register the LiveData object for the provide node to ensure consistency
 * with the database.
 */
public final class FetchSuccessorsVisitTimeRunnable implements Runnable {
    private static final String LOG_TAG = FetchSuccessorsVisitTimeRunnable.class.getSimpleName();

    ActivityNode activity;
    SessionBasedSelectQueryType queryType;
    int lastNSessions;

    public FetchSuccessorsVisitTimeRunnable(ActivityNode activity, SessionBasedSelectQueryType queryType, int lastNSessions) {
        this.activity = activity;
        this.queryType = queryType;
        this.lastNSessions = lastNSessions;
    }

    @Override
    public void run() {
        LiveData<List<AggregateVisitTimeByActivity>> successorsVisitTimeList;

        Log.d(LOG_TAG, activity.getActivitySimpleName() + " Fetching session data for " + queryType);

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
