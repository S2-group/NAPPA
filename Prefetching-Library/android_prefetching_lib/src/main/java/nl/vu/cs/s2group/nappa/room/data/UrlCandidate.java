package nl.vu.cs.s2group.nappa.room.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents an individual URL candidate
 */
@Entity(tableName = "nappa_url_candidate")
public class UrlCandidate {

    @PrimaryKey(autoGenerate = true) public Long id;

    public UrlCandidate(Long idActivity, Integer count) {
        this.idActivity = idActivity;
        this.count = count;
    }

    @ColumnInfo(name = "id_activity") public Long idActivity;
    @ColumnInfo(name = "count") public Integer count;
}
