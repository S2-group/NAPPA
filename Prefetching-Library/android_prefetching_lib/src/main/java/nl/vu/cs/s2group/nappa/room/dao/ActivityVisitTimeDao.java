package nl.vu.cs.s2group.nappa.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import nl.vu.cs.s2group.nappa.room.data.ActivityVisitTime;

@Dao
public interface ActivityVisitTimeDao {
    @Insert
    void insert(ActivityVisitTime activityVisitTime);

    @Query("SELECT * " +
            "FROM pf_activity_visit_time " +
            "WHERE id_activity = :activityId")
    List<ActivityVisitTime> getActivityVisitTime(Long activityId);

    @Query("SELECT * " +
            "FROM pf_activity_visit_time " +
            "WHERE id_activity = :activityId " +
            "AND id_session = :sessionId")
    List<ActivityVisitTime> getActivityVisitTime(Long activityId, Long sessionId);

    @Query("SELECT * " +
            "FROM pf_activity_visit_time " +
            "WHERE id_activity = :activityId " +
            "AND id_session > (SELECT MAX(id_session) - :lastNSessions FROM pf_activity_visit_time) ")
    List<ActivityVisitTime> getActivityVisitTime(Long activityId, int lastNSessions);

    @Query("SELECT id_activity, id_session, SUM(duration) as duration " +
            "FROM pf_activity_visit_time " +
            "WHERE id_activity = :activityId " +
            "AND id_session > (SELECT MAX(id_session) - :lastNSessions FROM pf_activity_visit_time) " +
            "GROUP BY id_session")
    List<ActivityVisitTime> getAggregatedActivityVisitTime(Long activityId, int lastNSessions);

    @Query("SELECT id_activity, SUM(duration) as duration " +
            "FROM pf_activity_visit_time " +
            "WHERE id_activity = :activityId " +
            "AND id_session > (SELECT MAX(id_session) - :lastNSessions FROM pf_activity_visit_time) " +
            "GROUP BY id_activity")
    List<ActivityVisitTime> getTotalAggregatedActivityVisitTime(Long activityId, int lastNSessions);
}
