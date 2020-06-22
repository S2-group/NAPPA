package nl.vu.cs.s2group.nappa.room.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * Represents the database table that register the time a user spends visiting activities
 */
@Entity(tableName = "pf_activity_visit_time")
public class ActivityVisitTime {
    @PrimaryKey
    public Long id;

    @ColumnInfo(name = "id_activity")
    public Long idActivity;

    @ColumnInfo(name = "id_session")
    public Long idSession;

    /**
     * The date this activity was accessed
     */
    public Date timestamp;

    /**
     * The visit duration in millisecond
     */
    public Long duration;
}
