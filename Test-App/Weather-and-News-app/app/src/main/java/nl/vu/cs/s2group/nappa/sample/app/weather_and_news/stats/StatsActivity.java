package nl.vu.cs.s2group.nappa.sample.app.weather_and_news.stats;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import nl.vu.cs.s2group.nappa.NAPPALifecycleObserver;
import nl.vu.cs.s2group.nappa.room.AggregateUrlDao;
import nl.vu.cs.s2group.nappa.sample.app.weather_and_news.R;

public class StatsActivity extends AppCompatActivity {

    AggregateViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new NAPPALifecycleObserver(this));
        setContentView(R.layout.activity_stats);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView textView = findViewById(R.id.text_stats);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        viewModel = new ViewModelProvider.NewInstanceFactory().create(AggregateViewModel.class);

        viewModel.loadAggregate();

        viewModel.getLiveData().observe(this, datas -> {
            for (AggregateUrlDao.AggregateURL data : datas) {
                textView.append(data.toString() + "\n\n\n");
            }
        });

    }
}
