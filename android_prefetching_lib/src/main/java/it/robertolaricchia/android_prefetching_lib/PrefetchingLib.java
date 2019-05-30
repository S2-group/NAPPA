package it.robertolaricchia.android_prefetching_lib;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.LruCache;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import it.robertolaricchia.android_prefetching_lib.graph.ActivityGraph;
import it.robertolaricchia.android_prefetching_lib.graph.ActivityNode;
import it.robertolaricchia.android_prefetching_lib.prefetch.PrefetchStrategy;
import it.robertolaricchia.android_prefetching_lib.prefetch.PrefetchStrategyImpl;
import it.robertolaricchia.android_prefetching_lib.prefetch.PrefetchStrategyImpl2;
import it.robertolaricchia.android_prefetching_lib.prefetch.PrefetchStrategyImpl3;
import it.robertolaricchia.android_prefetching_lib.prefetchurl.ParameteredUrl;
import it.robertolaricchia.android_prefetching_lib.room.ActivityData;
import it.robertolaricchia.android_prefetching_lib.room.PrefetchingDatabase;
import it.robertolaricchia.android_prefetching_lib.room.RequestData;
import it.robertolaricchia.android_prefetching_lib.room.dao.SessionDao;
import it.robertolaricchia.android_prefetching_lib.room.data.ActivityExtraData;
import it.robertolaricchia.android_prefetching_lib.room.data.Session;
import it.robertolaricchia.android_prefetching_lib.room.data.SessionData;
import it.robertolaricchia.android_prefetching_lib.room.data.UrlCandidate;
import it.robertolaricchia.android_prefetching_lib.room.data.UrlCandidateParts;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.cache.CacheStrategy;

public class PrefetchingLib {

    private static PrefetchingLib instance;

    private static File cacheDir;
    private static String currentActivityName;
    private static ActivityGraph activityGraph;
    private static LiveData<List<ActivityData>> listLiveData;
    public static HashMap<String, Long> activityMap = new HashMap<>();      // Map of ActivityNodes containing Key: ActivityName Value: ID,
    private static Session session;
    private static PrefetchStrategy strategyHistory = new PrefetchStrategyImpl();
    private static PrefetchStrategy strategyIntent = new PrefetchStrategyImpl3(0.6f);
    private static OkHttpClient okHttpClient;
    private static ConcurrentHashMap<String, Long> prefetchRequest = new ConcurrentHashMap<>();
    private static ScheduledThreadPoolExecutor poolExecutor = new ScheduledThreadPoolExecutor(1);
    private static LruCache<String, SimpleResponse> responseLruCache = new LruCache<>(100);
    // Corresponds to a Map whose key is the Activity ID and the value is list of extras (key-value pairs)
    //  for the given activity.
    private static LongSparseArray<Map<String, String>> extrasMap = new LongSparseArray<>();
    private static DiffMatchPatch dmp = new DiffMatchPatch();

    private static boolean prefetchEnabled = true;
    private static int candidatePrefetchableUrlThreshold = 2;


    public static LongSparseArray<Map<String, String>> getExtrasMap() {
        return extrasMap;
    }

    private PrefetchingLib() {
    }


