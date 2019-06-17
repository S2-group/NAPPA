package nl.vu.cs.s2group.android_prefetching_2018.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import nl.vu.cs.s2group.PrefetchingLib;
import nl.vu.cs.s2group.room.ActivityData;

public class ViewModelActivityList extends ViewModel {

    public LiveData<List<ActivityData>> liveData;

    public ViewModelActivityList() {
        liveData = PrefetchingLib.getActivityLiveData();
    }

}
