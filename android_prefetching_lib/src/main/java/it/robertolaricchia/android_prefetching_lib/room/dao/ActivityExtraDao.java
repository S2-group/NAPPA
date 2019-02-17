package it.robertolaricchia.android_prefetching_lib.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import it.robertolaricchia.android_prefetching_lib.room.data.ActivityExtraData;

@Dao
public interface ActivityExtraDao {

    @Insert
    void insertActivityExtra(ActivityExtraData activityExtraData);

    @Query("SELECT * FROM pf_activity_extra WHERE id_activity = :idAct")
    LiveData<List<ActivityExtraData>> getActivityExtraLiveData(Long idAct);

}
