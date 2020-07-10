package nl.vu.cs.s2group.nappa.handler.activity;

import org.jetbrains.annotations.NotNull;

import nl.vu.cs.s2group.nappa.Nappa;
import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.handler.activity.session.FetchSessionDataRunnable;
import nl.vu.cs.s2group.nappa.handler.activity.visittime.FetchSuccessorsVisitTimeRunnable;
import nl.vu.cs.s2group.nappa.handler.activity.visittime.FetchVisitTimeRunnable;
import nl.vu.cs.s2group.nappa.prefetch.PrefetchingStrategy;
import nl.vu.cs.s2group.nappa.room.activity.visittime.ActivityVisitTime;
import nl.vu.cs.s2group.nappa.room.data.ActivityExtraData;
import nl.vu.cs.s2group.nappa.room.data.SessionData;
import nl.vu.cs.s2group.nappa.util.NappaThreadPool;

/**
 * Defines a handler to fetch in the database LiveData objects for all information
 * the provided activity have in the database. Each specific data is define on its
 * own handler, as such, this handler acts as "hub" to invoke all these handlers in
 * a single point, thus reducing the need to write this logic twice in the {@link Nappa}
 * class. The fetched data includes {@link SessionData}, {@link ActivityVisitTime}
 * and {@link ActivityExtraData}. Upon fetching the data, all the specific handlers
 * register the fetched LiveData object in the provided activity. Using LiveData
 * objects ensures consistency with the database.
 */
public class FetchActivityLiveDataInfoHandler {

    public static void run(@NotNull ActivityNode activity, @NotNull PrefetchingStrategy strategy) {
        if (activity.shouldSetSessionAggregateLiveData())
            NappaThreadPool.submit(new FetchSessionDataRunnable(activity));

        if (strategy.needVisitTime() && !activity.isAggregateVisitTimeInstantiated())
            NappaThreadPool.submit(new FetchVisitTimeRunnable(activity));

        if (strategy.needSuccessorsVisitTime() && !activity.isSuccessorVisitTimeInstantiated())
            NappaThreadPool.submit(new FetchSuccessorsVisitTimeRunnable(activity));
    }
}
