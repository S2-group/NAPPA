package nl.vu.cs.s2group.room.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.annotation.NonNull;

@Entity(tableName = "pf_LAR", primaryKeys = {"activity_name"}, indices = @Index(value = {"activity_name"}, unique = true))
public class LARData {
    @NonNull @ColumnInfo(name = "activity_name") public String activity_name;
    @NonNull @ColumnInfo(name = "PR") public float PR;
    @NonNull @ColumnInfo(name = "authority") public float authority;
    @NonNull @ColumnInfo(name = "hub") public float hub;
    @NonNull @ColumnInfo(name = "authorityS") public float authorityS;
    @NonNull @ColumnInfo(name = "hubS") public float hubS;

    public LARData(@NonNull String activity_name, @NonNull float PR, @NonNull float authority, @NonNull float hub, @NonNull float authorityS, @NonNull float hubS ) {
        this.activity_name = activity_name;
        this.PR = PR;
        this.authority = authority;
        this.hub = hub;
        this.authorityS = authorityS;
        this.hubS = hubS;
    }
}