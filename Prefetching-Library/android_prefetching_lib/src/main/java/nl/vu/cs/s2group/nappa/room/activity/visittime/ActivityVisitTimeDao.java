package nl.vu.cs.s2group.nappa.room.activity.visittime;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ActivityVisitTimeDao {
    @Insert
    void insert(ActivityVisitTime activityVisitTime);

    /**
     * This query takes the Database View {@link AggregateVisitTimeBySession} with the aggregate
     * time per activity and session and filters this View to return only the rows concerning the
     * provided activity and the last N sessions. The result is aggregated again, returning a
     * single object containing only the activity ID and the total duration
     *
     * @param activityId    The activity to search for
     * @param lastNSessions The number of sessions to take, starting from the current session and before
     * @return A list of the total aggregate time spend in a given activity for the last N sessions
     */
    @Query("SELECT activityId, totalDuration " +
            "FROM AggregateVisitTimeBySession " +
            "WHERE activityId = :activityId " +
            "AND sessionId > (SELECT MAX(id) - :lastNSessions FROM pf_session) " +
            "GROUP BY activityId")
    AggregateVisitTimeByActivity getAggregateVisitTimeByActivity(long activityId, int lastNSessions);
}