    public static void init(Context context) {
        if (instance == null) {
            instance = new PrefetchingLib();

            final Long start = new Date().getTime();
            PrefetchingDatabase.getInstance(context);
            cacheDir = context.getCacheDir();
            activityGraph = new ActivityGraph();

            poolExecutor.schedule(() -> {

                //INIT A NEW SESSION EACH TIME THE LIB IS INITIALIZED
                Session session = new Session(new Date().getTime());
                PrefetchingDatabase.getInstance().sessionDao().insertSession(session);
                PrefetchingLib.session = PrefetchingDatabase.getInstance().sessionDao().getSession(session.date);

                // This fetches the activities stored in the DB and their ID into the activitymap
                updateActivityMap(PrefetchingDatabase.getInstance().activityDao().getListActivity());

                // Iterate through the activity table which contains the activity name and its id
                for (String actName: activityMap.keySet()) {
                    Log.i("PrefetchingLib", "Init nodes");
                    activityGraph.initNodes(actName);

                    // Fetch ActivityNode Object, and its corresponding ID
                    ActivityNode byName = activityGraph.getByName(actName);
                    Long actId = activityMap.get(actName);

                    // Instantiate the static aggregated data for the count of Source --> Destination
                    // edge visits.  Set up the observers to ensure consistency with the database
                    if (byName.shouldSetSessionAggregateLiveData()) {
                        Log.i("PREF_LIB", "loading data for " + actName);
                        byName.setListSessionAggregateLiveData(
                            PrefetchingDatabase.getInstance().sessionDao().getCountForActivitySource(
                                    actId
                            )
                        );
                    }

                    // Instantiate all extras data for this activity AND set up all the observers to
                    // ensure consistency with the database
                    if (byName.shouldSetActivityExtraLiveData()) {
                        Log.i("PREF_LIB", "loading extras for " + actName);
                        byName.setListActivityExtraLiveData(
                            PrefetchingDatabase.getInstance().activityExtraDao().getActivityExtraLiveData(
                                    actId
                            )
                        );
                    }

                    // Instantiate the static UrlCandidate
                    if (byName.shouldSetUrlCandidateDbLiveDataLiveData()) {
                        Log.i("PREF_LIB", "loading urlcandidate for " + actName);
                        // Build
                        byName.setUrlCandidateDbLiveData(
                                // Fetch the URL candidates stored in the database
                                PrefetchingDatabase.getInstance().urlCandidateDao().getCandidatePartsListLiveDataForActivity(
                                        actId
                                )
                        );
                    }
                }


                listLiveData = PrefetchingDatabase.getInstance().activityDao().getListActivityLiveData();
                /*
                listLiveData.observeForever(new Observer<List<ActivityData>>() {
                    @Override
                    public void onChanged(@Nullable List<ActivityData> activityData) {
                        Log.i("PREFETCHINGLIB", "Added/Removed/Updated a new Activity");
                        synchronized (activityMap) {
                            updateActivityMap(activityData);
                        }
                    }
                });*/

                Log.w("PrefetchingLib", "Extended Startup-time: " + (new Date().getTime() - start) + " ms");

            }, 0, TimeUnit.SECONDS);

            Log.w("PrefetchingLib", "Startup-time: " + (new Date().getTime() - start) + " ms");
        }

    }

    public static LiveData<List<ActivityData>> getActivityLiveData() {
        return listLiveData;
    }

    /**
     * Instatntiate the Activity Map to conatin all the activities contained in the Database.
     * The Keys in the map are the activity name (String) and the value is the Activity ID (Long)
     * @param dataList - The list of all activities as stored in the database.
     */
    private static void updateActivityMap(List<ActivityData> dataList) {
        for (ActivityData activityData : dataList) {
            activityMap.remove(activityData.activityName);
            activityMap.put(activityData.activityName, activityData.id);
            Log.i("pref-lib::updActMap", activityData.activityName + ": " + activityData.id);
        }
    }

    /**
     * Inserts an activity (Its name and autogenerated ID) into the database AND also the static activity
     * map. If the node already exists in the hashmap (and therefore the database),
     * the function performs nothing.
     *
     * Insertion is performed in a threaded fashion to avoid locking up the main thread of
     * the instrumented application.
     * @param activityName
     */
    public static void registerActivity(String activityName) {
        if (!activityMap.containsKey(activityName)) {
            ActivityData activityData = new ActivityData(activityName);
            poolExecutor.schedule(() -> {
                    PrefetchingDatabase.getInstance().activityDao().insert(activityData);
                    updateActivityMap(PrefetchingDatabase.getInstance().activityDao().getListActivity());
            }, 0, TimeUnit.SECONDS);
        }
    }

    /**
     * This method instruments the OkHttpClient in order to use interceptors<br/>
     * <br/>
     * <b>
     *     NOTE: This method DOES NOT enforce the singleton pattern.  This pattern must be
     *           Enforced inside the application
     * </b>
     *
     * @param okHttpClient The identified okHttpClient as identified from the original code
     * @return An Instrumented OkHTTP client
     */
    public static OkHttpClient getOkHttp(OkHttpClient okHttpClient) {
        synchronized (okHttpClient) {
            PrefetchingLib.okHttpClient = okHttpClient
                    .newBuilder()
                    .addInterceptor(new CustomInterceptor())
                    .cache(new Cache(cacheDir, (10 * 10 * 1024)))
                    .build();

            Log.w("TAG", "okHttpClient initialized");
        }
        return PrefetchingLib.okHttpClient;
    }

