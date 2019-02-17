package it.robertolaricchia.android_prefetching_2018;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;

import it.robertolaricchia.android_prefetching_2018.data.Weather;
import it.robertolaricchia.android_prefetching_2018.network.OkHttpProvider;
import it.robertolaricchia.android_prefetching_lib.*;
import it.robertolaricchia.android_prefetching_lib.PrefetchingLib;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private String city;
    private Gson gson;
    private Handler handler;
    private TextView temp;

    @Override
    protected void onResume() {
        super.onResume();
        PrefetchingLib.setCurrentActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weather);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((view) -> {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
        });

        temp = findViewById(R.id.text_temp);

        if (getIntent()!=null && getIntent().hasExtra("capital")) {
            temp.setText(getIntent().getStringExtra("capital"));
            city = getIntent().getStringExtra("capital");
        } else {
            finish();
        }

        gson = new Gson();
        handler = new Handler(getMainLooper());

        getTemp();
    }

    private void getTemp() {
        OkHttpClient client = OkHttpProvider.getInstance().getOkHttpClient();
        Request request = new Request.Builder().url("http://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=75f4ddb403cdbac1df21fa8a10c21ce9").build();
        Long start = new Date().getTime();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Long end = new Date().getTime();
                Log.d("PERF_REQUEST", (end-start)+" ms");
                Weather weather = gson.fromJson(response.body().charStream(), Weather.class);
                handler.post(() -> {
                    try {
                        temp.append("\n\n" + weather.main.getTemp().toString() + " °C");
                    } catch (Exception e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(WeatherActivity.this)
                                .setTitle("Error")
                                .setMessage("City not found")
                                .setPositiveButton("Ok", (i,m) -> {finish();})
                                .show();

                    }
                });
            }
        });
    }
}
