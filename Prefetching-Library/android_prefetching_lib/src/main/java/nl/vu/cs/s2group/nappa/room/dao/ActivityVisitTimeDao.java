package nl.vu.cs.s2group.nappa.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import nl.vu.cs.s2group.nappa.room.data.ActivityVisitTime;
import nl.vu.cs.s2group.nappa.room.view.ActivityAggregateVisitTime;

@Dao
public interface ActivityVisitTimeDao {
    @Insert
    void insert(ActivityVisitTime activityVisitTime);

    @Query("SELECT activityId, totalDuration " +
            "FROM ActivityAggregateVisitTime " +
            "WHERE activityId = :activityId " +
            "AND sessionId > (SELECT MAX(id_session) - :lastNSessions FROM pf_activity_visit_time) " +
            "GROUP BY activityId")
    List<ActivityAggregateVisitTime> getTotalAggregatedActivityVisitTime(Long activityId, int lastNSessions);
}
