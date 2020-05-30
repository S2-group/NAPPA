package nl.vu.cs.s2group.nappa.db.room.graph;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface LARDao {
    @Insert
    void insertLAR(LARData PR);

    @Update
    void updateLAR(LARData LARData);

    @Query("SELECT * FROM nappa_LAR WHERE activity_name=:activity_name")
    LARData getLAR(String activity_name);
}
