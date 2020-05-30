package nl.vu.cs.s2group.nappa.db.room.graph;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "nappa_activity", indices = @Index(value = {"activity_name"}, unique = true))
public class Activity {
    @PrimaryKey(autoGenerate = true) public Long id;
    // activityName is UNIQUE, thus no two rows may have the same activity name
    @ColumnInfo(name = "activity_name") public String activityName;

    public Activity(String activityName) {
        this.activityName = activityName;
    }
}
