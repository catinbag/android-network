package org.demo.network.core.data;

import org.demo.network.core.api.DogApi;
import org.demo.network.core.domain.Breed;
import org.demo.network.core.domain.BreedRepository;
import org.demo.network.core.domain.Cancellable;
import org.demo.network.core.domain.ResultCallback;
import org.demo.network.core.dto.BreedListResponse;
import org.demo.network.core.dto.ImagesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Retrofit stays in the data layer. Callers get domain types and HttpError. */
public class RetrofitBreedRepository implements BreedRepository {
    private final DogApi api;
    private final BreedMapper mapper;
    private final ErrorParser errorParser;

    public RetrofitBreedRepository(DogApi api, BreedMapper mapper, ErrorParser errorParser) {
        this.api = api;
        this.mapper = mapper;
        this.errorParser = errorParser;
    }

    @Override
    public Cancellable getBreeds(ResultCallback<List<Breed>> callback) {
        Call<BreedListResponse> call = api.getBreeds();
        call.enqueue(new Callback<BreedListResponse>() {
            @Override
            public void onResponse(Call<BreedListResponse> c, Response<BreedListResponse> r) {
                if (c.isCanceled()) return;   // on a JVM there is no main-thread executor that checks this
                if (r.isSuccessful() && r.body() != null) callback.onSuccess(mapper.map(r.body()));
                else callback.onError(errorParser.toError(r));      // own HttpError, not a Retrofit class
            }

            @Override
            public void onFailure(Call<BreedListResponse> c, Throwable t) {
                if (!c.isCanceled()) callback.onError(t);
            }
        });
        return call::cancel;
    }

    @Override
    public Cancellable getImages(String breed, ResultCallback<List<String>> callback) {
        Call<ImagesResponse> call = api.getImages(breed);
        call.enqueue(new Callback<ImagesResponse>() {
            @Override
            public void onResponse(Call<ImagesResponse> c, Response<ImagesResponse> r) {
                if (c.isCanceled()) return;
                if (r.isSuccessful() && r.body() != null) callback.onSuccess(urlsOf(r.body()));
                else callback.onError(errorParser.toError(r));
            }

            @Override
            public void onFailure(Call<ImagesResponse> c, Throwable t) {
                if (!c.isCanceled()) callback.onError(t);
            }
        });
        return call::cancel;
    }

    private static List<String> urlsOf(ImagesResponse dto) {
        return dto.imageUrls == null ? new ArrayList<String>() : dto.imageUrls;
    }
}
