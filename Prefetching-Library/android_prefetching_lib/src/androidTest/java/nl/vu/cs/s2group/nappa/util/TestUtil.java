package nl.vu.cs.s2group.nappa.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import nl.vu.cs.s2group.nappa.room.ActivityData;
import nl.vu.cs.s2group.nappa.room.activity.visittime.ActivityVisitTime;
import nl.vu.cs.s2group.nappa.room.data.Session;

public class TestUtil {
    private static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static ActivityData createActivity(String activityName) {
        return new ActivityData(activityName);
    }

    @NotNull
    @Contract("_, _ -> new")
    public static ActivityVisitTime createActivityVisitTime(Long activityId, Long sessionId) {
        return new ActivityVisitTime(activityId, sessionId, new Date(), new Random().nextInt(60) * 1000L);
    }

    @NotNull
    public static List<ActivityVisitTime> createActivityVisitTimeList(int entriesToCreate, Long activityId, Long sessionId) {
        List<ActivityVisitTime> list = new ArrayList<>();

        for (int i = 0; i < entriesToCreate; i++) {
            list.add(createActivityVisitTime(activityId, sessionId));
        }

        return list;
    }

    @NotNull
    public static List<ActivityData> createActivityList(int entriesToCreate) {
        List<ActivityData> list = new ArrayList<>();

        for (int i = 0; i < entriesToCreate; i++) {
            list.add(createActivity("com.test.activity" + i));
        }

        return list;
    }

    @NotNull
    @Contract(" -> new")
    public static Session createSession() {
        return new Session(new Date().getTime());
    }

    @NotNull
    @Contract("_ -> new")
    public static Session createSession(@NotNull Date date) {
        return new Session(date.getTime());
    }

    @NotNull
    public static List<Session> createSessionList(int entriesToCreate) {
        List<Session> list = new ArrayList<>();
        Date date = new Date();

        for (int i = 0; i < entriesToCreate; i++) {
            list.add(createSession(date));
            date = new Date(date.getTime() - MILLIS_IN_A_DAY);
        }
        return list;
    }
}
