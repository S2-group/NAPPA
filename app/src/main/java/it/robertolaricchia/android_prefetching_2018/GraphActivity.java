package it.robertolaricchia.android_prefetching_2018;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;
import android.widget.TextView;

import it.robertolaricchia.android_prefetching_lib.*;
import it.robertolaricchia.android_prefetching_lib.PrefetchingLib;

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
