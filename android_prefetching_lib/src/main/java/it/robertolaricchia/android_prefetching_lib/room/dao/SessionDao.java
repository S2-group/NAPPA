package it.robertolaricchia.android_prefetching_lib.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import it.robertolaricchia.android_prefetching_lib.room.data.Session;
import it.robertolaricchia.android_prefetching_lib.room.data.SessionData;

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
     * @param idSource
     * @return
     */
    @Query("SELECT id_activity_destination as idActDest, activity_name as actName, SUM(count_source_destination) as countSource2Dest " +
            "FROM pf_session_data " +
            "LEFT JOIN pf_activity as pfa ON pfa.id = id_activity_destination " +
            "WHERE id_activity_source = :idSource " +
            "GROUP BY id_activity_destination")
    public LiveData<List<SessionAggregate>> getCountForActivitySource (Long idSource);

    class SessionAggregate {
        public Long idActDest;
        public String actName;
        public Long countSource2Dest;
    }

}
