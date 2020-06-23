package nl.vu.cs.s2group.nappa;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class NAPPALifecycleObserver implements LifecycleObserver {
    private static final String LOG_TAG = NAPPALifecycleObserver.class.getSimpleName();

    private Activity activity;

    public NAPPALifecycleObserver(Activity activity) {
        this.activity = activity;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        Log.d(LOG_TAG, activity.getClass().getCanonicalName() + " - onResume");
        PrefetchingLib.setCurrentActivity(activity);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        Log.d(LOG_TAG, activity.getClass().getCanonicalName() + " - onPause");
        PrefetchingLib.leavingCurrentActivity();
    }
}