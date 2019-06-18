package nl.vu.cs.s2group.android_prefetching_2018;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import nl.vu.cs.s2group.android_prefetching_2018.R;
import nl.vu.cs.s2group.PrefetchingLib;

public class GraphActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        TextView textView = findViewById(R.id.text_graph);

        textView.setText(PrefetchingLib.getActivityGraph().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefetchingLib.setCurrentActivity(this);
    }
}
