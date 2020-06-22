package nl.vu.cs.s2group.nappa.room.dao;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

        for (int i = 0; i < sessionList.size(); i++) {
            Session session = sessionList.get(i);
            db.sessionDao().insertSession(session);
            for (int j = 0; j < activityList.size(); j++) {
                ActivityData activityData = activityList.get(j);
                timeList.addAll(TestUtil.createActivityVisitTimeList(10, (long) i, (long) j));
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        db.close();
    }

    @Test
    public void insert() {
        ActivityVisitTime data = TestUtil.createActivityVisitTime(1L, 2L);
        db.activityVisitTimeDao().insert(data);
        List<ActivityVisitTime> byActivity = db.activityVisitTimeDao().getActivityVisitTime(1L);
        assertThat(byActivity.get(0).duration, equalTo(data.duration));
        assertThat(byActivity.get(0).sessionId, equalTo(data.sessionId));
        assertThat(byActivity.get(0).timestamp, equalTo(data.timestamp));
    }

    @Test
    public void testGetActivityVisitTime() {
    }

    @Test
    public void testGetActivityVisitTime1() {
    }
}