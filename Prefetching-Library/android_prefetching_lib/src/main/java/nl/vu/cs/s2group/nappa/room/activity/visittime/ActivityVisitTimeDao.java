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
     * @param activityName The activity to search for.
     * @return The total aggregate time spend in a given activity for the all recorded sessions.
     */
    @Query("SELECT " +
            "   activityName, " +
            "   SUM(totalDuration) as totalDuration " +
            "FROM AggregateVisitTimeBySession " +
            "WHERE activityName = :activityName ")
    LiveData<AggregateVisitTimeByActivity> getAggregateVisitTimeByActivity(String activityName);

    /**
     * This query extends the query defined at {@link #getAggregateVisitTimeByActivity(String)}
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
     * @param activityName  The activity to search for.
     * @param lastNSessions The number N of sessions to take.
     * @return The total aggregate time spend in a given activity for the last N sessions or
     * {@code Null} if the activity was not accessed in the last N sessions.
     */
    @Nullable
    @Query("SELECT " +
            "   activityName, " +
            "   SUM(totalDuration) as totalDuration " +
            "FROM AggregateVisitTimeBySession " +
            "WHERE activityName = :activityName " +
            "AND sessionId > ( " +
            "   SELECT MAX(id) - :lastNSessions " +
            "   FROM pf_session " +
            ") ")
    LiveData<AggregateVisitTimeByActivity> getAggregateVisitTimeByActivityWithinLastNSessionsInEntitySession(String activityName, int lastNSessions);


    /**
     * This query extends the query defined at {@link #getAggregateVisitTimeByActivity(String)}
     * by further filtering the view by the last N sessions. For this query, the last N sessions
     * refers to the last N sessions that the activity with name {@code activityName} was
     * accessed and data was registered in the Entity {@link ActivityVisitTime}.
     * <p>
     * For example, if the current session is Session #100, N = 2, and the activity was
     * last accessed in Sessions #99 and #90, them sessions #90 and #99 are used in this query.
     * <p>
     * This query differs from
     * {@link #getAggregateVisitTimeByActivityWithinLastNSessionsInEntitySession(String, int)},
     * as it will always return a non null value, unless an exception takes place when
     * first accessing the activity and before leaving it (i.e., a new activity was recorded
     * but the app crashed before recoding the time spent on it).
     * <p>
     * Overall, this query sacrifice data freshness in favor data availability.
     *
     * @param activityName  The activity to search for.
     * @param lastNSessions The number N of sessions to take.
     * @return The total aggregate time spend in a given activity for the last N sessions
     */
    @Query("SELECT " +
            "   activityName, " +
            "   SUM(totalDuration) as totalDuration " +
            "FROM (" +
            "   SELECT * " +
            "   FROM AggregateVisitTimeBySession " +
            "   WHERE activityName = :activityName " +
            "   GROUP BY sessionId " +
            "   ORDER BY sessionId " +
            "   DESC LIMIT :lastNSessions" +
            ") ")
    LiveData<AggregateVisitTimeByActivity> getAggregateVisitTimeByActivityWithinLastNSessionsInThisEntity(String activityName, int lastNSessions);


    /**
     * This query takes the Database View {@link SuccessorsAggregateVisitTimeBySession} with
     * the aggregate time per activity, source activity and session and filters this View
     * to return only the rows concerning the aggregate time from a specific activity. The
     * result is aggregated again, returning a list containing only the name of the successors
     * activities and their total duration.
     *
     * @param fromActivity The name of the activity used as reference to obtain the duration of
     *                     the successors nodes
     * @return A list containing the total aggregate time spend in the successor activities.
     */
    @Query("SELECT " +
            "	activityName, " +
            "	SUM(totalDuration) as totalDuration " +
            "FROM pf_view_successors_aggregate_visit_time_by_session " +
            "WHERE fromActivity = :fromActivity " +
            "GROUP BY activityName ")
    LiveData<List<AggregateVisitTimeByActivity>> getSuccessorAggregateVisitTime(String fromActivity);

    /**
     * This query extends the query defined at {@link #getSuccessorAggregateVisitTime(String)}
     * by further filtering the view by the last N sessions.
     * <p>
     * For this query, the last N sessions refers to the last N sessions recorded in the Entity
     * {@link nl.vu.cs.s2group.nappa.room.data.Session Session}. Therefore, this query can return
     * an empty list. See {@link #getAggregateVisitTimeByActivityWithinLastNSessionsInEntitySession(String, int)}
     * for which cases this query might return an empty list.
     * <p>
     * Overall, this query sacrifice data availability in favor of data freshness.
     *
     * @param fromActivity  The name of the activity used as reference to obtain the duration of
     *                      the successors nodes
     * @param lastNSessions The number N of sessions to take.
     * @return A list containing the total aggregate time spend in the successor activities for
     * the last N sessions.
     */
    @Query("SELECT " +
            "	activityName, " +
            "	SUM(totalDuration) as totalDuration " +
            "FROM pf_view_successors_aggregate_visit_time_by_session " +
            "WHERE " +
            "	fromActivity = :fromActivity " +
            "AND sessionId > ( " +
            "   SELECT MAX(id) - :lastNSessions " +
            "   FROM pf_session " +
            ") " +
            "GROUP BY activityName ")
    LiveData<List<AggregateVisitTimeByActivity>> getSuccessorAggregateVisitTimeWithinLastNSessionsInEntitySession(
            String fromActivity,
            int lastNSessions);

    /**
     * This query extends the query defined at {@link #getSuccessorAggregateVisitTime(String)}
     * by further filtering the view by the last N sessions.
     * <p>
     * For this query, the last N sessions refers to the last N sessions recorded in the View
     * {@link SuccessorsAggregateVisitTimeBySession}. Therefore, this query can always return
     * a non-empty list.
     * <p>
     * Overall, this query sacrifice data freshness in favor data availability.
     *
     * @param fromActivity  The name of the activity used as reference to obtain the duration of
     *                      the successors nodes
     * @param lastNSessions The number N of sessions to take.
     * @return A list containing the total aggregate time spend in the successor activities for
     * the last N sessions.
     */
    @Query("SELECT " +
            "	activityName, " +
            "	SUM(totalDuration) as totalDuration " +
            "FROM pf_view_successors_aggregate_visit_time_by_session " +
            "WHERE " +
            "	fromActivity = :fromActivity " +
            "AND sessionId > ( " +
            "   SELECT MAX(sessionId) - :lastNSessions " +
            "   FROM pf_view_successors_aggregate_visit_time_by_session " +
            ") " +
            "GROUP BY activityName ")
    LiveData<List<AggregateVisitTimeByActivity>> getSuccessorAggregateVisitTimeWithinLastNSessionsInThisEntity(
            String fromActivity,
            int lastNSessions);
}
