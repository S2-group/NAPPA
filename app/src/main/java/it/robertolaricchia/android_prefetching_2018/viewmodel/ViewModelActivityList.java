package it.robertolaricchia.android_prefetching_2018.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import it.robertolaricchia.android_prefetching_lib.PrefetchingLib;
import it.robertolaricchia.android_prefetching_lib.room.ActivityData;

public class ViewModelActivityList extends ViewModel {

    public LiveData<List<ActivityData>> liveData;

    public ViewModelActivityList() {
        liveData = PrefetchingLib.getActivityLiveData();
    }

}
