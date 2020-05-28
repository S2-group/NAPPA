package nl.vu.cs.s2group.nappa.sample.app.weather_and_news;

import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

import nl.vu.cs.s2group.nappa.sample.app.weather_and_news.R;
import nl.vu.cs.s2group.nappa.PrefetchingLib;

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
