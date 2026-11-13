package org.demo.network.core.api;

import org.demo.network.core.dto.BreedListResponse;
import org.demo.network.core.dto.ImagesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/** Dog CEO API. baseUrl: https://dog.ceo/api/ (the trailing slash is required). */
public interface DogApi {
    @GET("breeds/list/all")
    Call<BreedListResponse> getBreeds();

    @GET("breed/{breed}/images/random/{count}")
    Call<ImagesResponse> getRandomImages(@Path("breed") String breed,
                                         @Path("count") int count);

    @GET("breed/{breed}/images")
    Call<ImagesResponse> getImages(@Path("breed") String breed);
}
