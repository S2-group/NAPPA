package it.robertolaricchia.android_prefetching_lib.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ActivityTableDao {

    @Insert
    void insert(ActivityData activityData);

    @Query("SELECT id, activity_name FROM pf_activity")
    List<ActivityData> getListActivity();

    @Query("SELECT id, activity_name FROM pf_activity")
    LiveData<List<ActivityData>> getListActivityLiveData();

}
