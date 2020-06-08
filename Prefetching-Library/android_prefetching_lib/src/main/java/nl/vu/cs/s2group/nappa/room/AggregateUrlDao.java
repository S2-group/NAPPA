package nl.vu.cs.s2group.nappa.room;

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
            "FROM pf_url " +
            "LEFT JOIN pf_activity as a ON id_activity =  a.id " +
            "GROUP BY url, id_activity ORDER BY count DESC")
    List<AggregateURL> getAggregate();

    @Query("select url, COUNT(*) as count, avg(size) as sizeAvg, activity_name as activityName " +
            "FROM pf_url " +
            "LEFT JOIN pf_activity as a ON id_activity =  a.id " +
            "WHERE id_activity = :idActivity " +
            "GROUP BY url, id_activity ORDER BY count DESC " +
            "LIMIT :maxUrl")
    List<AggregateURL> getAggregateForIdActivity(Long idActivity, Integer maxUrl);

    @Query("select url, COUNT(*) as count, avg(size) as sizeAvg, activity_name as activityName " +
            "FROM pf_url " +
            "LEFT JOIN pf_activity as a ON id_activity =  a.id " +
            "GROUP BY url, id_activity ORDER BY count DESC")
    LiveData<List<AggregateURL>> getAggregateLiveData();



    static class AggregateURL {

        public String getUrl() {
            return url;
        }

        String url;
        Integer count;
        Long sizeAvg;
        String activityName;

        public AggregateURL(String url, Integer count, Long sizeAvg, String activityName) {
            this.url = url;
            this.count = count;
            this.sizeAvg = sizeAvg;
            this.activityName = activityName;
        }

        @Override
        public String toString() {
            return url+"\ncount: "+count+"\nsizeAvg: "+sizeAvg+" bytes"+"\nActivity_name: "+activityName+"\n\n";
        }
    }

}
