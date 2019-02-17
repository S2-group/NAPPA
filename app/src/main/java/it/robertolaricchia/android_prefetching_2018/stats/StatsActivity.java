package it.robertolaricchia.android_prefetching_2018.stats;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.Iterator;

import it.robertolaricchia.android_prefetching_2018.R;
import it.robertolaricchia.android_prefetching_lib.*;
import it.robertolaricchia.android_prefetching_lib.room.AggregateUrlDao;

public class StatsActivity extends AppCompatActivity {

    AggregateViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView textView = findViewById(R.id.text_stats);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        viewModel = new ViewModelProvider.NewInstanceFactory().create(AggregateViewModel.class);

        viewModel.loadAggregate();

        //viewModel?.liveAggregate?.observeForever { textView.text = it?.joinToString("\n") }

        viewModel.getLiveData().observe(this, datas -> {
            //AggregateUrlDao.AggregateURL aggregateURL = datas.get(0);
            //textView.setText(aggregateURL.toString());
            for (AggregateUrlDao.AggregateURL data : datas) {
                textView.append(data.toString()+"\n\n\n");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefetchingLib.setCurrentActivity(this);
    }
}
