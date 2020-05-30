package nl.vu.cs.s2group.nappa.db.room.graph;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ActivityDao {

    @Insert
    void insert(Activity activityData);

    @Query("SELECT id, activity_name FROM nappa_activity")
    List<Activity> getListActivity();

    @Query("SELECT id, activity_name FROM nappa_activity")
    LiveData<List<Activity>> getListActivityLiveData();
}
