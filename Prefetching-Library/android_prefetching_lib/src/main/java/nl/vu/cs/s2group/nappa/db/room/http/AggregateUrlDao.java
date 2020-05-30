package nl.vu.cs.s2group.nappa.db.room.http;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AggregateUrlDao {

    @Insert
    void insert(RequestData data);

    @Query("select url, COUNT(*) as count, avg(size) as sizeAvg, activity_name as activityName " +
            "FROM nappa_url " +
            "LEFT JOIN nappa_activity as a ON id_activity =  a.id " +
            "GROUP BY url, id_activity ORDER BY count DESC")
    List<AggregateURL> getAggregate();

    @Query("select url, COUNT(*) as count, avg(size) as sizeAvg, activity_name as activityName " +
            "FROM nappa_url " +
            "LEFT JOIN nappa_activity as a ON id_activity =  a.id " +
            "WHERE id_activity = :idActivity " +
            "GROUP BY url, id_activity ORDER BY count DESC " +
            "LIMIT :maxUrl")
    List<AggregateURL> getAggregateForIdActivity(Long idActivity, Integer maxUrl);

    //@Query("select url, COUNT(*) as count, avg(size) as sizeAvg from nappa_url GROUP BY url ORDER BY count DESC")
    @Query("select url, COUNT(*) as count, avg(size) as sizeAvg, activity_name as activityName " +
            "FROM nappa_url " +
            "LEFT JOIN nappa_activity as a ON id_activity =  a.id " +
            "GROUP BY url, id_activity ORDER BY count DESC")
    LiveData<List<AggregateURL>> getAggregateLiveData();


}
