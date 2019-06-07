package it.robertolaricchia.android_prefetching_2018;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import it.robertolaricchia.android_prefetching_2018.stats.SessionDataActivity;
import it.robertolaricchia.android_prefetching_lib.PrefetchingLib;
import it.robertolaricchia.android_prefetching_lib.room.data.Session;

public class InterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button kabulButton = findViewById(R.id.button_kabul);

        kabulButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, WeatherActivity.class);
            intent.putExtra("capital","Kabul");
            PrefetchingLib.notifyExtra("capital","Kabul");
            startActivity(intent);
        });
        Button sessionButton = findViewById(R.id.button_session);
        sessionButton.setText("CapitalList");
        sessionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, CapitalListActivity.class);
            startActivity(intent);
        });

        Button graphButton = findViewById(R.id.button_news);

        graphButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewsActivity.class);
            startActivity(intent);
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        PrefetchingLib.setCurrentActivity(this);
    }

}
