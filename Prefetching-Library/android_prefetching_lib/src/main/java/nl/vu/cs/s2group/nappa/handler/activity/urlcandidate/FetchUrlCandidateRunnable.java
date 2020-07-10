package nl.vu.cs.s2group.nappa.handler.activity.urlcandidate;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.room.NappaDB;
import nl.vu.cs.s2group.nappa.room.dao.UrlCandidateDao;

public class FetchUrlCandidateRunnable implements Runnable {
    private static final String LOG_TAG = FetchUrlCandidateRunnable.class.getSimpleName();

    ActivityNode activity;

    public FetchUrlCandidateRunnable(ActivityNode activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        Log.d(LOG_TAG, activity.getActivitySimpleName() + " Fetching URL candidates");

        LiveData<List<UrlCandidateDao.UrlCandidateToUrlParameter>> liveData = NappaDB.getInstance()
                .urlCandidateDao()
                .getCandidatePartsListLiveDataForActivity(activity.getActivityId());

        new Handler(Looper.getMainLooper()).post(() -> activity.setUrlCandidateDbLiveData(liveData));

    }
}
