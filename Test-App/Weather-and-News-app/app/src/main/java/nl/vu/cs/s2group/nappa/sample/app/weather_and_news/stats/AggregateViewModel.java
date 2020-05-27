package nl.vu.cs.s2group.nappa.sample.app.weather_and_news.stats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import nl.vu.cs.s2group.nappa.room.AggregateUrlDao;
import nl.vu.cs.s2group.nappa.room.PrefetchingDatabase;

public class AggregateViewModel extends ViewModel {

    private LiveData<List<AggregateUrlDao.AggregateURL>> liveAggregate;

    void loadAggregate() {
        liveAggregate = PrefetchingDatabase.getInstance().urlDao().getAggregateLiveData();
    }

    LiveData<List<AggregateUrlDao.AggregateURL>> getLiveData() {
        return liveAggregate;
    }

}
