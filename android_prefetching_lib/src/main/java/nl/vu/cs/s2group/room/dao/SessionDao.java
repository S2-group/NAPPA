package nl.vu.cs.s2group.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import nl.vu.cs.s2group.room.data.Session;
import nl.vu.cs.s2group.room.data.SessionData;
import nl.vu.cs.s2group.graph.ActivityNode;

@Dao
public interface SessionDao {

    @Insert
    public void insertSession(Session session);

    @Insert
    public void insertSessionData(SessionData sessionData);

    @Update
    public void updateSessionData(SessionData sessionData);

    @Query("SELECT id, date from pf_session")
    public LiveData<List<Session>> getSessionListLiveData();

    @Query("SELECT id, date from pf_session where date=:date")
    public Session getSession(Long date);

    @Query("SELECT id_session, id_activity_source, id_activity_destination, count_source_destination FROM pf_session_data")
    public LiveData<List<SessionData>> getSessionDataListLiveData();

    /**
     * Gets the count of the number of instances a (source --> destination) edge has been followed, for
     * for a given idSource, for all of its destinations
     *
     * @param idSource The source {@link ActivityNode} x containing a successor set Y.
     *
     * @return Given x, for all y of Y,  a total count of all transitions x --> y  will be returned.
     */
    @Query("SELECT id_activity_destination as idActDest, activity_name as actName, SUM(count_source_destination) as countSource2Dest " +
            "FROM pf_session_data " +
            "LEFT JOIN pf_activity as pfa ON pfa.id = id_activity_destination " +
            "WHERE id_activity_source = :idSource " +
            "GROUP BY id_activity_destination")
    public LiveData<List<SessionAggregate>> getCountForActivitySource (Long idSource);

    @Query("SELECT id_activity_destination as idActDest, activity_name as actName, SUM(count_source_destination) as countSource2Dest "+
            "FROM (SELECT id_activity_destination , activity_name , count_source_destination, id_session  " +
            "FROM pf_session_data " +
            "LEFT JOIN pf_activity as pfa ON pfa.id = id_activity_destination " +
            "WHERE id_activity_source = :idSource " +
            "ORDER BY id_session DESC) as X " +
            "WHERE X.id_session >= ((SELECT MAX(id_session) FROM pf_session_data) - :lastN) " +
            "GROUP BY id_activity_destination ")
    public LiveData<List<SessionAggregate>> getCountForActivitySource (Long idSource, int lastN);
/*@Query("SELECT id_activity_destination as idActDest, activity_name as actName, SUM(count_source_destination) as countSource2Dest "+
            "FROM (SELECT id_activity_destination , activity_name , count_source_destination, id_session  " +
            "FROM pf_session_data " +
            "LEFT JOIN pf_activity as pfa ON pfa.id = id_activity_destination " +
            "WHERE id_activity_source = :idSource " +
            "ORDER BY id_session DESC) as X " +
            "WHERE X.id_session > ((SELECT MAX(id_session) FROM pf_session_data) - :lastN) " +
            "GROUP BY id_activity_destination ")*/
    class SessionAggregate {
        public Long idActDest;
        public String actName;
        public Long countSource2Dest;
    }

}
