package nl.vu.cs.s2group.nappa.sample.app.weather_and_news;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import nl.vu.cs.s2group.nappa.Nappa;
import nl.vu.cs.s2group.nappa.NappaLifecycleObserver;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new NappaLifecycleObserver(this));
        setContentView(R.layout.activity_graph);

        TextView textView = findViewById(R.id.text_graph);

        textView.setText(Nappa.getActivityGraph().toString());
    }
}
