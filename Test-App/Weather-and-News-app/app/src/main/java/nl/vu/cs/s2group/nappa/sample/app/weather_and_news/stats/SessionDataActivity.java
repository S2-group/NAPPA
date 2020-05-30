package nl.vu.cs.s2group.nappa.sample.app.weather_and_news.stats;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import nl.vu.cs.s2group.nappa.PrefetchingLib;
import nl.vu.cs.s2group.nappa.db.room.session.SessionContent;
import nl.vu.cs.s2group.nappa.sample.app.weather_and_news.R;
import nl.vu.cs.s2group.nappa.sample.app.weather_and_news.viewmodel.ViewModelSessionDataList;

public class SessionDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_data);

        final TextView textView = findViewById(R.id.text_session_data);

        ViewModelSessionDataList sessionDataList = ViewModelProviders.of(this).get(ViewModelSessionDataList.class);

        sessionDataList.listLiveData.observe(this, sessionData -> {
            StringBuilder sb = new StringBuilder();
            for (SessionContent data : sessionData) {
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
