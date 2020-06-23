package nl.vu.cs.s2group.nappa.sample.app.weather_and_news.stats;

import androidx.lifecycle.ViewModelProviders;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import nl.vu.cs.s2group.nappa.sample.app.weather_and_news.NAPPALifecycleObserver;
import nl.vu.cs.s2group.nappa.sample.app.weather_and_news.R;
import nl.vu.cs.s2group.nappa.sample.app.weather_and_news.viewmodel.ViewModelActivityList;
import nl.vu.cs.s2group.nappa.PrefetchingLib;
import nl.vu.cs.s2group.nappa.room.ActivityData;

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
                sb.append(data.id+": "+data.activityName+"\n\n\n");
            }
            textView.setText(sb.toString());
        });
    }


}
