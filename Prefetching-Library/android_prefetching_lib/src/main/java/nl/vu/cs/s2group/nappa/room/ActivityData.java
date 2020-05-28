package nl.vu.cs.s2group.nappa.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "pf_activity", indices = @Index(value = {"activity_name"}, unique = true))
public class ActivityData {
    @PrimaryKey(autoGenerate = true) public Long id;
    // activityName is UNIQUE, thus no two rows may have the same activity name
    @ColumnInfo(name = "activity_name") public String activityName;

    public ActivityData(String activityName) {
        this.activityName = activityName;
    }
}
