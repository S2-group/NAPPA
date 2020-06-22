package nl.vu.cs.s2group.nappa.room.dao;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import nl.vu.cs.s2group.nappa.room.ActivityData;
import nl.vu.cs.s2group.nappa.room.PrefetchingDatabase;
import nl.vu.cs.s2group.nappa.room.data.ActivityVisitTime;
import nl.vu.cs.s2group.nappa.room.data.Session;
import nl.vu.cs.s2group.nappa.util.TestUtil;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ActivityVisitTimeDaoTest {
    private PrefetchingDatabase db;
    private List<Session> sessionList;
    private List<ActivityData> activityList;
    private List<ActivityVisitTime> timeList;

    @Before
    public void setUp() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, PrefetchingDatabase.class).build();
        sessionList = TestUtil.createSessionList(3);
        activityList = TestUtil.createActivityList(4);
        timeList = new ArrayList<>();

        for (Session session : sessionList) {
            db.sessionDao().insertSession(session);
        }

        for (ActivityData activityData : activityList) {
            db.activityDao().insert(activityData);
        }

        for (int i = 0; i < sessionList.size(); i++) {
            for (int j = 0; j < activityList.size(); j++) {
                timeList.addAll(TestUtil.createActivityVisitTimeList(10, (long) i + 1, (long) j + 1));
            }
        }

        for (ActivityVisitTime activityVisitTime : timeList) {
            db.activityVisitTimeDao().insert(activityVisitTime);
        }
    }

    @After
    public void tearDown() throws Exception {
        db.close();
    }

    @Test
    public void testGetActivityVisitTimeByActivity() {
        List<ActivityVisitTime> result = db.activityVisitTimeDao().getActivityVisitTime(1L);
        assertThat(result.size(), equalTo(activityList.size() * 10));
        assertThat(result.get(0).timestamp, equalTo(timeList.get(0).timestamp));
        assertThat(result.get(0).duration, equalTo(timeList.get(0).duration));
    }

    @Test
    public void testGetActivityVisitTimeByActivityAndSession() {
        List<ActivityVisitTime> result = db.activityVisitTimeDao().getActivityVisitTime(1L, 1L);
        assertThat(result.size(), equalTo(10));
        assertThat(result.get(0).timestamp, equalTo(timeList.get(0).timestamp));
        assertThat(result.get(0).duration, equalTo(timeList.get(0).duration));
    }

    @Test
    public void testGetActivityVisitTimeByActivityAndLastSession() {
        List<ActivityVisitTime> result = db.activityVisitTimeDao().getActivityVisitTime(1L, 2);
        List<ActivityVisitTime> test = db.activityVisitTimeDao().getActivityVisitTime(1L).stream().filter(obj -> obj.sessionId >= 3).collect(Collectors.toList());
        assertThat(result.size(), equalTo(20));
        assertThat(result.get(0).timestamp, equalTo(timeList.get(0).timestamp));
        assertThat(result.get(0).duration, equalTo(timeList.get(0).duration));
    }
}