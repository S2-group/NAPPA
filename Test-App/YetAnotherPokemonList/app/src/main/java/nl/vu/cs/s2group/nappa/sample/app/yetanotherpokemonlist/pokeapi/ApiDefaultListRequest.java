package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.pokeapi;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.http.SingletonOkHttpClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ApiDefaultListRequest extends ApiRequest {
    private static final String LOG_TAG = ApiDefaultListRequest.class.getSimpleName();

    protected void sendRequest(String url, Consumer<List<? extends ApiModel>> callback) {
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
                wrapper = new Gson().fromJson(body.charStream(), ApiDefaultResponseWrapper.class);
                Log.d(LOG_TAG, wrapper.toString());
                if (lastPageUrl == null) makeLastPageUrl();
                callback.accept(((ApiDefaultResponseWrapper) wrapper).getResults());
            }
        });
    }
}
