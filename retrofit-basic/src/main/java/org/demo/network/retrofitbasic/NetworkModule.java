package org.demo.network.retrofitbasic;

import org.demo.network.core.api.DogApi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * One OkHttpClient and one Retrofit per server for the whole app.
 * A new client per request would create a new connection pool every time.
 */
public final class NetworkModule {
    private static NetworkModule instance;

    private final OkHttpClient client;
    private final DogApi dogApi;
    private final PostApi postApi;

    public static synchronized NetworkModule get() {
        if (instance == null) instance = new NetworkModule();
        return instance;
    }

    private NetworkModule() {
        client = createClient();

        Retrofit dogRetrofit = new Retrofit.Builder()
                .baseUrl("https://dog.ceo/api/")                    // the trailing slash is required
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        dogApi = dogRetrofit.create(DogApi.class);                  // Retrofit writes the implementation

        Retrofit postsRetrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        postApi = postsRetrofit.create(PostApi.class);
    }

    private static OkHttpClient createClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BODY      // only in debug: BODY shows tokens and personal data
                : HttpLoggingInterceptor.Level.NONE);

        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(chain -> chain.proceed(
                        chain.request().newBuilder()
                                .header("Accept", "application/json")
                                .build()))
                .addInterceptor(logging)
                .build();
    }

    public OkHttpClient client() {
        return client;
    }

    public DogApi dogApi() {
        return dogApi;
    }

    public PostApi postApi() {
        return postApi;
    }
}
