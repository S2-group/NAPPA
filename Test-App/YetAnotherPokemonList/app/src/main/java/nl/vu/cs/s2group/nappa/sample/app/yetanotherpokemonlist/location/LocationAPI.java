package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.location;

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

public class LocationAPI {
    private static final String LOG_TAG = LocationAPI.class.getSimpleName();
    private static final String URL = Config.API_URL + "location/";

    private LocationAPI() {
        throw new IllegalStateException("LocationAPI is an utility class and should be instantiated!");
    }

    public static void makeRequest(String url, Consumer<Location> callback) {
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
                Location location = new Gson().fromJson(body.charStream(), Location.class);
                Log.d(LOG_TAG, location.toString());
                callback.accept(location);
            }
        });
    }

    public static void makeRequest(int id, Consumer<Location> callback) {
        String url = URL + id;
        makeRequest(url, callback);
    }
}
