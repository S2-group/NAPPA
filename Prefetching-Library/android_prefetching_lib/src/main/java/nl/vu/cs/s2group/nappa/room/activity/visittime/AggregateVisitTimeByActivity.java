package nl.vu.cs.s2group.nappa.room.activity.visittime;

import java.util.Objects;

public class AggregateVisitTimeByActivity {
    public String activityName;
    public long totalDuration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateVisitTimeByActivity that = (AggregateVisitTimeByActivity) o;
        return totalDuration == that.totalDuration &&
                activityName.equals(that.activityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityName, totalDuration);
    }
}
