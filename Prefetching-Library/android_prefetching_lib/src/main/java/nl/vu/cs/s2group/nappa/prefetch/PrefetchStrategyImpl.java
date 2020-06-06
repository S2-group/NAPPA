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

public class PrefetchStrategyImpl implements PrefetchStrategy {
    @NonNull
    @Override
    public List<String> getTopNUrlToPrefetchForNode(ActivityNode node, Integer maxNumber) {
        Log.d("PrefStratImpl", "Started for node: "+node.activityName);
        SessionDao.SessionAggregate best = null;
        List<SessionDao.SessionAggregate> sessionAggregateList = node.getSessionAggregateList();
        if (sessionAggregateList!=null) {
            for (SessionDao.SessionAggregate aggregate : sessionAggregateList) {
                Log.d("PrefStratImpl", "Evaluating successor: " + aggregate.actName);
                if (best == null) {
                    best = aggregate;
                } else if (aggregate.countSource2Dest > best.countSource2Dest) {
                    Log.d("PrefStratImpl",
                            "choosing "+aggregate.actName+": "+aggregate.countSource2Dest+" against " +
                                    best.actName+": "+best.countSource2Dest);
                    best = aggregate;
                }
            }
            if (best != null) {
                Log.d("PrefStratImpl", "Chosen successor: " + best.actName);
                List<AggregateUrlDao.AggregateURL> list = PrefetchingDatabase.getInstance().urlDao().getAggregateForIdActivity(best.idActDest, maxNumber);
                LinkedList<String> toBeReturned = new LinkedList<String>();
                for (AggregateUrlDao.AggregateURL elem : list) {
                    toBeReturned.add(elem.getUrl());
                }
                return toBeReturned;
            } else {
                Log.d("PrefStratImpl", "Null successor");
            }
        } else {
            Log.d("PrefStratImpl", "SessionAggregateList is null");
        }
        return Arrays.asList(new String[]{});
    }
}
