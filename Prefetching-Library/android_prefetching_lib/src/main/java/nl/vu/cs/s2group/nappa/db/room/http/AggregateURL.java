package nl.vu.cs.s2group.nappa.db.room.http;

public class AggregateURL {

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
