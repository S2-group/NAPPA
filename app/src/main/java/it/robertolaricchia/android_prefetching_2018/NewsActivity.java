package it.robertolaricchia.android_prefetching_2018;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.robertolaricchia.android_prefetching_2018.cardview.NewsCardViewAdapter;
import it.robertolaricchia.android_prefetching_2018.data.News;
import it.robertolaricchia.android_prefetching_2018.data.NewsWrapper;
import it.robertolaricchia.android_prefetching_2018.network.OkHttpProvider;
import it.robertolaricchia.android_prefetching_lib.*;
import it.robertolaricchia.android_prefetching_lib.PrefetchingLib;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsActivity extends AppCompatActivity {

    private Gson gson = new Gson();
    private Handler handler;
    private RecyclerView recyclerView;
    private NewsCardViewAdapter cardViewAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        PrefetchingLib.setCurrentActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        handler = new Handler(getMainLooper());
        recyclerView = findViewById(R.id.recycler_news);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        cardViewAdapter = new NewsCardViewAdapter();

        recyclerView.setAdapter(cardViewAdapter);
        getNews();
    }

    private void getNews() {
        OkHttpClient client = OkHttpProvider.getInstance().getOkHttpClient();

        Request newsRequest = new Request.Builder()
                .url("https://newsapi.org/v2/top-headlines?country=it&apiKey=d7b2de397a3946b795bef942099c5860")
                .build();

        client.newCall(newsRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("ERROR", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                NewsWrapper newsWrapper = gson.fromJson(response.body().charStream(), NewsWrapper.class);
                cardViewAdapter.newsList = newsWrapper.articles;
                Runnable r = () -> cardViewAdapter.notifyDataSetChanged();
                handler.post(r);
            }
        });
    }
}
