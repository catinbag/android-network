package org.demo.network.retrofitbasic.mistakes;

import org.demo.network.core.api.DogApi;
import org.demo.network.core.dto.ImagesResponse;

import java.io.IOException;

import retrofit2.Response;

/** Mistake 11: body() without isSuccessful(). 404 is onResponse, and body() is null. */
public class NoIsSuccessfulMode extends MistakeMode {
    public NoIsSuccessfulMode() {
        super("No isSuccessful");
    }

    @Override
    public void run(boolean fix, Reporter out) {
        DogApi api = gsonRetrofit("https://dog.ceo/api/").build().create(DogApi.class);
        api.getImages("unknown").enqueue(new ShowCallback<ImagesResponse>(out) {
            @Override
            protected String handle(Response<ImagesResponse> r) {
                if (!fix) {
                    return "images: " + r.body().imageUrls.size();       // the mistake: body() is null
                }
                if (r.isSuccessful() && r.body() != null) {
                    return "images: " + r.body().imageUrls.size();
                }
                return "errorBody: " + errorText(r);                     // the fix: code() and errorBody()
            }
        });
    }

    private static String errorText(Response<?> r) {
        try {
            return r.errorBody() == null ? "(none)" : r.errorBody().string();
        } catch (IOException e) {
            return describe(e);
        }
    }
}
