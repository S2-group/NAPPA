package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.Config;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.http.ApiResponseWrapper;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.http.SingletonOkHttpClient;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.model.pokemon.Pokemon;
import okhttp3.Call;
import okhttp3.Callback;
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

        SingletonOkHttpClient.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleResponse(new Gson().fromJson(response.body().charStream(), PokemonsWrapper.class));
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.activity_listview, mobileArray);
        ListView listView = findViewById(R.id.pokemon_list);
        listView.setAdapter(adapter);
    }

    private void handleResponse(PokemonsWrapper response) {
        Log.d(LOG_TAG, response.toString());
        List<Pokemon> pokemons = (List<Pokemon>) response.getResults();

        runOnUiThread(() -> {
            PokemonsAdapter adapter = new PokemonsAdapter(this, R.layout.activity_pokemons, pokemons);
            ListView listView = (ListView) findViewById(R.id.pokemon_list);
            listView.setAdapter(adapter);
        });
    }
}