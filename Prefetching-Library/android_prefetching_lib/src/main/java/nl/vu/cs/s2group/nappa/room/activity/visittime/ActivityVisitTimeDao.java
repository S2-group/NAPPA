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
     * <p>
     * The more complex subquery is required instead of a simple {@code (SELECT MAX(id) -
     * :lastNSessions FROM pf_session)}, because orphan sessions (i.e., sessions without data)
     * can be registered when:
     *
     * <ul>
     *     <li> An exception takes place after creating the session - Often during development </li>
     *     <li> The activity with {@code activityName} was not visited in the last N sessions </li>
     * </ul>
     * <p>
     * To address this, the inner subquery filter the view {@link AggregateVisitTimeBySession}
     * by the queried activity and takes the last 5 entries, representing the last 5 sessions
     * where this activity was accessed. These 5 sessions can be different than the actual last
     * five recorded sessions. Then the first query simply takes the subquery result and aggregate
     * the total duration to return a single row.
     *
     * @param activityName  The activity to search for
     * @param lastNSessions The number N of sessions to take, where N represents the last N
     *                      sessions that this activity was accessed
     * @return The total aggregate time spend in a given activity for the last N sessions
     */
    @Query("SELECT activityName, SUM(totalDuration) as totalDuration " +
            "FROM (" +
            "   SELECT * " +
            "   FROM AggregateVisitTimeBySession " +
            "   WHERE activityName = :activityName " +
            "   GROUP BY sessionId " +
            "   ORDER BY sessionId " +
            "   DESC LIMIT :lastNSessions" +
            ")")
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
