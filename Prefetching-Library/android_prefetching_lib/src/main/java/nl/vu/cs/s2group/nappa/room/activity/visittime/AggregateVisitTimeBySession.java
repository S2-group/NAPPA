package nl.vu.cs.s2group.nappa.room.activity.visittime;

import androidx.room.DatabaseView;

@DatabaseView(viewName = "nappa_view_aggregate_visit_time_by_session",
        value = "SELECT " +
                "   activity_name AS activityName, " +
                "   SUM(duration) as totalDuration, " +
                "   id_session AS sessionId " +
                "FROM nappa_activity_visit_time " +
                "GROUP BY " +
                "   activity_name, " +
                "   id_session ")
public class AggregateVisitTimeBySession {
    public String activityName;
    public long totalDuration;
    public long sessionId;
}
