package nl.vu.cs.s2group.nappa.room.activity.visittime;

import androidx.room.DatabaseView;

@DatabaseView("SELECT id_activity AS activityId, SUM(duration) as totalDuration, id_session AS sessionId " +
        "FROM pf_activity_visit_time " +
        "GROUP BY id_session")
public class ActivityAggregateVisitTime {
    public long activityId;
    public long totalDuration;
    public Long sessionId;
}
