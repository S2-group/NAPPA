package it.robertolaricchia.android_prefetching_lib.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "pf_activity", indices = @Index(value = {"activity_name"}, unique = true))
public class ActivityData {
    @PrimaryKey(autoGenerate = true) public Long id;
    @ColumnInfo(name = "activity_name") public String activityName;

    public ActivityData(String activityName) {
        this.activityName = activityName;
    }
}
