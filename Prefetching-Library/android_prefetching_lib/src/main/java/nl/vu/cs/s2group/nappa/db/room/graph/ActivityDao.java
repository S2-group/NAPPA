package nl.vu.cs.s2group.nappa.db.room.graph;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import nl.vu.cs.s2group.nappa.db.data.LARData;

@Dao
public interface ActivityDao {

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
