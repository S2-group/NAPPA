package nl.vu.cs.s2group.nappa.sample.app.weather_and_news.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import nl.vu.cs.s2group.nappa.PrefetchingLib;
import nl.vu.cs.s2group.nappa.room.data.SessionData;

public class ViewModelSessionDataList extends ViewModel {

    public LiveData<List<SessionData>> listLiveData;

    public ViewModelSessionDataList() {
        listLiveData = PrefetchingLib.getSessionDataListLiveData();
    }

}
