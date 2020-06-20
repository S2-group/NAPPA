package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.http;

import nl.vu.cs.s2group.nappa.*;
import okhttp3.OkHttpClient;

/**
 * Provides a Singleton instance of {@link OkHttpClient}.
 * <p>
 * Read more about Singleton pattern in Android at
 * https://medium.com/@kevalpatel2106/how-to-make-the-perfect-singleton-de6b951dfdb0
 */
public class SingletonOkHttpClient {
    private static OkHttpClient okHttpClient;

    private SingletonOkHttpClient() {
        throw new IllegalStateException("SingletonOkHttpClient is a singleton class and should be instantiated via constructor");
    }

    /**
     * Instantiate a new {@link OkHttpClient} if invoked by the first time, otherwise, obtain a previously instantiated
     * object.
     *
     * @return A singleton  instance of {@link OkHttpClient}.
     */
    public static OkHttpClient getInstance() {
        if (okHttpClient == null) {
            okHttpClient = PrefetchingLib.getOkHttp(new OkHttpClient());
        }

        return okHttpClient;
    }

}
