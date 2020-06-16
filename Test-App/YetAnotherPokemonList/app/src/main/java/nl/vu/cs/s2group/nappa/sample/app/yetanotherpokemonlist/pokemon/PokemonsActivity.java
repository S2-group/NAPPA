package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.Config;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.http.SingletonOkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PokemonsActivity extends AppCompatActivity {
    private static final String LOG_TAG = PokemonsActivity.class.getSimpleName();
    private static final String API_URL = Config.API_URL + "pokemon/";
    String[] mobileArray = {"Android", "IPhone", "WindowsMobile", "Blackberry",
            "WebOS", "Ubuntu", "Windows7", "Max OS X"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemons);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        try (Response response = SingletonOkHttpClient.getInstance().newCall(request).execute()) {
            Log.d(LOG_TAG, response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.activity_listview, mobileArray);
        ListView listView = (ListView) findViewById(R.id.pokemon_list);
        listView.setAdapter(adapter);
    }
}