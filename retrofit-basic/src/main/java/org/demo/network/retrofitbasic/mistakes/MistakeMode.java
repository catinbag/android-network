package org.demo.network.retrofitbasic.mistakes;

import org.demo.network.core.dto.BreedListResponse;
import org.demo.network.retrofitbasic.NetworkModule;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * One mistake of the mistakes.md list. run(false) shows the mistake, run(true) shows the fix.
 * An exception thrown by run() is caught by the screen and shown. Exceptions inside
 * a Callback are caught by {@link ShowCallback}.
 */
public abstract class MistakeMode {
    /** Shows text on the screen and in Logcat. Always called in the main thread. */
    public interface Reporter {
        void show(String text);
    }

    private final String title;

    protected MistakeMode(String title) {
        this.title = title;
    }

    public abstract void run(boolean fix, Reporter out);

    /** The Spinner shows this text. */
    @Override
    public String toString() {
        return title;
    }

    public static String describe(Throwable t) {
        return t.getClass().getName() + "\n" + t.getMessage();
    }

    /** Retrofit.Builder with the app OkHttpClient, without a converter. */
    protected static Retrofit.Builder retrofit(String baseUrl) {
        return new Retrofit.Builder().baseUrl(baseUrl).client(NetworkModule.get().client());
    }

    protected static Retrofit.Builder gsonRetrofit(String baseUrl) {
        return retrofit(baseUrl).addConverterFactory(GsonConverterFactory.create());
    }

    /** The right way to read BreedListResponse: check isSuccessful(). */
    protected static ShowCallback<BreedListResponse> countBreeds(Reporter out) {
        return new ShowCallback<BreedListResponse>(out) {
            @Override
            protected String handle(Response<BreedListResponse> r) {
                return r.isSuccessful() && r.body() != null
                        ? "breeds: " + r.body().breeds.size()
                        : "not successful, no exception";
            }
        };
    }
}
