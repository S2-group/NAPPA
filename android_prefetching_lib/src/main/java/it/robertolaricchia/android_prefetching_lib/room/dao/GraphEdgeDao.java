package it.robertolaricchia.android_prefetching_lib.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface GraphEdgeDao {

    @Query("SELECT DISTINCT id_activity_destination as idActDest, activity_name as actName " +
            "FROM pf_session_data as psd " +
            "LEFT JOIN pf_activity as pa ON pa.id = psd.id_activity_destination " +
            "WHERE id_activity_source = (SELECT id FROM pf_activity WHERE activity_name = :actName) ")
    List<GraphEdge> getEdgesForActivity(String actName);

    class GraphEdge {
        public Long idActDest;
        public String actName;
    }

}
