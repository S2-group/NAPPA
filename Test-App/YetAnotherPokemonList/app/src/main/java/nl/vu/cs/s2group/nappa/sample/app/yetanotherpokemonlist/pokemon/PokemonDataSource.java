package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.Config;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.http.SingletonOkHttpClient;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.model.pokemon.Pokemon;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PokemonDataSource extends PageKeyedDataSource<Integer, Pokemon> {
    private static final String LOG_TAG = PokemonDataSource.class.getSimpleName();
    private static final String API_URL = Config.API_URL + "pokemon/";

    private String previous;
    private String next;

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Pokemon> callback) {
        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        SingletonOkHttpClient.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(LOG_TAG, Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                ResponseBody body = Objects.requireNonNull(response.body());
                PokemonsWrapper pokemonsWrapper = new Gson().fromJson(body.charStream(), PokemonsWrapper.class);

                updatePPaginationLinks(pokemonsWrapper);
                callback.onResult(pokemonsWrapper.getResults(), null, 21);
            }
        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Pokemon> callback) {
        Request request = new Request.Builder()
                .url(previous)
                .build();

        SingletonOkHttpClient.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(LOG_TAG, Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                ResponseBody body = Objects.requireNonNull(response.body());
                PokemonsWrapper pokemonsWrapper = new Gson().fromJson(body.charStream(), PokemonsWrapper.class);
                Integer previousKey = (params.key == 0) ? params.key - 1 : null;

                updatePPaginationLinks(pokemonsWrapper);
                callback.onResult(pokemonsWrapper.getResults(), previousKey);
            }
        });
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Pokemon> callback) {
        Request request = new Request.Builder()
                .url(next)
                .build();

        SingletonOkHttpClient.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(LOG_TAG, Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                ResponseBody body = Objects.requireNonNull(response.body());
                PokemonsWrapper pokemonsWrapper = new Gson().fromJson(body.charStream(), PokemonsWrapper.class);
                Integer nextKey = (params.key == pokemonsWrapper.getCount()) ? null : params.key + 1;

                updatePPaginationLinks(pokemonsWrapper);
                callback.onResult(pokemonsWrapper.getResults(), nextKey);
            }
        });
    }

    private void updatePPaginationLinks(PokemonsWrapper pokemonsWrapper) {
        next = pokemonsWrapper.getNext();
        previous = pokemonsWrapper.getPrevious();
    }

}