    /**
     * Returns an Instrumented OkHttp Client.  This method will enforce a single instance of an OkHttp
     * Client.  If no instance has been created yet,  it will create one.  Otherwise,  it will return
     * the same instance in all successive calls. See {@link OkHttpClient} for reference. <br/>
     * <br/>
     * <b>
     *     NOTE: This method is to be used to instrument a Retrofit client to use an instrumented
     *          OkHttp Client whenever a client is not specified
     * </b>
     *
     * @return An Instrumented OkHTTP client.
     */
    public static OkHttpClient getOkHttp() {

            synchronized (PrefetchingLib.okHttpClient) {

                if (okHttpClient == null) {
                    PrefetchingLib.okHttpClient = okHttpClient.newBuilder()
                            .addInterceptor(new CustomInterceptor())
                            .cache(new Cache(cacheDir, (10 * 10 * 1024)))
                            .build();
                }

                return PrefetchingLib.okHttpClient;
            }

    }

    /**
     * Notifies the prefetching library whenever an activity transition takes place
     * @param activity
     */
    public static void setCurrentActivity(@NonNull Activity activity) {
        boolean shouldPrefetch;
        currentActivityName = activity.getClass().getCanonicalName();
        //SHOULD PREFETCH IFF THE USER IS MOVING FORWARD
        shouldPrefetch = activityGraph.updateNodes(currentActivityName);
        if (activityGraph.getCurrent().shouldSetSessionAggregateLiveData()) {
            Log.i("PREF_LIB", "loading data for " + currentActivityName);
            activityGraph.getCurrent().setListSessionAggregateLiveData(
                    PrefetchingDatabase.getInstance().sessionDao().getCountForActivitySource(
                            activityMap.get(currentActivityName)
                    )
            );
        }

        if (activityGraph.getCurrent().shouldSetActivityExtraLiveData()) {
            Log.i("PREF_LIB", "loading extras for " + currentActivityName);
            activityGraph.getCurrent().setListActivityExtraLiveData(
                    PrefetchingDatabase.getInstance().activityExtraDao().getActivityExtraLiveData(
                            activityMap.get(currentActivityName)
                    )
            );
        }

        //TODO prefetching spot here

        Log.w("SHOULD_PREFETCH", ""+shouldPrefetch);
        if (shouldPrefetch) {
            for (ActivityNode node : activityGraph.getByName(currentActivityName).successors.keySet()) {
                try {
                    Log.i("SUCCESSORS", node.activityName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            poolExecutor.schedule(() -> {
                //List<String> topNUrls = strategyHistory.getTopNUrlToPrefetchForNode(activityGraph.getCurrent(), 1);
                List<String> topNUrls = strategyIntent.getTopNUrlToPrefetchForNode(activityGraph.getCurrent(), 1);
                for (String url : topNUrls) {
                    Log.e("TO_BE_PREF", url);
                }
                if (prefetchEnabled) {
                    prefetchUrls(topNUrls);
                }
            }, 0, TimeUnit.SECONDS);
        }
    }

    /*private static List<ActivityNode> getAllParents(ActivityNode node, List<ActivityNode> parents) {
        for (ActivityNode parent : node.ancestors.keySet()) {
            parents.add(parent);
            getAllParents(parent, parents);
        }
        return parents;
    }*/

    public static ActivityGraph getActivityGraph() {
        return activityGraph;
    }

    /**
     * For the current session, ADD a source-destination Pair with a corresponding count of occurrences
     * to the Room Database
     *
     * @param actSource
     * @param actDest
     * @param count
     */
    public static void addSessionData(String actSource, String actDest, Long count) {
        poolExecutor.schedule(() -> {
                SessionData data = new SessionData(session.id, activityMap.get(actSource), activityMap.get(actDest), count);
                Log.i("PREFLIB", activityMap.toString());
                PrefetchingDatabase.getInstance().sessionDao().insertSessionData(data);
        }, 0, TimeUnit.SECONDS);
    }

    /**
     * For a given source-destination Pair,  Modify this entry in the Room database
     * @param actSource
     * @param actDest
     * @param count
     */
    public static void updateSessionData(String actSource, String actDest, Long count) {
        poolExecutor.schedule(() -> {
                SessionData data = new SessionData(session.id, activityMap.get(actSource), activityMap.get(actDest), count);
                PrefetchingDatabase.getInstance().sessionDao().updateSessionData(data);
        }, 0, TimeUnit.SECONDS);
    }

    public static LiveData<List<SessionData>> getSessionDataListLiveData() {
        return PrefetchingDatabase.getInstance().sessionDao().getSessionDataListLiveData();
    }

    /**
     *
     * @param key The key provided to the original putExtra method call.
     * @param value The value provided to the original putExtra method call.
     */
    public static void notifyExtra(String key, String value) {
        //PREFETCHING SPOT HERE FOR INTENT-BASED PREFETCHING
        final Long idAct = activityMap.get(currentActivityName);
        // Duplicate map containing key value pairs corresponding to android intent extras
        Map<String, String> extras = extrasMap.get(idAct, new HashMap<>());
        // Put on this extras tracker for this activity the new key-value pair
        extras.put(key, value);
        // Update the global extras map
        extrasMap.put(idAct, extras);
        poolExecutor.schedule(() -> {
            List<String> toBePrefetched = strategyIntent.getTopNUrlToPrefetchForNode(activityGraph.getCurrent(), 10);
            for (String url : toBePrefetched) {
                Log.e("PREFSTRAT2", "URL: " + url);
            }
            if (prefetchEnabled) {
                prefetchUrls(toBePrefetched);
            }
            /*for (String url : toBePrefetched) {
                Log.e("PREFSTRAT2", "URL: "+url);
                Request request = new Request.Builder().get().url(url).build();
                try {
                    okHttpClient.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }*/
        }, 0, TimeUnit.SECONDS);
        poolExecutor.schedule(() -> {
            ActivityExtraData activityExtraData =
                    new ActivityExtraData(session.id, idAct, key, value);
            Log.i("PREFSTRAT2", "ADDING NEW ACTEXTRADATA");
            PrefetchingDatabase.getInstance().activityExtraDao().insertActivityExtra(activityExtraData);
        }, 0, TimeUnit.SECONDS);
    }

    public static Long getActivityIdFromName(String activityName) {
        return activityMap.get(activityName);
    }

    private static void prefetchUrls(List<String> requests) {
        for (String request : requests) {
            try {
                Request request1 = new Request.Builder().url(request).build();
                okHttpClient.newCall(request1).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves a {@link ParameteredUrl} in the database, which is represented in the database
     * as a {@link UrlCandidate} with individual {@link UrlCandidateParts}
     * @param url the ParameteredUrl object to become a UrlCandidate
     */
    private static void serializeAndSaveParameteredUrl(ParameteredUrl url) {

        poolExecutor.schedule(() -> {
            Log.i("serializeAndSavePar", "start adding");
            UrlCandidate urlCandidate = new UrlCandidate(activityMap.get(currentActivityName), 1);
            // Save the URL candidate as a
            Long id = PrefetchingDatabase.getInstance().urlCandidateDao().insertUrlCandidate(urlCandidate);
            List<UrlCandidateParts> urlCandidateParts = ParameteredUrl.toUrlCandidateParts(url, id);

            PrefetchingDatabase.getInstance().urlCandidateDao().insertUrlCandidateParts(urlCandidateParts);
            Log.i("serializeAndSavePar", "end adding");
        }, 0, TimeUnit.SECONDS);
    }

    /**
     * Precondition: An URL request is performed on the currently active activity {@code node} with
     *               name currentActivityName
     * <br><br>
     * FOR ALL parents OF the current activity {@code node}, check the given {@code url} with ALL the
     * {@link ActivityExtraData}(an extra key-value pair) owned by  a given parent node.  If the requested {@code URL} contains
     * the value of a parent's extra,  then begin building a {@link UrlCandidate} with its corresponding
     * {@link UrlCandidateParts}.
     * <br>
     * After all UrlCandidates are created,
     *
     * @param url The url for which all URL candidates will be verified.
     */
    private static void checkUrlWithExtras(String url) {
        poolExecutor.schedule(() -> {
            ActivityNode node = activityGraph.getByName(currentActivityName);
            List<ActivityNode> parents = ActivityNode.getAllParents(node, new LinkedList<>());
            Log.i("PARENTS", "\nOf: " + node.activityName + " -> ");
            for (ActivityNode parent : parents) {
                Log.i("PARENTS", parent.activityName);
                // For a given parent of the current Activity, fetch all extras (key-value pairs) for this activity
                Map<String, String> extrasMap_ = extrasMap.get(activityMap.get(parent.activityName), new HashMap<>());
                // Iterate through all extras for a given parent
                if (extrasMap_.size() > 0) {
                    for (String key :extrasMap_.keySet()) {
                        String value = extrasMap_.get(key);
                        Log.i("PARENTS", "has extra: (" + key + ", " + value + ")");
                        // Verify if the Original URL contains  an extra from the extras map
                        if (url.contains(value)) {
                            Log.i("PARENTS", "value of key '" + key + "' is contained into " + url);
                            // Create a diff-map which checks what values from the URL are not part of the extras
                            // and those parts which are of INSERT type represent static URL values, while the parts
                            // with EQUAL type represent a PARAMETER
                            LinkedList<DiffMatchPatch.Diff> diffs = dmp.diffMain(value, url);
                            dmp.diffCleanupEfficiency(diffs);
                            ParameteredUrl parameteredUrl = new ParameteredUrl(diffs, true);
                            for (ParameteredUrl.UrlParameter parameter : parameteredUrl.getUrlParameterList()) {
                                // From the list of parameters, identify which corresponds to an extra's value
                                // and store the extra's key as the urlPiece
                                if (parameter.type == ParameteredUrl.TYPES.STATIC) {
                                    Log.i("PARENTS", parameter.urlPiece);
                                } else { // url Piece must represent the key not the value
                                    if (parameter.urlPiece.compareTo(value) == 0) {
                                        parameter.urlPiece = key;
                                    }
                                    Log.i("PARENTS", "PARAM: " + parameter.urlPiece);
                                }
                            }
                            // Checks if a given parameteredUrl is contained in the parameteredUrlList.
                            // Semantically, two extras sent from different parent nodes which also contain the
                            // same value will also
                            if (!node.parameteredUrlList.contains(parameteredUrl)) {
                                Log.i("PARENTS ", " NODE " + node.activityName + " DOES NOT CONTAIN THIS URL");

                                //TODO TO-BE-REMOVED
                                node.parameteredUrlMap.put(key, parameteredUrl);
                                node.parameteredUrlList.add(parameteredUrl);

                                serializeAndSaveParameteredUrl(parameteredUrl);

                            } else {
                                Log.i("PARENTS ", " NODE " + node.activityName + " ALREADY CONTAINS THIS URL");
                            }
                        }

                    }
                }
            }
        }, 0, TimeUnit.SECONDS);
    }

    /**
     * Represents an interceptor to be added to the OkHTTP chain of interceptors
     */
    private static class CustomInterceptor implements Interceptor {

        public Response intercept(Interceptor.Chain chain) {
            Request request = chain.request();
            boolean triggeredByPrefetch = false;
            boolean isGet = request.method().toLowerCase().compareTo("get") == 0;

            Log.w("NETWORK-PROVIDER", request.url().toString());

            // Focus on Get requests only and not posts to avoid side effects
            if (isGet) {
                // Perform candidate generation
                PrefetchingLib.checkUrlWithExtras(request.url().toString());
            }

            if ( request.header("X-PREF") != null ) {
                triggeredByPrefetch = true;
                Log.i("REQ_PREFETCHING", request.url().toString());
                Log.w("REQ_TIMINGS", prefetchRequest.get(request.url().toString()) + "\t"+new Date().getTime());
                // Ensure a that the request is both prefetched and Fresh (not stale beyond 30000 Milliseconds)
                if (prefetchRequest.contains(request.url().toString()) &&
                        (new Date().getTime() - prefetchRequest.get(request.url().toString())) < 30000L ) {
                    Log.i("REQ_PREFETCHING", "discarded");
                    return null;
                } else {
                    Log.i("REQ_PREFETCHING", "done");
                    prefetchRequest.put(request.url().toString(), new Date().getTime());
                    request = request.newBuilder().removeHeader("X-PREF").build();
                }
            }

            // Add a cache control mechanism setting staleness to 300 MS
            request = request.newBuilder()
                    .removeHeader("cache-control")
                    .removeHeader("Cache-control")
                    .removeHeader("Cache-Control")
                    .addHeader("Cache-Control", "max-age=300, max-stale=300")
                    //.cacheControl(CacheControl.FORCE_CACHE)
                    .build();


            Log.d("HEADER REQUEST", "--------------");
            Headers headers = request.headers();
            for (String name : headers.names()) {
                Log.d(name, headers.get(name));
            }

            SimpleResponse cachedResp = responseLruCache.get(request.url().toString());
            // If the request is both a Get request and is cached
            if (isGet && cachedResp != null) {
                Log.i("PREFLIB", "GET REQUEST " + request.url().toString());
                //SET TIMEOUT FOR STALE RESOURCES = 300 SECONDS
                if ((new Date().getTime() - cachedResp.receivedDate.getTime()) < 300*1000 ) {
                    Log.i("PREFLIB", "found " + request.url().toString() + ", sending it back");

                    Log.i("CONTENT", cachedResp.body);

                    // Return the Cached Response
                    return new Response.Builder().body(
                            ResponseBody.create(MediaType.parse(cachedResp.contentType), cachedResp.body.getBytes()))
                            .request(request)
                            .protocol(Protocol.HTTP_1_1)
                            .code(200)
                            .message("Ok")
                            .build();
                } else {
                    Log.i("PREFLIB", "found " + request.url().toString() + ", found but stale");
                }
            } else {
                Log.i("PREFLIB", "NOT A GET REQUEST OR NOT IN CACHE" + request.method());
            }

            try {
                // Execute the request
                Response response = chain.proceed(request);

                // Insert the new request
                RequestData req = new RequestData(
                        null,
                        //1L,
                        activityMap.get(currentActivityName),
                        request.url().url().toString(),
                        response.body().contentType().type(),
                        response.body().contentLength(),
                        Calendar.getInstance().getTimeInMillis());

                PrefetchingDatabase.getInstance().urlDao().insert(req);

                /*SimpleDateFormat format = new SimpleDateFormat(
                        "EEE, dd MMM yyyy HH:mm:ss"
                );
                format.setTimeZone(TimeZone.getTimeZone("Europe/London"));*/

                /*
                response = response.newBuilder()
                        .removeHeader("cache-control")
                        .removeHeader("Cache-control")
                        .removeHeader("Cache-Control")
                        .removeHeader("Pragma")
                        .removeHeader("Expires")
                        .removeHeader("X-Cache-Expires")
                        .addHeader("Cache-Control", "public, immutable, max-age=300, only-if-cached, max-stale=300")
                        //.addHeader("Last-Modified", format.format(GregorianCalendar.getInstance().getTime())+" GMT")
                        .build();
            */

                // Instrument the response to include new cache control aspects
                if (response.cacheControl().maxAgeSeconds() < 300) {
                    Log.w("CACHE_CONTROL", "SETTING NEW MAX-AGE");
                    response = response.newBuilder()
                            .removeHeader("cache-control")
                            .removeHeader("Cache-control")
                            .removeHeader("Cache-Control")
                            .removeHeader("Pragma")
                            .removeHeader("Expires")
                            .removeHeader("X-Cache-Expires")
                            .addHeader("Cache-Control", "public, immutable, max-age=300, only-if-cached, max-stale=300")
                            .addHeader("Expires", formatDate(5, TimeUnit.MINUTES))
                            .build();
                }


                Log.d("HEADER RESPONSE", "--------------");
                headers = response.headers();
                for (String name : headers.names()) {
                    Log.d(name, headers.get(name));
                }

                Log.w("CACHEABLE", ""+ CacheStrategy.isCacheable(response, request));

                Log.i("REQ", request.url().toString());
                if (response.networkResponse() != null) {
                    Log.i("REQ_SERV_BY", "net");
                } else if (response.cacheResponse() != null) {
                    Log.i("REQ_SERV_BY", "cache");
                }

                // If the response is successful and if the request is a get request, add the
                // response to the cache
                if (response.isSuccessful() && isGet) {
                    Log.i("PREFLIB", "Adding response to lrucache");
                    responseLruCache.put(request.url().toString(), new SimpleResponse(response.body().contentType().toString(), response.body().string()));
                    cachedResp = responseLruCache.get(request.url().toString());
                    return new Response.Builder().body(
                            ResponseBody.create(MediaType.parse(cachedResp.contentType), cachedResp.body.getBytes()))
                            .request(request)
                            .protocol(response.protocol())
                            .code(response.code())
                            .message(response.message())
                            .build();
                }

                return response;

            } catch (IOException exception) {
                exception.printStackTrace();
            }

            return null;
        }
    }

    private static String formatDate(long delta, TimeUnit timeUnit) {
        return formatDate(new Date(System.currentTimeMillis() + timeUnit.toMillis(delta)));
    }

    private static String formatDate(Date date) {
        DateFormat rfc1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        rfc1123.setTimeZone(TimeZone.getTimeZone("GMT"));
        return rfc1123.format(date);
    }

    static class SimpleResponse {

        String contentType;
        String body;
        Date receivedDate;


        public SimpleResponse(String contentType, String body) {
            this.contentType = contentType;
            this.body = body;
            receivedDate = new Date();
        }
    }
}
