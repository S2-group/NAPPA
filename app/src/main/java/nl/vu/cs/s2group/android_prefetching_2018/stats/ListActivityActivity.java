package nl.vu.cs.s2group.android_prefetching_2018.stats;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import nl.vu.cs.s2group.android_prefetching_2018.R;
import nl.vu.cs.s2group.android_prefetching_2018.viewmodel.ViewModelActivityList;
import nl.vu.cs.s2group.PrefetchingLib;
import nl.vu.cs.s2group.room.ActivityData;

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
