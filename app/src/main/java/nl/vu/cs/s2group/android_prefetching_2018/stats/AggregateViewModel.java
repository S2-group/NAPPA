package nl.vu.cs.s2group.android_prefetching_2018.stats;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import nl.vu.cs.s2group.room.AggregateUrlDao;
import nl.vu.cs.s2group.room.PrefetchingDatabase;

public class AggregateViewModel extends ViewModel {

    private LiveData<List<AggregateUrlDao.AggregateURL>> liveAggregate;

    void loadAggregate() {
        liveAggregate = PrefetchingDatabase.getInstance().urlDao().getAggregateLiveData();
    }

    LiveData<List<AggregateUrlDao.AggregateURL>> getLiveData() {
        return liveAggregate;
    }

}
