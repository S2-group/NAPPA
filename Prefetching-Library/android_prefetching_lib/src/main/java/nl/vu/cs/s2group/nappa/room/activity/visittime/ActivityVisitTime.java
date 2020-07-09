package nl.vu.cs.s2group.nappa.room.activity.visittime;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Represents the database table that register the time a user spends visiting activities
 */
@Entity(tableName = "nappa_activity_visit_time",
        indices = {
                @Index("activity_name"),
                @Index("from_activity"),
                @Index("id_session"),
        })
public class ActivityVisitTime {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "id_activity")
    public long activityId;

    /**
     * The previous activity can be null for the first activity accessed when launching the app
     */
    @ColumnInfo(name = "id_from_activity")
    public Long fromActivityId;

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

    public ActivityVisitTime(long activityId,
                             Long fromActivityId,
                             long sessionId,
                             @NonNull Date timestamp,
                             long duration) {
        this.activityId = activityId;
        this.fromActivityId = fromActivityId;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.duration = duration;
    }
}
