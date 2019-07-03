package nl.vu.cs.s2group.room.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pf_session")
public class Session {

    @PrimaryKey(autoGenerate = true) public Long id;
    @ColumnInfo(name = "date") public Long date;

    public Session(Long date) {
        this.date = date;
    }
}
