package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.http.SingletonOkHttpClient;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util.Config;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NamedAPI {
    private String logTag;
    private String baseApiUrl;
    private int currentPage;
    private String lastPageUrl;
    private NamedAPIResourceList wrapper;

    public NamedAPI(String baseApiUrl) {
        this(baseApiUrl, NamedAPI.class.getSimpleName());
    }

    public NamedAPI(String baseApiUrl, String logTag) {
        this.logTag = logTag;
        this.baseApiUrl = Config.API_URL + baseApiUrl;
        currentPage = 1;
    }

    public void getInitialContent(Consumer<List<NamedAPIResource>> callback) {
        sendRequest(baseApiUrl, callback);
    }

    public void getFirstPage(Consumer<List<NamedAPIResource>> callback) {
        sendRequest(baseApiUrl, callback);
        currentPage = 1;
    }

    public void getLastPage(Consumer<List<NamedAPIResource>> callback) {
        sendRequest(lastPageUrl, callback);
        currentPage = getTotalPages();
    }

    public void getNext(Consumer<List<NamedAPIResource>> callback) {
        sendRequest(wrapper.getNext(), callback);
        currentPage++;
    }

    public void getPrevious(Consumer<List<NamedAPIResource>> callback) {
        sendRequest(wrapper.getPrevious(), callback);
        currentPage--;
    }

    public boolean hasNext() {
        return wrapper.getNext() != null;
    }

    public int getTotalItems() {
        return wrapper.getCount();
    }

    public int getTotalPages() {
        return (int) Math.ceil(wrapper.getCount() / (double) Config.ITEMS_PER_PAGE);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public boolean hasPrevious() {
        return wrapper.getPrevious() != null;
    }

    private void makeLastPageUrl() {
        int offset = wrapper.getCount() - (wrapper.getCount() % 20);
        lastPageUrl = baseApiUrl + "?offset=" + offset + "&limit=20";
    }

    private void sendRequest(String url, Consumer<List<NamedAPIResource>> callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        SingletonOkHttpClient.getInstance().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(logTag, Log.getStackTraceString(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                ResponseBody body = Objects.requireNonNull(response.body());
                wrapper = new Gson().fromJson(body.charStream(), NamedAPIResourceList.class);
                Log.d(logTag, wrapper.toString());
                if (lastPageUrl == null) makeLastPageUrl();
                callback.accept(wrapper.getResults());
            }
        });
    }
}
