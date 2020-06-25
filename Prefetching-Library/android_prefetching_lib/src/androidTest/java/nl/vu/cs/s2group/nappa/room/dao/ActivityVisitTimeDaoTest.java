package nl.vu.cs.s2group.nappa.room.dao;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

import nl.vu.cs.s2group.nappa.room.ActivityData;
import nl.vu.cs.s2group.nappa.room.PrefetchingDatabase;
import nl.vu.cs.s2group.nappa.room.activity.visittime.ActivityVisitTime;
import nl.vu.cs.s2group.nappa.room.data.Session;
import nl.vu.cs.s2group.nappa.util.TestUtil;

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

}