package it.robertolaricchia.android_prefetching_lib.room.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "pf_session")
public class Session {

    @PrimaryKey(autoGenerate = true) public Long id;
    @ColumnInfo(name = "date") public Long date;

    public Session(Long date) {
        this.date = date;
    }
}
