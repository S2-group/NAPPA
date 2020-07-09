package nl.vu.cs.s2group.nappa.room.activity.visittime;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ActivityVisitTimeDao {
    @Insert
    void insert(ActivityVisitTime activityVisitTime);


    /**
     * This query takes the Database View {@link AggregateVisitTimeBySession} with the aggregate
     * time per activity and session and filters this View to return only the rows concerning the
     * provided activity. The result is aggregated again, returning a single object containing only
     * the activity name and the total duration.
     *
     * @param activityId The ID of the activity to search for.
     * @return The total aggregate time spend in a given activity for the all recorded sessions.
     */
    @Query("SELECT " +
            "   activity_name AS activityName, " +
            "   SUM(totalDuration) AS totalDuration " +
            "FROM nappa_view_aggregate_visit_time_by_session " +
            "LEFT JOIN nappa_activity " +
            "   ON nappa_activity.id = nappa_view_aggregate_visit_time_by_session.activityId " +
            "WHERE nappa_activity.id = :activityId ")
    LiveData<AggregateVisitTimeByActivity> getAggregateVisitTimeByActivity(long activityId);

    /**
     * This query extends the query defined at {@link #getAggregateVisitTimeByActivity(long)}
     * by further filtering the view by the last N sessions. For this query, the last N sessions
     * refers to the last N sessions recorded in the Entity {@link nl.vu.cs.s2group.nappa.room.data.Session Session}.
     * <p>
     * For example, if the current sessions is Session #100 and N = 2, then sessions
     * #99 and #100 are used in this query.
     * <p>
     * This query can return empty results in the following cases:
     *
     * <ul>
     *     <li> An exception takes place after creating the session - Often during development </li>
     *     <li> The activity with {@code activityName} was not visited in the last N sessions </li>
     * </ul>
     * <p>
     * Overall, this query sacrifice data availability in favor of data freshness.
     *
     * @param activityId    The ID of the activity to search for.
     * @param lastNSessions The number N of sessions to take.
     * @return The total aggregate time spend in a given activity for the last N sessions or
     * {@code Null} if the activity was not accessed in the last N sessions.
     */
    @Nullable
    @Query("SELECT " +
            "   activity_name AS activityName, " +
            "   SUM(totalDuration) AS totalDuration " +
            "FROM nappa_view_aggregate_visit_time_by_session " +
            "LEFT JOIN nappa_activity " +
            "   ON nappa_activity.id = nappa_view_aggregate_visit_time_by_session.activityId " +
            "WHERE " +
            "   activityId = :activityId AND " +
            "   sessionId > ( " +
            "       SELECT IFNULL(MAX(id) - :lastNSessions, 0) " +
            "       FROM nappa_session " +
            "   ) ")
    LiveData<AggregateVisitTimeByActivity> getAggregateVisitTimeByActivityWithinLastNSessionsInEntitySession(long activityId, int lastNSessions);


    /**
     * This query extends the query defined at {@link #getAggregateVisitTimeByActivity(long)}
     * by further filtering the view by the last N sessions. For this query, the last N sessions
     * refers to the last N sessions that the activity with ID {@code activityId} was
     * accessed and data was registered in the Entity {@link ActivityVisitTime}.
     * <p>
     * For example, if the current session is Session #100, N = 2, and the activity was
     * last accessed in Sessions #99 and #90, them sessions #90 and #99 are used in this query.
     * <p>
     * This query differs from
     * {@link #getAggregateVisitTimeByActivityWithinLastNSessionsInEntitySession(long, int)},
     * as it will always return a non null value, unless an exception takes place when
     * first accessing the activity and before leaving it (i.e., a new activity was recorded
     * but the app crashed before recoding the time spent on it).
     * <p>
     * Overall, this query sacrifice data freshness in favor data availability.
     *
     * @param activityId    The ID of the activity to search for.
     * @param lastNSessions The number N of sessions to take.
     * @return The total aggregate time spend in a given activity for the last N sessions
     */
    @Query("SELECT " +
            "   activity_name AS activityName, " +
            "   SUM(totalDuration) AS totalDuration " +
            "FROM (" +
            "   SELECT * " +
            "   FROM nappa_view_aggregate_visit_time_by_session " +
            "   WHERE activityId = :activityId " +
            "   GROUP BY sessionId " +
            "   ORDER BY sessionId " +
            "   DESC LIMIT :lastNSessions" +
            ") " +
            "LEFT JOIN nappa_activity " +
            "   ON nappa_activity.id = activityId ")
    LiveData<AggregateVisitTimeByActivity> getAggregateVisitTimeByActivityWithinLastNSessionsInThisEntity(long activityId, int lastNSessions);


    /**
     * This query takes the Database View {@link SuccessorsAggregateVisitTimeBySession} with
     * the aggregate time per activity, source activity and session and filters this View
     * to return only the rows concerning the aggregate time from a specific activity. The
     * result is aggregated again, returning a list containing only the name of the successors
     * activities and their total duration.
     *
     * @param fromActivityId The ID of the activity used as reference to obtain the duration of
     *                       the successors nodes
     * @return A list containing the total aggregate time spend in the successor activities.
     */
    @Query("SELECT " +
            "	destinationActivityName AS activityName, " +
            "	SUM(totalDuration) AS totalDuration " +
            "FROM nappa_view_successors_aggregate_visit_time_by_session " +
            "INNER JOIN nappa_view_activity_source_destination " +
            "ON " +
            "   activityId = destinationActivityID AND " +
            "   fromActivityId = sourceActivityID " +
            "WHERE fromActivityId = :fromActivityId " +
            "GROUP BY activityId ")
    LiveData<List<AggregateVisitTimeByActivity>> getSuccessorAggregateVisitTime(long fromActivityId);

    /**
     * This query extends the query defined at {@link #getSuccessorAggregateVisitTime(long)}
     * by further filtering the view by the last N sessions.
     * <p>
     * For this query, the last N sessions refers to the last N sessions recorded in the Entity
     * {@link nl.vu.cs.s2group.nappa.room.data.Session Session}. Therefore, this query can return
     * an empty list. See {@link #getAggregateVisitTimeByActivityWithinLastNSessionsInEntitySession(long, int)}
     * for which cases this query might return an empty list.
     * <p>
     * Overall, this query sacrifice data availability in favor of data freshness.
     *
     * @param fromActivityId The ID of the activity used as reference to obtain the duration of
     *                       the successors nodes
     * @param lastNSessions  The number N of sessions to take.
     * @return A list containing the total aggregate time spend in the successor activities for
     * the last N sessions.
     */
    @Query("SELECT " +
            "	destinationActivityName AS activityName, " +
            "	SUM(totalDuration) as totalDuration " +
            "FROM nappa_view_successors_aggregate_visit_time_by_session " +
            "INNER JOIN nappa_view_activity_source_destination " +
            "ON " +
            "   activityId = destinationActivityID AND " +
            "   fromActivityId = sourceActivityID " +
            "WHERE " +
            "	fromActivityId = :fromActivityId AND " +
            "	sessionId > ( " +
            "		SELECT IFNULL(MAX(id) - :lastNSessions, 0) " +
            "		FROM nappa_session " +
            "	) " +
            "GROUP BY activityId ")
    LiveData<List<AggregateVisitTimeByActivity>> getSuccessorAggregateVisitTimeWithinLastNSessionsInEntitySession(
            long fromActivityId,
            int lastNSessions);

    /**
     * This query extends the query defined at {@link #getSuccessorAggregateVisitTime(long)}
     * by further filtering the view by the last N sessions.
     * <p>
     * For this query, the last N sessions refers to the last N sessions recorded in the View
     * {@link SuccessorsAggregateVisitTimeBySession}. Therefore, this query can always return
     * a non-empty list.
     * <p>
     * Overall, this query sacrifice data freshness in favor data availability.
     *
     * @param fromActivityId The ID of the activity used as reference to obtain the duration of
     *                       the successors nodes
     * @param lastNSessions  The number N of sessions to take.
     * @return A list containing the total aggregate time spend in the successor activities for
     * the last N sessions.
     */
    @Query("SELECT " +
            "	destinationActivityName AS activityName, " +
            "	SUM(totalDuration) as totalDuration " +
            "FROM nappa_view_successors_aggregate_visit_time_by_session " +
            "INNER JOIN nappa_view_activity_source_destination " +
            "ON " +
            "   activityId = destinationActivityID AND " +
            "   fromActivityId = sourceActivityID " +
            "WHERE " +
            "	fromActivityId = :fromActivityId AND " +
            "	sessionId > ( " +
            "		SELECT IFNULL(MAX(sessionId) - :lastNSessions, 0) " +
            "		FROM nappa_view_successors_aggregate_visit_time_by_session " +
            "	) " +
            "GROUP BY activityId ")
    LiveData<List<AggregateVisitTimeByActivity>> getSuccessorAggregateVisitTimeWithinLastNSessionsInThisEntity(
            long fromActivityId,
            int lastNSessions);
}
