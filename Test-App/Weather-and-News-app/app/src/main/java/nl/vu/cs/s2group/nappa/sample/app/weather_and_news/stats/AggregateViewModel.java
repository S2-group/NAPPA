package nl.vu.cs.s2group.nappa.sample.app.weather_and_news.stats;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import nl.vu.cs.s2group.nappa.db.NappaDB;
import nl.vu.cs.s2group.nappa.db.room.http.AggregateURL;

public class AggregateViewModel extends ViewModel {

    private LiveData<List<AggregateURL>> liveAggregate;

    void loadAggregate() {
        liveAggregate = NappaDB.getInstance().urlDao().getAggregateLiveData();
    }

    LiveData<List<AggregateURL>> getLiveData() {
        return liveAggregate;
    }

}
