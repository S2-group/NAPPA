package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokemon.ability;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.http.SingletonOkHttpClient;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.Config;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AbilityAPI {
    private static final String LOG_TAG = AbilityAPI.class.getSimpleName();
    private static final String URL = Config.API_URL + "ability/";

    private AbilityAPI() {
        throw new IllegalStateException("AbilityAPI is an utility class and should be instantiated!");
    }

    public static void makeRequest(String url, Consumer<Ability> callback) {
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
                Ability ability = new Gson().fromJson(body.charStream(), Ability.class);
                Log.d(LOG_TAG, ability.toString());
                callback.accept(ability);
            }
        });
    }

    public static void makeRequest(int id, Consumer<Ability> callback) {
        String url = URL + id;
        makeRequest(url, callback);
    }
}
