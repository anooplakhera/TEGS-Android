package com.tegs.retrofit;

import android.app.Activity;
import android.util.Log;

import com.tegs.BuildConfig;
import com.tegs.global.TegsApplication;
import com.tegs.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS;
import static okhttp3.logging.HttpLoggingInterceptor.Level.NONE;

/**
 * Created
 * by heena on 4/1/18.
 */

public class RestClient {

    private String TAG = "----" + getClass().getSimpleName() + "----";
    public int DEFAULT_TIMEOUT = 60; // 60 seconds
    private ApiInterface apiInterface;
    private static RestClient clientInstance;// INSTANCE OF WITHOUT HEADER
    private static RestClient clientInstanceWithHeader;// INSTANCE OF WITH HEADER
    private Utils utils;


    private static final String CACHE_CONTROL = "Cache-Control";

    public static RestClient getInstance() {
        if (clientInstance == null) {
            clientInstance = new RestClient();
        }
        return clientInstance;
    }

    //    public static RestClient getInstance(boolean IS_AUTH_HEADER_REQUIRED) {
    public static RestClient getInstance(boolean IS_AUTH_HEADER_REQUIRED) {
        if (clientInstanceWithHeader == null) {
            clientInstanceWithHeader = new RestClient();
        }
        return clientInstanceWithHeader;
    }

    private RestClient() {
        Retrofit retrofit1 = new Retrofit.Builder()
                .baseUrl(RequestParameters.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getClient())
                .build();

        apiInterface = retrofit1.create(ApiInterface.class);
    }

    public ApiInterface getApiInterface() {
        return apiInterface;
    }

    public OkHttpClient getClient() {
        //setup cache
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        httpClient.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        httpClient.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

//        httpClient.cache(provideCache());
//        httpClient.addInterceptor(httpLoggingInterceptor);
//        httpClient.addNetworkInterceptor(provideCacheInterceptor());
//        httpClient.addInterceptor(provideOfflineCacheInterceptor());

//        httpClient.addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);

        httpClient.addInterceptor(provideHttpLoggingInterceptor());
        httpClient.addInterceptor(provideOfflineCacheInterceptor());
        httpClient.addNetworkInterceptor(provideCacheInterceptor());
        httpClient.cache(provideCache1());


//        OkHttpClient okHttpClient = OkCacheControl.on(new OkHttpClient.Builder())
//                .overrideServerCachePolicy(1, MINUTES)
//                .forceCacheWhenOffline(networkMonitor)
//                .apply() // return to the OkHttpClient.Builder instance
//                //.addInterceptor(provideHttpLoggingInterceptor())
//                .cache(provideCache())
//                .build();

        utils = new Utils(TegsApplication.getAppInstance());

        return httpClient.build();
//        return okHttpClient;
    }

    public static void clearInstance() {
        clientInstance = null;
        clientInstanceWithHeader = null;
    }

    public static void makeApiRequest(final Activity activity, Object call, final boolean showProgressDialog, final ApiResponseListener apiResponseListener) {
       /* if (!Utils.isConnected(activity)) {
            Utils.dismissDialog();
            Utils.showSnackBar(activity, activity.getString(R.string.err_internet));
            return;
        }*/

        Call<Object> objectCall = (Call<Object>) call;
        objectCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                if (response.raw().cacheResponse() != null) {
                    Log.d("Network", "response came from cache");
                }
                if (response.raw().networkResponse() != null) {
                    Log.d("Network", "response came from server");
                }
                apiResponseListener.onApiResponse(call, response.body());
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                //There is more than just a failing request (like: no internet connection)
                apiResponseListener.onApiError(call, t);
            }
        });
    }

    //Interceptor enable by anoop


    public Interceptor provideCacheInterceptor1() {
        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Response response = chain.proceed(chain.request());
                // re-write response header to force use of cache
                CacheControl cacheControl;

                if (Utils.isConnected(TegsApplication.getAppInstance())) {
                    cacheControl = new CacheControl.Builder()
                            .maxAge(0, TimeUnit.SECONDS)
                            .build();
                } else {
                    cacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS)
                            .build();
                }
                return response.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", cacheControl.toString())
                        .build();
            }
        };
    }

    public Interceptor provideOfflineCacheInterceptor1() {
        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!Utils.isConnected(TegsApplication.getAppInstance())) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS)
                            .build();

                    request = request.newBuilder()
                            .removeHeader("Pragma")
                            .removeHeader("Cache-Control")
                            .cacheControl(cacheControl)
                            .build();
                }

                return chain.proceed(request);
            }
        };
    }

    private Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(TegsApplication.getAppInstance().getCacheDir(), "http-cache"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
        return cache;
    }

    static OkCacheControl.NetworkMonitor networkMonitor = new
            OkCacheControl.NetworkMonitor() {
                @Override
                public boolean isOnline() {
                    return Utils.isConnected(TegsApplication.getAppInstance());
                }
            };


    //cache
    Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {

            CacheControl.Builder cacheBuilder = new CacheControl.Builder();
            cacheBuilder.maxAge(0, TimeUnit.SECONDS);
            cacheBuilder.maxStale(365, TimeUnit.DAYS);
            CacheControl cacheControl = cacheBuilder.build();

            Request request = chain.request();
            if (Utils.isConnected(TegsApplication.getAppInstance())) {
                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            if (Utils.isConnected(TegsApplication.getAppInstance())) {
                int maxAge = 60 * 60; // read from cache
                return originalResponse.newBuilder()
                        .header("Cache-Control", String.format("max-age=%d", 50000))
//                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };

    ///newwwwwww

    private static Cache provideCache1() {
        Cache cache = null;
        try {
            cache = new Cache(new File(TegsApplication.getInstance().getCacheDir(), "http-cache"),
                    10 * 1024 * 1024); // 10 MB
        } catch (Exception e) {
            Log.e("" + e, "Could not create Cache!");
        }
        return cache;
    }

    private static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Log.d("", message);
                    }
                });
        httpLoggingInterceptor.setLevel(BuildConfig.DEBUG ? HEADERS : NONE);
        return httpLoggingInterceptor;
    }

    public static Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());

                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(2, TimeUnit.MINUTES)
                        .build();

                return response.newBuilder()
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .build();
            }
        };
    }

    public static Interceptor provideOfflineCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                if (!Utils.isConnected(TegsApplication.getAppInstance())) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(7, TimeUnit.DAYS)
                            .build();

                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .build();
                }

                return chain.proceed(request);
            }
        };
    }

}