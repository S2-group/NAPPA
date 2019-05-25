package it.robertolaricchia.android_prefetching_lib.room.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity(tableName = "pf_PR", primaryKeys = "activity_name")
public class PRData {
    @NonNull @ColumnInfo(name = "activity_name") public String activity_name;
    @NonNull @ColumnInfo(name = "PR") public float PR;

    public PRData(@NonNull String activity_name, @NonNull float PR) {
        this.activity_name = activity_name;
        this.PR = PR;
    }
}