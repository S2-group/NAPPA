package nl.vu.cs.s2group.nappa.db.room.graph;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface LARDao {
    @Insert
    void insertLAR(LAR PR);

    @Update
    void updateLAR(LAR LAR);

    @Query("SELECT * FROM nappa_lar WHERE activity_name=:activity_name")
    LAR getLAR(String activity_name);
}
