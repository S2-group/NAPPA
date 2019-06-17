package nl.vu.cs.s2group.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import nl.vu.cs.s2group.room.data.LARData;

@Dao
public interface ActivityTableDao {

    @Insert
    void insert(ActivityData activityData);

    @Query("SELECT id, activity_name FROM pf_activity")
    List<ActivityData> getListActivity();

    @Query("SELECT id, activity_name FROM pf_activity")
    LiveData<List<ActivityData>> getListActivityLiveData();

    @Insert
    void insertLAR(LARData PR);

    @Update
    void updateLAR(LARData LARData);

    @Query("SELECT * FROM pf_LAR WHERE activity_name=:activity_name")
    LARData getLAR(String activity_name);
}
