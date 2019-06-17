package nl.vu.cs.s2group.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "pf_activity", indices = @Index(value = {"activity_name"}, unique = true))
public class ActivityData {
    @PrimaryKey(autoGenerate = true) public Long id;
    // activityName is UNIQUE, thus no two rows may have the same activity name
    @ColumnInfo(name = "activity_name") public String activityName;

    public ActivityData(String activityName) {
        this.activityName = activityName;
    }
}
