package it.robertolaricchia.android_prefetching_2018.stats;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import it.robertolaricchia.android_prefetching_2018.R;
import it.robertolaricchia.android_prefetching_2018.viewmodel.ViewModelSessionDataList;
import it.robertolaricchia.android_prefetching_lib.*;
import it.robertolaricchia.android_prefetching_lib.room.data.SessionData;

public class SessionDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_data);

        final TextView textView = findViewById(R.id.text_session_data);

        ViewModelSessionDataList sessionDataList = ViewModelProviders.of(this).get(ViewModelSessionDataList.class);

        sessionDataList.listLiveData.observe(this, sessionData -> {
            StringBuilder sb = new StringBuilder();
            for (SessionData data : sessionData) {
                sb.append(data.toString()).append("\n\n");
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
