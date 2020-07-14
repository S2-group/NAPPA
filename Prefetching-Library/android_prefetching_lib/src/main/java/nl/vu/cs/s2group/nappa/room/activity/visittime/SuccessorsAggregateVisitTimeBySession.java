package nl.vu.cs.s2group.nappa.room.activity.visittime;

import androidx.room.DatabaseView;

@DatabaseView(viewName = "nappa_view_successors_aggregate_visit_time_by_session",
        value = "SELECT " +
                "   activity_name AS activityName, " +
                "   from_activity AS fromActivity, " +
                "   SUM(duration) as totalDuration, " +
                "   id_session AS sessionId " +
                "FROM nappa_activity_visit_time " +
                "GROUP BY " +
                "   activity_name, " +
                "   from_activity, " +
                "   id_session ")
public class SuccessorsAggregateVisitTimeBySession {
    public String activityName;
    public String fromActivity;
    public long totalDuration;
    public long sessionId;
}
