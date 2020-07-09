package nl.vu.cs.s2group.nappa.room.activity.visittime;

import androidx.room.DatabaseView;

@DatabaseView(viewName = "nappa_view_successors_aggregate_visit_time_by_session",
        value = "SELECT " +
                "   id_activity AS activityId, " +
                "   id_from_activity AS fromActivityId, " +
                "   SUM(duration) as totalDuration, " +
                "   id_session AS sessionId " +
                "FROM nappa_activity_visit_time " +
                "GROUP BY " +
                "   id_activity, " +
                "   id_from_activity, " +
                "   id_session ")
public class SuccessorsAggregateVisitTimeBySession {
    public long activityId;
    public long fromActivityId;
    public long totalDuration;
    public long sessionId;
}
