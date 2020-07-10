package nl.vu.cs.s2group.nappa.handler.activity.session;

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
import nl.vu.cs.s2group.nappa.room.dao.SessionDao;
import nl.vu.cs.s2group.nappa.room.data.SessionData;
import nl.vu.cs.s2group.nappa.util.NappaConfigMap;
import nl.vu.cs.s2group.nappa.util.NappaThreadPool;

/**
 * Defines a handler to fetch in the database a object containing the {@link SessionData}
 * for the provided node. The data contains the count of Source --> Destination edge
 * visits. After fetching the data, this handler will register the LiveData object
 * for the provide node to ensure consistency with the database.
 */
public class FetchSessionDataHandler {
    private static final String LOG_TAG = FetchSessionDataHandler.class.getSimpleName();

    private FetchSessionDataHandler() {
        throw new IllegalStateException(LOG_TAG + " is a handler class and should not be instantiated!");
    }

    public static void run(@NotNull ActivityNode activity) {
        if (!activity.shouldSetSessionAggregateLiveData()) return;
        if (Looper.getMainLooper().isCurrentThread())
            NappaThreadPool.submit(() -> runQuery(activity));
        else runQuery(activity);
    }

    private static void runQuery(@NotNull ActivityNode activity) {
        LiveData<List<SessionDao.SessionAggregate>> sessionDataList;
        SessionBasedSelectQueryType queryType = NappaConfigMap.getSessionBasedSelectQueryType();

        int lastNSessions = NappaConfigMap.get(
                PrefetchingStrategyConfigKeys.LAST_N_SESSIONS,
                AbstractPrefetchingStrategy.DEFAULT_LAST_N_SESSIONS);

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
