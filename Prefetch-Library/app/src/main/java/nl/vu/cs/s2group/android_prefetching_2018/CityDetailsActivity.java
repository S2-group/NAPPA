package nl.vu.cs.s2group.android_prefetching_2018;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import nl.vu.cs.s2group.PrefetchingLib;

public class CityDetailsActivity extends AppCompatActivity {

    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_details);

        if (getIntent() != null && getIntent().hasExtra("capital")) {
            city = getIntent().getStringExtra("capital");
        } else {
            finish();
        }

        Button weatherButton = findViewById(R.id.button_weather);
        weatherButton.setOnClickListener((view) -> {
            Intent intent = new Intent(this, WeatherActivity.class);
            intent.putExtra("capital", city);
            PrefetchingLib.notifyExtra("capital", city);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefetchingLib.setCurrentActivity(this);
    }
}
