package it.robertolaricchia.android_prefetching_2018.stats;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleService;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import it.robertolaricchia.android_prefetching_2018.R;
import it.robertolaricchia.android_prefetching_2018.viewmodel.ViewModelActivityList;
import it.robertolaricchia.android_prefetching_lib.*;
import it.robertolaricchia.android_prefetching_lib.PrefetchingLib;
import it.robertolaricchia.android_prefetching_lib.room.ActivityData;

public class ListActivityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_activity);

        ViewModelActivityList model = ViewModelProviders.of(this).get(ViewModelActivityList.class);

        final TextView textView = findViewById(R.id.text_activity);

        model.liveData.observe(this, dataList -> {
            StringBuilder sb = new StringBuilder();
            for (ActivityData data : dataList) {
                sb.append(data.id+": "+data.activityName+"\n\n\n");
            }
            textView.setText(sb.toString());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefetchingLib.setCurrentActivity(this);
    }
}
