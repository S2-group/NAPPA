package nl.vu.cs.s2group.nappa.room.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GraphEdgeDao {


    /**
     * Get all the edges corresponding to a given activity.
     *
     * Essentially get all Destination's ID (nappa_session data) AND its activity Name (nappa_activity),
     * all of which corresponds to a source (String actName)
     *
     * @param actName
     * @return
     */
    @Query("SELECT DISTINCT id_activity_destination as idActDest, activity_name as actName " +
            "FROM nappa_session_data as psd " +
            "LEFT JOIN nappa_activity as pa ON pa.id = psd.id_activity_destination " +
            "WHERE id_activity_source = (SELECT id FROM nappa_activity WHERE activity_name = :actName) ")
    List<GraphEdge> getEdgesForActivity(String actName);


    class GraphEdge {
        public Long idActDest;
        public String actName;
    }

}
