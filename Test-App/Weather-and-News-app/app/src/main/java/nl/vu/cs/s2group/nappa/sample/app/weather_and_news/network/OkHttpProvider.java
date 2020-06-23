package nl.vu.cs.s2group.nappa.sample.app.weather_and_news.network;

import nl.vu.cs.s2group.nappa.PrefetchingLib;
import okhttp3.OkHttpClient;

public class OkHttpProvider {

    private static OkHttpProvider okHttpProvider = null;
    private OkHttpClient okHttpClient;

    private OkHttpProvider() {
        okHttpClient = new OkHttpClient();
        okHttpClient = PrefetchingLib.getOkHttp(okHttpClient);
    }

    public static OkHttpProvider getInstance() {
        if (OkHttpProvider.okHttpProvider == null) {
            okHttpProvider = new OkHttpProvider();
        }
        return okHttpProvider;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
