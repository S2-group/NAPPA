package nl.vu.cs.s2group.nappa.sample.app.weather_and_news;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import nl.vu.cs.s2group.nappa.Nappa;
import nl.vu.cs.s2group.nappa.NappaLifecycleObserver;

public class CityDetailsActivity extends AppCompatActivity {

    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new NappaLifecycleObserver(this));
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
            Nappa.notifyExtra("capital", city);
            startActivity(intent);
        });
    }
}
