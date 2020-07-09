package nl.vu.cs.s2group.nappa.room.activity.visittime;

import androidx.room.DatabaseView;

@DatabaseView(viewName = "nappa_view_aggregate_visit_time_by_session",
        value = "SELECT " +
                "   id_activity AS activityId, " +
                "   SUM(duration) as totalDuration, " +
                "   id_session AS sessionId " +
                "FROM nappa_activity_visit_time " +
                "GROUP BY " +
                "   id_activity, " +
                "   id_session ")
public class AggregateVisitTimeBySession {
    public long activityId;
    public long totalDuration;
    public long sessionId;
}
