package nl.vu.cs.s2group.nappa.room.activity.visittime;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ActivityVisitTimeDao {
    @Insert
    void insert(ActivityVisitTime activityVisitTime);

    @Query("SELECT activityId, totalDuration " +
            "FROM AggregateVisitTimeBySession " +
            "WHERE activityId = :activityId " +
            "AND sessionId > (SELECT MAX(id_session) - :lastNSessions FROM pf_activity_visit_time) " +
            "GROUP BY activityId")
    List<AggregateVisitTimeByActivity> getTotalAggregatedActivityVisitTime(Long activityId, int lastNSessions);
}
