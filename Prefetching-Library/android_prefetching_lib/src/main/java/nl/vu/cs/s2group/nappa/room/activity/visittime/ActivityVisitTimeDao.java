package nl.vu.cs.s2group.nappa.room.activity.visittime;

import androidx.lifecycle.LiveData;
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
     * single object containing only the activity name and the total duration
     *
     * @param activityName  The activity to search for
     * @param lastNSessions The number of sessions to take, starting from the current session and before
     * @return The total aggregate time spend in a given activity for the last N sessions
     */
    @Query("SELECT activityName, totalDuration " +
            "FROM AggregateVisitTimeBySession " +
            "WHERE activityName = :activityName " +
            "AND sessionId > (SELECT MAX(id) - :lastNSessions FROM pf_session) " +
            "GROUP BY activityName")
    LiveData<AggregateVisitTimeByActivity> getAggregateVisitTimeByActivity(String activityName, int lastNSessions);

    /**
     * Same as {@link ActivityVisitTimeDao#getAggregateVisitTimeByActivity(String, int)} but taking
     * data from all recorded sessions instead of the last N sessions
     *
     * @param activityName The activity to search for
     * @return The total aggregate time spend in a given activity for the all recorded sessions
     */
    @Query("SELECT activityName, totalDuration " +
            "FROM AggregateVisitTimeBySession " +
            "WHERE activityName = :activityName " +
            "GROUP BY activityName")
    LiveData<AggregateVisitTimeByActivity> getAggregateVisitTimeByActivity(String activityName);
}
