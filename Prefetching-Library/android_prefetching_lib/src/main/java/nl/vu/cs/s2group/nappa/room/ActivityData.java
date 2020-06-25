package nl.vu.cs.s2group.nappa.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "pf_activity", indices = @Index(value = {"activity_name"}, unique = true))
public class ActivityData {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    /**
     * Represent the canonical name of the underlying Activity class as defined by
     * the Java Language Specification
     */
    @NonNull
    @ColumnInfo(name = "activity_name")
    public String activityName;

    public ActivityData(@NonNull String activityName) {
        this.activityName = activityName;
    }
}
