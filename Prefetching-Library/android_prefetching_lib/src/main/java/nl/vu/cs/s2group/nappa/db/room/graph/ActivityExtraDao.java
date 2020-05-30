package nl.vu.cs.s2group.nappa.db.room.graph;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ActivityExtraDao {

    @Insert
    void insertActivityExtra(ActivityExtra activityExtra);

    /**
     * Get all the extra's key-value pairs for a given activity
     * @param idAct The id of the Activity
     * @return A list contatining all extra's key value pairs
     */
    @Query("SELECT * FROM nappa_activity_extra WHERE id_activity = :idAct")
    LiveData<List<ActivityExtra>> getActivityExtraLiveData(Long idAct);

}
