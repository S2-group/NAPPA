package nl.vu.cs.s2group.nappa.sample.app.weather_and_news;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Button;

import nl.vu.cs.s2group.nappa.PrefetchingLib;
import nl.vu.cs.s2group.nappa.sample.app.weather_and_news.R;

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

    @Override
    protected void onPause() {
        super.onPause();
        PrefetchingLib.leavingCurrentActivity();
    }
}
