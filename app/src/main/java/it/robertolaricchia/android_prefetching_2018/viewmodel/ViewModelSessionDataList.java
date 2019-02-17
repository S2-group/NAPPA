package it.robertolaricchia.android_prefetching_2018.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import it.robertolaricchia.android_prefetching_lib.PrefetchingLib;
import it.robertolaricchia.android_prefetching_lib.room.data.SessionData;

public class ViewModelSessionDataList extends ViewModel {

    public LiveData<List<SessionData>> listLiveData;

    public ViewModelSessionDataList() {
        listLiveData = PrefetchingLib.getSessionDataListLiveData();
    }

}
