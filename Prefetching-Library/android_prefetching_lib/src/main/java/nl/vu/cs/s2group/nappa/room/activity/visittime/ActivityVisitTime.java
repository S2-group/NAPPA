package nl.vu.cs.s2group.nappa.room.activity.visittime;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Represents the database table that register the time a user spends visiting activities
 */
@Entity(tableName = "pf_activity_visit_time")
public class ActivityVisitTime {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    @ColumnInfo(name = "activity_name")
    public String activityName;

    @ColumnInfo(name = "id_session")
    public long sessionId;

    /**
     * The date this activity was accessed
     */
    @NonNull
    public Date timestamp;

    /**
     * The visit duration in millisecond
     */
    public long duration;

    public ActivityVisitTime(@NonNull String activityName, long sessionId, @NonNull Date timestamp, long duration) {
        this.activityName = activityName;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.duration = duration;
    }
}
