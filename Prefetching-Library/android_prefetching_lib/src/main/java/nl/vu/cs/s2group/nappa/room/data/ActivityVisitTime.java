package nl.vu.cs.s2group.nappa.room.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Represents the database table that register the time a user spends visiting activities
 */
@Entity(tableName = "pf_activity_visit_time")
public class ActivityVisitTime {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "id_activity")
    public long activityId;

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

    public ActivityVisitTime(long activityId, long sessionId, @NotNull Date timestamp, long duration) {
        this.activityId = activityId;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.duration = duration;
    }
}
