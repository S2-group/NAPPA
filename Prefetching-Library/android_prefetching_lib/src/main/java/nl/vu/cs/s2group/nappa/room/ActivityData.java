package nl.vu.cs.s2group.nappa.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "pf_activity",
        indices = @Index(value = {"activity_name"}, unique = true))
public class ActivityData {
    /**
     * Represent the canonical name of the underlying Activity class as defined by
     * the Java Language Specification
     */
    @PrimaryKey
    @ColumnInfo(name = "activity_name")
    public String activityName;

    public ActivityData(String activityName) {
        this.activityName = activityName;
    }
}
