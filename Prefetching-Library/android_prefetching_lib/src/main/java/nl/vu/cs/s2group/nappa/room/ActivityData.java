package nl.vu.cs.s2group.nappa.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// TODO Replace the activity ID for the name as this Entity PK
//  The activity name represents the canonical name of the activity
//  (e.g., `com.myapp.MyActivity`), which means that the activity name is unique.
//  As such, there is not need to have an auto incremental ID as the PK.
//  Changing this Entity, however, means refactoring all Entities that reference this entity
//  using the ID and all classes that keeps this data in memory.
//  The benefit in doing this refactor are:
//  * Eliminate the maps `ID -> name` and `name -> ID` present in the library
//  * Eliminates the need to call getActivityNameById() and getActivityIdByName() --> actual names are different
//  * Improve logs and database debugging --> Logging the message `Navigating from activity 1 to 6` does not adds value
//  * Eliminates the need of performing `JOIN` operations with this table to get the activity name
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
