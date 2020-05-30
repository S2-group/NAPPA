package nl.vu.cs.s2group.nappa.db.room.session;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;

@Dao
public interface SessionDao {

    @Insert
    void insertSession(Session session);

    @Insert
    void insertSessionData(SessionContent sessionContent);

    @Update
    void updateSessionData(SessionContent sessionContent);

    @Query("SELECT id, date from pf_session")
    LiveData<List<Session>> getSessionListLiveData();

    @Query("SELECT id, date from pf_session where date=:date")
    Session getSession(Long date);

    @Query("SELECT id_session, id_activity_source, id_activity_destination, count_source_destination FROM pf_session_content")
    LiveData<List<SessionContent>> getSessionDataListLiveData();

    /**
     * Gets the count of the number of instances a (source --> destination) edge has been followed, for
     * for a given idSource, for all of its destinations
     *
     * @param idSource The source {@link ActivityNode} x containing a successor set Y.
     *
     * @return Given x, for all y of Y,  a total count of all transitions x --> y  will be returned.
     */
    @Query("SELECT id_activity_destination as idActDest, activity_name as actName, SUM(count_source_destination) as countSource2Dest " +
            "FROM pf_session_content " +
            "LEFT JOIN pf_activity as pfa ON pfa.id = id_activity_destination " +
            "WHERE id_activity_source = :idSource " +
            "GROUP BY id_activity_destination")
    LiveData<List<SessionAggregate>> getCountForActivitySource(Long idSource);

    @Query("SELECT id_activity_destination as idActDest, activity_name as actName, SUM(count_source_destination) as countSource2Dest "+
            "FROM (SELECT id_activity_destination , activity_name , count_source_destination, id_session  " +
            "FROM pf_session_content " +
            "LEFT JOIN pf_activity as pfa ON pfa.id = id_activity_destination " +
            "WHERE id_activity_source = :idSource " +
            "ORDER BY id_session DESC) as X " +
            "WHERE X.id_session >= ((SELECT MAX(id_session) FROM pf_session_content) - :lastN) " +
            "GROUP BY id_activity_destination ")
    LiveData<List<SessionAggregate>> getCountForActivitySource(Long idSource, int lastN);

    class SessionAggregate {
        public Long idActDest;
        public String actName;
        public Long countSource2Dest;
    }
}
