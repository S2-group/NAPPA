package it.robertolaricchia.android_prefetching_lib.room.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Represents an individual URL candidate
 */
@Entity(tableName = "pf_url_candidate")
public class UrlCandidate {

    @PrimaryKey(autoGenerate = true) public Long id;

    public UrlCandidate(Long idActivity, Integer count) {
        this.idActivity = idActivity;
        this.count = count;
    }

    @ColumnInfo(name = "id_activity") public Long idActivity;
    @ColumnInfo(name = "count") public Integer count;
}
