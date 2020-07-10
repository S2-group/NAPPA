package nl.vu.cs.s2group.nappa.handler.activity.session;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.handler.SessionBasedSelectQueryType;
import nl.vu.cs.s2group.nappa.room.NappaDB;
import nl.vu.cs.s2group.nappa.room.dao.SessionDao;
import nl.vu.cs.s2group.nappa.room.data.SessionData;

/**
 * Defines a runnable to fetch in the database a object containing the {@link SessionData}
 * for the provided node. The data contains the count of Source --> Destination edge
 * visits. After fetching the data, this handler will register the LiveData object
 * for the provide node to ensure consistency with the database.
 */
public class FetchSessionDataRunnable implements Runnable {
    private static final String LOG_TAG = FetchSessionDataRunnable.class.getSimpleName();

    ActivityNode activity;
    SessionBasedSelectQueryType queryType;
    int lastNSessions;

    public FetchSessionDataRunnable(ActivityNode activity, SessionBasedSelectQueryType queryType, int lastNSessions) {
        this.activity = activity;
        this.queryType = queryType;
        this.lastNSessions = lastNSessions;
    }

    @Override
    public void run() {
        LiveData<List<SessionDao.SessionAggregate>> sessionDataList;

        Log.d(LOG_TAG, "Fetching session data for " + queryType);

        switch (queryType) {
            case ALL_SESSIONS:
                sessionDataList = NappaDB.getInstance()
                        .sessionDao()
                        .getCountForActivitySource(activity.getActivityId());
                break;
            case LAST_N_SESSIONS_FROM_ENTITY_SESSION:
            case LAST_N_SESSIONS_FROM_QUERIED_ENTITY:
                sessionDataList = NappaDB.getInstance()
                        .sessionDao()
                        .getCountForActivitySource(activity.getActivityId(), lastNSessions);
                break;
            default:
                throw new IllegalArgumentException("Unknown query type " + queryType);
        }

        new Handler(Looper.getMainLooper()).post(() -> activity.setListSessionAggregateLiveData(sessionDataList));
    }
}
