package nl.vu.cs.s2group.nappa.sample.app.weather_and_news.stats;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import nl.vu.cs.s2group.nappa.NAPPALifecycleObserver;
import nl.vu.cs.s2group.nappa.room.ActivityData;
import nl.vu.cs.s2group.nappa.sample.app.weather_and_news.R;
import nl.vu.cs.s2group.nappa.sample.app.weather_and_news.viewmodel.ViewModelActivityList;

public class ListActivityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new NAPPALifecycleObserver(this));
        setContentView(R.layout.activity_list_activity);

        ViewModelActivityList model = ViewModelProviders.of(this).get(ViewModelActivityList.class);

        final TextView textView = findViewById(R.id.text_activity);

        model.liveData.observe(this, dataList -> {
            StringBuilder sb = new StringBuilder();
            for (ActivityData data : dataList) {
                sb.append(data.id + ": " + data.activityName + "\n\n\n");
            }
            textView.setText(sb.toString());
        });
    }


}
