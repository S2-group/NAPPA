package nl.vu.cs.s2group.nappa;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import nl.vu.cs.s2group.nappa.graph.ActivityGraph;
import nl.vu.cs.s2group.nappa.graph.ActivityNode;
import nl.vu.cs.s2group.nappa.prefetch.AbstractPrefetchingStrategy;
import nl.vu.cs.s2group.nappa.prefetch.PrefetchingStrategy;
import nl.vu.cs.s2group.nappa.prefetch.PrefetchingStrategyConfigKeys;
import nl.vu.cs.s2group.nappa.prefetch.PrefetchingStrategyType;
import nl.vu.cs.s2group.nappa.prefetchurl.ParameteredUrl;
import nl.vu.cs.s2group.nappa.room.ActivityData;
import nl.vu.cs.s2group.nappa.room.PrefetchingDatabase;
import nl.vu.cs.s2group.nappa.room.RequestData;
import nl.vu.cs.s2group.nappa.room.activity.visittime.ActivityVisitTime;
import nl.vu.cs.s2group.nappa.room.activity.visittime.AggregateVisitTimeByActivity;
import nl.vu.cs.s2group.nappa.room.dao.SessionDao;
import nl.vu.cs.s2group.nappa.room.dao.UrlCandidateDao;
import nl.vu.cs.s2group.nappa.room.data.ActivityExtraData;
import nl.vu.cs.s2group.nappa.room.data.Session;
import nl.vu.cs.s2group.nappa.room.data.SessionData;
import nl.vu.cs.s2group.nappa.room.data.UrlCandidate;
import nl.vu.cs.s2group.nappa.room.data.UrlCandidateParts;
import okhttp3.Cache;
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
    private static final String LOG_TAG = PrefetchingLib.class.getSimpleName();

    private static Map<PrefetchingStrategyConfigKeys, Object> config;

    private static PrefetchingLib instance;
    private static boolean libGet = false;
    private static File cacheDir;
    private static String currentActivityName;
    private static ActivityGraph activityGraph;
    private static LiveData<List<ActivityData>> listLiveData;
    /**
     * Map of ActivityNodes containing Key: ActivityName Value: ID,
     */
    public static HashMap<String, Long> activityMap = new HashMap<>();
    private static Session session;
    private static PrefetchingStrategy strategyIntent;
    private static OkHttpClient okHttpClient;
    private static ConcurrentHashMap<String, Long> prefetchRequest = new ConcurrentHashMap<>();
    private static ScheduledThreadPoolExecutor poolExecutor = new ScheduledThreadPoolExecutor(1);
    private static LruCache<String, SimpleResponse> responseLruCache = new LruCache<>(100);
    /**
     * Corresponds to a Map whose key is the Activity ID and the value is list of extras
     * (key-value pairs) for the given activity.
     */
    private static LongSparseArray<Map<String, String>> extrasMap = new LongSparseArray<>();
    private static DiffMatchPatch dmp = new DiffMatchPatch();
    public static PrefetchingStrategyType prefetchingStrategyType;
    private static boolean prefetchEnabled = true;
    private static int requestP = 0, requestNP = 0;
    private static float timeSaved = 0f;
    private static Date visitedCurrentActivityDate;

    public static LongSparseArray<Map<String, String>> getExtrasMap() {
        return extrasMap;
    }

    private PrefetchingLib() {
    }

    private static PrefetchingLib getInstance() {
        if (instance == null)
            instance = new PrefetchingLib();
        return instance;
    }

    @SuppressWarnings("unused")
    public static void init(Context context, PrefetchingStrategyType prefetchingStrategyType) {
        init(context, prefetchingStrategyType, new HashMap<>());
    }

    @SuppressWarnings("unused")
    public static void init(Context context, PrefetchingStrategyType prefetchingStrategyType, Map<PrefetchingStrategyConfigKeys, Object> config) {
        if (instance == null) {
            final long start = new Date().getTime();

            Log.d(LOG_TAG, "Selected prefetching strategy " + prefetchingStrategyType.name());

            instance = PrefetchingLib.getInstance();
            PrefetchingDatabase.getInstance(context);

            PrefetchingLib.config = config;
            PrefetchingLib.prefetchingStrategyType = prefetchingStrategyType;
            strategyIntent = PrefetchingStrategy.getStrategy(prefetchingStrategyType, config);
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
                for (String actName : activityMap.keySet()) {
                    Log.d(LOG_TAG, "Init nodes");
                    activityGraph.initNodes(actName);
                    // Fetch ActivityNode Object, and its corresponding ID
                    ActivityNode byName = activityGraph.getByName(actName);
                    Long actId = activityMap.get(actName);

                    addAUrlCandidateObserver(byName, actId);
                    addActivityExtraObserver(byName, actId);
                    addSessionAggregateObserver(byName, actId);
                    addVisitTimePerActivityObserver(byName);
                }


                listLiveData = PrefetchingDatabase.getInstance().activityDao().getListActivityLiveData();

                Log.d(LOG_TAG, "Extended Startup-time: " + (new Date().getTime() - start) + " ms");

            }, 0, TimeUnit.SECONDS);

            Log.d(LOG_TAG, "Startup-time: " + (new Date().getTime() - start) + " ms");
        }

    }

    /**
     * Query the database for the aggregate visit time and add an observer to the result
     *
     * @param activity A {@link ActivityNode} object contaning the activity name
     */
    private static void addVisitTimePerActivityObserver(@NotNull ActivityNode activity) {
        if (activity.isAggregateVisitTimeInstantiated()) return;
        if (prefetchingStrategyType != PrefetchingStrategyType.STRATEGY_GREEDY_VISIT_FREQUENCY_AND_TIME)
            return;

        Log.d(LOG_TAG, activity.activityName + " - Add aggregate visit time observer");

        poolExecutor.execute(() -> {
            LiveData<AggregateVisitTimeByActivity> liveData;

            Object lastNSessionsObj = config.get(PrefetchingStrategyConfigKeys.LAST_N_SESSIONS);
            int lastNSessions = lastNSessionsObj != null ?
                    Integer.parseInt(lastNSessionsObj.toString()) : AbstractPrefetchingStrategy.DEFAULT_LAST_N_SESSIONS;

            Object useSessionEntityObj = config.get(PrefetchingStrategyConfigKeys.USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS);
            boolean useSessionEntity = useSessionEntityObj != null ?
                    Boolean.getBoolean(useSessionEntityObj.toString()) : AbstractPrefetchingStrategy.DEFAULT_USE_ALL_SESSIONS_AS_SOURCE_FOR_LAST_N_SESSIONS;

            if (lastNSessions == -1) {
                liveData = PrefetchingDatabase.getInstance().activityVisitTimeDao().getAggregateVisitTimeByActivity(activity.activityName);
            } else if (useSessionEntity) {
                liveData = PrefetchingDatabase.getInstance().activityVisitTimeDao().getAggregateVisitTimeByActivityWithinLastNSessionsInEntitySession(activity.activityName, lastNSessions);
            } else {
                liveData = PrefetchingDatabase.getInstance().activityVisitTimeDao().getAggregateVisitTimeByActivityWithinLastNSessionsInThisEntity(activity.activityName, lastNSessions);
            }

            new Handler(Looper.getMainLooper()).post(() -> activity.setAggregateVisitTimeLiveData(liveData));
        });
    }

    /**
     * Instantiate the static UrlCandidate
     *
     * @param activity
     * @param activityId
     */
    private static void addAUrlCandidateObserver(@NotNull ActivityNode activity, Long activityId) {
        if (!activity.shouldSetUrlCandidateDbLiveDataLiveData()) return;

        Log.d(LOG_TAG, activity.activityName + " - Add URL candidates observer");

        poolExecutor.schedule(() -> {
            LiveData<List<UrlCandidateDao.UrlCandidateToUrlParameter>> liveData;
            liveData = PrefetchingDatabase.getInstance().urlCandidateDao().getCandidatePartsListLiveDataForActivity(activityId);
            new Handler(Looper.getMainLooper()).post(() -> activity.setUrlCandidateDbLiveData(liveData));
        }, 0, TimeUnit.SECONDS);
    }

    /**
     * Instantiate all extras data for this activity AND set up all the observers to
     * ensure consistency with the database
     *
     * @param activity
     * @param activityId
     */
    private static void addActivityExtraObserver(@NotNull ActivityNode activity, Long activityId) {
        if (!activity.shouldSetActivityExtraLiveData()) return;

        Log.d(LOG_TAG, activity.activityName + " - Add extras observer");

        poolExecutor.schedule(() -> {
            LiveData<List<ActivityExtraData>> liveData;
            liveData = PrefetchingDatabase.getInstance().activityExtraDao().getActivityExtraLiveData(activityId);
            new Handler(Looper.getMainLooper()).post(() -> activity.setListActivityExtraLiveData(liveData));
        }, 0, TimeUnit.SECONDS);
    }

    /**
     * Instantiate the static aggregated data for the count of Source --> Destination
     * edge visits.  Set up the observers to ensure consistency with the database
     *
     * @param activity
     * @param activityId
     */
    private static void addSessionAggregateObserver(@NotNull ActivityNode activity, Long activityId) {
        if (!activity.shouldSetSessionAggregateLiveData()) return;

        Log.d(LOG_TAG, activity.activityName + " - Add session aggregate  observer");

        poolExecutor.schedule(() -> {
            LiveData<List<SessionDao.SessionAggregate>> liveData;

            Object lastNSessionsObj = config.get(PrefetchingStrategyConfigKeys.LAST_N_SESSIONS);
            int lastNSessions = lastNSessionsObj != null ?
                    Integer.parseInt(lastNSessionsObj.toString()) : AbstractPrefetchingStrategy.DEFAULT_LAST_N_SESSIONS;

            if (prefetchingStrategyType == PrefetchingStrategyType.STRATEGY_PPM || lastNSessions != -1) {
                liveData = PrefetchingDatabase.getInstance().sessionDao().getCountForActivitySource(activityId, lastNSessions);
                new Handler(Looper.getMainLooper()).post(() -> activity.setLastNListSessionAggregateLiveData(liveData));
            } else {
                liveData = PrefetchingDatabase.getInstance().sessionDao().getCountForActivitySource(activityId);
                new Handler(Looper.getMainLooper()).post(() -> activity.setListSessionAggregateLiveData(liveData));
            }


        }, 0, TimeUnit.SECONDS);
    }

    public static LiveData<List<ActivityData>> getActivityLiveData() {
        return listLiveData;
    }

    /**
     * Instatntiate the Activity Map to conatin all the activities contained in the Database.
     * The Keys in the map are the activity name (String) and the value is the Activity ID (Long)
     *
     * @param dataList - The list of all activities as stored in the database.
     */
    private static void updateActivityMap(List<ActivityData> dataList) {
        for (ActivityData activityData : dataList) {
            activityMap.remove(activityData.activityName);
            activityMap.put(activityData.activityName, activityData.id);
            Log.d(LOG_TAG, "pref-lib::updActMap " + activityData.activityName + ": " + activityData.id);
        }
    }

    /**
     * Inserts an activity (Its name and autogenerated ID) into the database AND also the static activity
     * map. If the node already exists in the hashmap (and therefore the database),
     * the function performs nothing.
     * <p>
     * Insertion is performed in a threaded fashion to avoid locking up the main thread of
     * the instrumented application.
     *
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
     * NOTE: This method DOES NOT enforce the singleton pattern.  This pattern must be
     * Enforced inside the application
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

            Log.d(LOG_TAG, "TAG " + "okHttpClient initialized");
        }
        return PrefetchingLib.okHttpClient;
    }

    /**
     * Returns an Instrumented OkHttp Client.  This method will enforce a single instance of an OkHttp
     * Client.  If no instance has been created yet,  it will create one.  Otherwise,  it will return
     * the same instance in all successive calls. See {@link OkHttpClient} for reference. <br/>
     * <br/>
     * <b>
     * NOTE: This method is to be used to instrument a Retrofit client to use an instrumented
     * OkHttp Client whenever a client is not specified
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
     *
     * @param activity Represents the activity the user navigated to.
     */
    public static void setCurrentActivity(@NonNull Activity activity) {
        boolean shouldPrefetch;
        currentActivityName = activity.getClass().getCanonicalName();
        //SHOULD PREFETCH IFF THE USER IS MOVING FORWARD
        shouldPrefetch = activityGraph.updateNodes(currentActivityName);

        // Add LiveData observers
        ActivityNode currentNode = activityGraph.getCurrent();
        Long activityId = activityMap.get(currentActivityName);
        if (activityId == null)
            throw new IllegalArgumentException("Unknown activity " + currentActivityName);
        addSessionAggregateObserver(currentNode, activityId);
        addActivityExtraObserver(currentNode, activityId);
        addVisitTimePerActivityObserver(currentNode);
        addAUrlCandidateObserver(currentNode, activityId);

        //TODO prefetching spot here

        Log.d(LOG_TAG, "SHOULD_PREFETCH " + "" + shouldPrefetch);
        if (shouldPrefetch) {
            for (ActivityNode node : activityGraph.getByName(currentActivityName).successors.keySet()) {
                try {
                    Log.d(LOG_TAG, "SUCCESSORS " + node.activityName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            poolExecutor.schedule(() -> {
                List<String> topNUrls = strategyIntent.getTopNUrlToPrefetchForNode(activityGraph.getCurrent(), 2);
                for (String url : topNUrls) {
                    Log.d(LOG_TAG, "TO_BE_PREF " + url);
                }
                if (prefetchEnabled) {
                    prefetchUrls(topNUrls);
                }
            }, 0, TimeUnit.SECONDS);
        }
        Log.d(LOG_TAG, "STATS " + "Number of requests not prefetched: " + requestNP);
        Log.d(LOG_TAG, "STATS " + "Number of requests prefetched: " + requestP);
        Log.d(LOG_TAG, "STATS " + "Time saved until now: " + timeSaved);
        visitedCurrentActivityDate = new Date();
    }

    /**
     * Notifies the prefetching library whenever an activity is no longer in the foreground
     * (i.e., the user is leaving the activity). This method should be invoked on the method
     * `onPause`
     */
    public static void leavingCurrentActivity() {
        long duration = new Date().getTime() - visitedCurrentActivityDate.getTime();
        Log.d(LOG_TAG, "Stayed on " + currentActivityName + " for " + duration + " ms");
        ActivityVisitTime visitTime = new ActivityVisitTime(
                currentActivityName,
                session.id,
                visitedCurrentActivityDate,
                duration
        );
        poolExecutor.schedule(() -> PrefetchingDatabase.getInstance().activityVisitTimeDao().insert(visitTime), 0, TimeUnit.SECONDS);
    }

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
            Log.d(LOG_TAG, activityMap.toString());
            PrefetchingDatabase.getInstance().sessionDao().insertSessionData(data);
        }, 0, TimeUnit.SECONDS);
    }

    /**
     * For a given source-destination Pair,  Modify this entry in the Room database
     *
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
     * Method instrumenting the set of all extras stored in a given intent. This method instruments
     * all extras in a single batch call rather than one by one.
     * <p>
     * NOTE:  This method should take place befpre {@link PrefetchingLib::setCurrentActivity}
     *
     * @param allExtras - The set of all extras that have been stored in an intent X, up to the
     *                  point right before startActivity(X) is called
     */
    @SuppressWarnings("unused")
    public static void notifyExtras(Bundle allExtras) {
        // Note:  if the currentActivityName has not been set (Activity Transition before
        // setCurrentActivity() is called), then notification is ignored
        if (currentActivityName != null) {
            //PREFETCHING SPOT HERE FOR INTENT-BASED PREFETCHING
            final Long idAct = activityMap.get(currentActivityName);
            // Duplicate map containing key value pairs corresponding to android intent extras
            Map<String, String> extras = extrasMap.get(idAct, new HashMap<>());

            // Ensure that the set of extras is not empty
            if (allExtras != null) {
                for (String key : allExtras.keySet()) {
                    // Put on this extras tracker for this activity the new key-value pair. If
                    //    No value has been associated with this extra, NULL will be stored
                    extras.put(key, allExtras.getString(key));
                }

                // Update the global extras map after all extras have been stored
                extrasMap.put(idAct, extras);

                // Begin Generating URL Candidates
                poolExecutor.schedule(() -> {
                    List<String> toBePrefetched = strategyIntent.getTopNUrlToPrefetchForNode(activityGraph.getCurrent(), 2);
                    for (String url : toBePrefetched) {
                        Log.d(LOG_TAG, "PREFSTRAT2 " + "URL: " + url);
                    }
                    // Trigger Prefetching
                    if (prefetchEnabled) {
                        prefetchUrls(toBePrefetched);
                    }

                }, 0, TimeUnit.SECONDS);

                // Store the extras identified for a given activity in the database
                poolExecutor.schedule(() -> {
                    // Iterate through All Extras
                    for (String key : allExtras.keySet()) {
                        // Create an Database Object and store it
                        ActivityExtraData activityExtraData =
                                new ActivityExtraData(session.id, idAct, key, allExtras.getString(key));
                        Log.d(LOG_TAG, "PREFSTRAT2 " + "ADDING NEW ACTEXTRADATA");
                        PrefetchingDatabase.getInstance().activityExtraDao().insertActivityExtra(activityExtraData);
                    }
                }, 0, TimeUnit.SECONDS);

            }

        }

    }

    /**
     * @param key   The key provided to the original putExtra method call.
     * @param value The value provided to the original putExtra method call.
     */
    @SuppressWarnings("unused")
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
            List<String> toBePrefetched = strategyIntent.getTopNUrlToPrefetchForNode(activityGraph.getCurrent(), 2);
            for (String url : toBePrefetched) {
                Log.d(LOG_TAG, "PREFSTRAT2 " + "URL: " + url);
            }
            if (prefetchEnabled) {
                prefetchUrls(toBePrefetched);
            }
        }, 0, TimeUnit.SECONDS);
        poolExecutor.schedule(() -> {
            ActivityExtraData activityExtraData =
                    new ActivityExtraData(session.id, idAct, key, value);
            Log.d(LOG_TAG, "PREFSTRAT2 " + "ADDING NEW ACTEXTRADATA");
            PrefetchingDatabase.getInstance().activityExtraDao().insertActivityExtra(activityExtraData);
        }, 0, TimeUnit.SECONDS);
    }

    public static Long getActivityIdFromName(String activityName) {
        return activityMap.get(activityName);
    }

    private static void prefetchUrls(List<String> requests) {
        libGet = true;
        for (String request : requests) {
            try {
                Request request1 = new Request.Builder().url(request).build();
                okHttpClient.newCall(request1).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        libGet = false;
    }

    /**
     * Saves a {@link ParameteredUrl} in the database, which is represented in the database
     * as a {@link UrlCandidate} with individual {@link UrlCandidateParts}
     *
     * @param url the ParameteredUrl object to become a UrlCandidate
     */
    private static void serializeAndSaveParameteredUrl(ParameteredUrl url) {

        poolExecutor.schedule(() -> {
            Log.d(LOG_TAG, "serializeAndSavePar " + "start adding");
            UrlCandidate urlCandidate = new UrlCandidate(activityMap.get(currentActivityName), 1);
            // Save the URL candidate as a
            Long id = PrefetchingDatabase.getInstance().urlCandidateDao().insertUrlCandidate(urlCandidate);
            List<UrlCandidateParts> urlCandidateParts = ParameteredUrl.toUrlCandidateParts(url, id);

            PrefetchingDatabase.getInstance().urlCandidateDao().insertUrlCandidateParts(urlCandidateParts);
            Log.d(LOG_TAG, "serializeAndSavePar " + "end adding");
        }, 0, TimeUnit.SECONDS);
    }

    /**
     * Precondition: An URL request is performed on the currently active activity {@code node} with
     * name currentActivityName
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
            Log.d(LOG_TAG, "PARENTS " + "\nOf: " + node.activityName + " -> ");
            for (ActivityNode parent : parents) {
                Log.d(LOG_TAG, "PARENTS " + parent.activityName);
                // For a given parent of the current Activity, fetch all extras (key-value pairs) for this activity
                Map<String, String> extrasMap_ = extrasMap.get(activityMap.get(parent.activityName), new HashMap<>());
                // Iterate through all extras for a given parent
                if (extrasMap_.size() > 0) {
                    for (String key : extrasMap_.keySet()) {
                        String value = extrasMap_.get(key);
                        Log.d(LOG_TAG, "PARENTS " + "has extra: (" + key + ", " + value + ")");
                        // Verify if the Original URL contains  an extra from the extras map
                        if (url.contains(value)) {
                            Log.d(LOG_TAG, "PARENTS " + "value of key '" + key + "' is contained into " + url);
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
                                    Log.d(LOG_TAG, "PARENTS " + parameter.urlPiece);
                                } else { // url Piece must represent the key not the value
                                    if (parameter.urlPiece.compareTo(value) == 0) {
                                        parameter.urlPiece = key;
                                    }
                                    Log.d(LOG_TAG, "PARENTS " + "PARAM: " + parameter.urlPiece);
                                }
                            }
                            // Checks if a given parameteredUrl is contained in the parameteredUrlList.
                            // Semantically, two extras sent from different parent nodes which also contain the
                            // same value will also
                            if (!node.parameteredUrlList.contains(parameteredUrl)) {
                                Log.d(LOG_TAG, "PARENTS  " + " NODE " + node.activityName + " DOES NOT CONTAIN THIS URL");

                                //TODO TO-BE-REMOVED
                                node.parameteredUrlMap.put(key, parameteredUrl);
                                node.parameteredUrlList.add(parameteredUrl);

                                serializeAndSaveParameteredUrl(parameteredUrl);

                            } else {
                                Log.d(LOG_TAG, "PARENTS  " + " NODE " + node.activityName + " ALREADY CONTAINS THIS URL");
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

            Log.d(LOG_TAG, "NETWORK-PROVIDER " + request.url().toString());

            // Focus on Get requests only and not posts to avoid side effects
            if (isGet) {
                // Perform candidate generation
                PrefetchingLib.checkUrlWithExtras(request.url().toString());
            }

            if (request.header("X-PREF") != null) {
                triggeredByPrefetch = true;
                Log.d(LOG_TAG, "REQ_PREFETCHING " + request.url().toString());
                Log.d(LOG_TAG, "REQ_TIMINGS " + prefetchRequest.get(request.url().toString()) + "\t" + new Date().getTime());
                // Ensure a that the request is both prefetched and Fresh (not stale beyond 30000 Milliseconds)
                if (prefetchRequest.contains(request.url().toString()) &&
                        (new Date().getTime() - prefetchRequest.get(request.url().toString())) < 30000L) {
                    Log.d(LOG_TAG, "REQ_PREFETCHING " + "discarded");
                    return null;
                } else {
                    Log.d(LOG_TAG, "REQ_PREFETCHING " + "done");
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


            Log.d(LOG_TAG, "HEADER REQUEST");
            Headers headers = request.headers();
            for (String name : headers.names()) {
                Log.d(LOG_TAG, name + " " + headers.get(name));
            }

            SimpleResponse cachedResp = responseLruCache.get(request.url().toString());
            // If the request is both a Get request and is cached
            if (isGet && cachedResp != null) {
                Log.d(LOG_TAG, "PREFLIB " + "GET REQUEST " + request.url().toString());
                //SET TIMEOUT FOR STALE RESOURCES = 300 SECONDS
                if ((new Date().getTime() - cachedResp.receivedDate.getTime()) < 300 * 1000) {
                    Log.d(LOG_TAG, "PREFLIB " + "found " + request.url().toString() + ", sending it back");
                    if (!libGet) {
                        timeSaved += cachedResp.timeToHandle;
                        requestP++;
                    }
                    Log.d(LOG_TAG, "CONTENT " + cachedResp.body);

                    // Return the Cached Response
                    return new Response.Builder().body(
                            ResponseBody.create(MediaType.parse(cachedResp.contentType), cachedResp.body.getBytes()))
                            .request(request)
                            .protocol(Protocol.HTTP_1_1)
                            .code(200)
                            .message("Ok")
                            .build();
                } else {
                    Log.d(LOG_TAG, "PREFLIB " + "found " + request.url().toString() + ", found but stale");
                }
            } else {
                Log.d(LOG_TAG, "PREFLIB " + "NOT A GET REQUEST OR NOT IN CACHE" + request.method());
            }

            try {
                // Execute the request
                Response response = chain.proceed(request);
                if (!libGet) requestNP++;
                // Insert the new request


                RequestData req;
                // Verify if this response has an associated mime type with it.
                if (response.body() != null &&
                        response.body().contentType() != null &&
                        response.body().contentType().type() != null) {
                    req = new RequestData(

                            null,
                            //1L,
                            activityMap.get(currentActivityName),
                            request.url().url().toString(),
                            response.body().contentType().type(),
                            response.body().contentLength(),
                            Calendar.getInstance().getTimeInMillis());

                }
                // If the response does not contain a defined mime-type, provide an empty string, as per
                //      RFC-7231
                else {
                    req = new RequestData(
                            null,
                            //1L,
                            activityMap.get(currentActivityName),
                            request.url().url().toString(),
                            "",
                            response.body().contentLength(),
                            Calendar.getInstance().getTimeInMillis());
                }

                PrefetchingDatabase.getInstance().urlDao().insert(req);

                // Instrument the response to include new cache control aspects
                if (response.cacheControl().maxAgeSeconds() < 300) {
                    Log.d(LOG_TAG, "CACHE_CONTROL " + "SETTING NEW MAX-AGE");
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


                Log.d(LOG_TAG, "HEADER RESPONSE");
                headers = response.headers();
                for (String name : headers.names()) {
                    Log.d(LOG_TAG, name + " " + headers.get(name));
                }

                Log.d(LOG_TAG, "CACHEABLE " + "" + CacheStrategy.isCacheable(response, request));

                Log.d(LOG_TAG, "REQ " + request.url().toString());
                if (response.networkResponse() != null) {
                    Log.d(LOG_TAG, "REQ_SERV_BY " + "net");
                } else if (response.cacheResponse() != null) {
                    Log.d(LOG_TAG, "REQ_SERV_BY " + "cache");
                }

                // If the response is successful and if the request is a get request, add the
                // response to the cache
                if (response.isSuccessful() && isGet) {
                    Log.d(LOG_TAG, "PREFLIB " + "Adding response to lrucache");
                    Float timeToHandle = (response.receivedResponseAtMillis() - response.sentRequestAtMillis()) / 1000f;
                    responseLruCache.put(request.url().toString(), new SimpleResponse(response.body().contentType().toString(), response.body().string(), timeToHandle));
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
        Float timeToHandle;


        public SimpleResponse(String contentType, String body, Float timeToHandle) {
            this.contentType = contentType;
            this.body = body;
            this.timeToHandle = timeToHandle;
            receivedDate = new Date();
        }
    }
}