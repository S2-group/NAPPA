package nl.vu.cs.s2group.nappa.sample.app.weather_and_news;

import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import java.io.IOException;

import nl.vu.cs.s2group.nappa.sample.app.weather_and_news.cardview.NewsCardViewAdapter;
import nl.vu.cs.s2group.nappa.sample.app.weather_and_news.data.NewsWrapper;
import nl.vu.cs.s2group.nappa.sample.app.weather_and_news.network.OkHttpProvider;
import nl.vu.cs.s2group.nappa.PrefetchingLib;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsActivity extends AppCompatActivity {
    private final static String LOG_TAG = NewsActivity.class.getSimpleName();

    private Gson gson = new Gson();
    private Handler handler;
    private RecyclerView recyclerView;
    private NewsCardViewAdapter cardViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(new NAPPALifecycleObserver(this));

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
                Log.e(LOG_TAG, "ERROR " + e.getMessage());
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
