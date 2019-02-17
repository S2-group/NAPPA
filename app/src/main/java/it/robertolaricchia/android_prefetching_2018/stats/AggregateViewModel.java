package it.robertolaricchia.android_prefetching_2018.stats;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import it.robertolaricchia.android_prefetching_lib.room.AggregateUrlDao;
import it.robertolaricchia.android_prefetching_lib.room.PrefetchingDatabase;

public class AggregateViewModel extends ViewModel {

    private LiveData<List<AggregateUrlDao.AggregateURL>> liveAggregate;

    void loadAggregate() {
        liveAggregate = PrefetchingDatabase.getInstance().urlDao().getAggregateLiveData();
    }

    LiveData<List<AggregateUrlDao.AggregateURL>> getLiveData() {
        return liveAggregate;
    }

}
