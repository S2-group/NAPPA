package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.Config;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.http.SingletonOkHttpClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PokemonsApi {
    private static final String LOG_TAG = PokemonsApi.class.getSimpleName();
    private static final String API_URL = Config.API_URL + "pokemon/";

    private PokemonsWrapper pokemonsWrapper;
    private int currentPage;

    public PokemonsApi() {
        currentPage = 1;
    }

    public void getInitialContent(Consumer<List<Pokemon>> callback) {
        sendRequest(API_URL, callback);
    }

    public void getNext(Consumer<List<Pokemon>> callback) {
        sendRequest(pokemonsWrapper.getNext(), callback);
    }

    public void getPrevious(Consumer<List<Pokemon>> callback) {
        sendRequest(pokemonsWrapper.getPrevious(), callback);
    }

    public boolean hasNext() {
        return pokemonsWrapper.getNext() != null;
    }

    public int getTotalItems() {
        return pokemonsWrapper.getCount();
    }

    public int getTotalPages() {
        return (int) Math.ceil(pokemonsWrapper.getCount() / (double) Config.ITEMS_PER_PAGE);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public boolean hasPrevious() {
        return pokemonsWrapper.getPrevious() != null;
    }

    private void sendRequest(String url, Consumer<List<Pokemon>> callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        SingletonOkHttpClient.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(LOG_TAG, Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                ResponseBody body = Objects.requireNonNull(response.body());
                pokemonsWrapper = new Gson().fromJson(body.charStream(), PokemonsWrapper.class);
                Log.d(LOG_TAG, pokemonsWrapper.toString());
                callback.accept(pokemonsWrapper.getResults());
            }
        });
    }
}
