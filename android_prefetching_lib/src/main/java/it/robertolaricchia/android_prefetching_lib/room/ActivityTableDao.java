package it.robertolaricchia.android_prefetching_lib.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import it.robertolaricchia.android_prefetching_lib.room.data.PRData;

@Dao
public interface ActivityTableDao {

    @Insert
    void insert(ActivityData activityData);

    @Query("SELECT id, activity_name FROM pf_activity")
    List<ActivityData> getListActivity();

    @Query("SELECT id, activity_name FROM pf_activity")
    LiveData<List<ActivityData>> getListActivityLiveData();

    @Insert
    void insertPR(PRData PR);

    @Update
    void updatePR(PRData prData);

    @Query("SELECT PR FROM pf_PR WHERE activity_name=:activity_name")
    float getPR(String activity_name);
}
