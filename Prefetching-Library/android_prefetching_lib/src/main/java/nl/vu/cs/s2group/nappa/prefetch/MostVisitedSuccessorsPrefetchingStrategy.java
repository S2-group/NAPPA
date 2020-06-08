package nl.vu.cs.s2group.nappa.prefetch;

import androidx.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.room.AggregateUrlDao;
import nl.vu.cs.s2group.nappa.room.PrefetchingDatabase;
import nl.vu.cs.s2group.nappa.room.dao.SessionDao;

@Deprecated
public class MostVisitedSuccessorsPrefetchingStrategy implements PrefetchStrategy {
    private final static String LOG_TAG = MostVisitedSuccessorsPrefetchingStrategy.class.getSimpleName();
    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber) {
        Log.d(LOG_TAG, "Started for node: "+node.activityName);
        SessionDao.SessionAggregate best = null;
        List<SessionDao.SessionAggregate> sessionAggregateList = node.getSessionAggregateList();
        if (sessionAggregateList!=null) {
            for (SessionDao.SessionAggregate aggregate : sessionAggregateList) {
                Log.d(LOG_TAG, "Evaluating successor: " + aggregate.actName);
                if (best == null) {
                    best = aggregate;
                } else if (aggregate.countSource2Dest > best.countSource2Dest) {
                    Log.d(LOG_TAG,
                            "choosing "+aggregate.actName+": "+aggregate.countSource2Dest+" against " +
                                    best.actName+": "+best.countSource2Dest);
                    best = aggregate;
                }
            }
            if (best != null) {
                Log.d(LOG_TAG, "Chosen successor: " + best.actName);
                List<AggregateUrlDao.AggregateURL> list = PrefetchingDatabase.getInstance().urlDao().getAggregateForIdActivity(best.idActDest, maxNumber);
                LinkedList<String> toBeReturned = new LinkedList<String>();
                for (AggregateUrlDao.AggregateURL elem : list) {
                    toBeReturned.add(elem.getUrl());
                }
                return toBeReturned;
            } else {
                Log.d(LOG_TAG, "Null successor");
            }
        } else {
            Log.d(LOG_TAG, "SessionAggregateList is null");
        }
        return Arrays.asList(new String[]{});
    }
}
