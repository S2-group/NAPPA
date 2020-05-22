package nl.vu.cs.s2group.android_prefetching_2018.stats;

import androidx.lifecycle.ViewModelProviders;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import nl.vu.cs.s2group.android_prefetching_2018.R;
import nl.vu.cs.s2group.android_prefetching_2018.viewmodel.ViewModelSessionDataList;
import nl.vu.cs.s2group.PrefetchingLib;
import nl.vu.cs.s2group.room.data.SessionData;

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
