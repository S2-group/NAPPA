package nl.vu.cs.s2group.nappa.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import nl.vu.cs.s2group.nappa.room.data.ActivityExtraData;

@Dao
public interface ActivityExtraDao {

    @Insert
    void insertActivityExtra(ActivityExtraData activityExtraData);

    /**
     * Get all the extra's key-value pairs for a given activity
     * @param idAct The id of the Activity
     * @return A list contatining all extra's key value pairs
     */
    @Query("SELECT * FROM nappa_activity_extra WHERE id_activity = :idAct")
    LiveData<List<ActivityExtraData>> getActivityExtraLiveData(Long idAct);

}
