package nl.vu.cs.s2group.nappa.db.room.graph;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.Contract;

@Entity(tableName = "pf_activity_extra")
public class ActivityExtra {

    @PrimaryKey(autoGenerate = true) public Long id;
    @ColumnInfo(name = "id_session") public Long idSession;
    @ColumnInfo(name = "id_activity") public Long idActivity;
    @ColumnInfo(name = "key") public String key;
    @ColumnInfo(name = "value") public String value;

    @Contract(pure = true)
    public ActivityExtra(Long idSession, Long idActivity, String key, String value) {
        this.idSession = idSession;
        this.idActivity = idActivity;
        this.key = key;
        this.value = value;
    }
}
