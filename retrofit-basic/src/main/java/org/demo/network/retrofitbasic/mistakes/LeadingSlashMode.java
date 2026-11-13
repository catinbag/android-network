package org.demo.network.retrofitbasic.mistakes;

import org.demo.network.core.api.DogApi;
import org.demo.network.core.dto.BreedListResponse;

import retrofit2.Call;
import retrofit2.Response;

/** Mistake 5: @GET("/breeds/list/all"). No exception: the request goes to another address. */
public class LeadingSlashMode extends MistakeMode {
    public LeadingSlashMode() {
        super("Leading slash");
    }

    @Override
    public void run(boolean fix, Reporter out) {
        retrofit2.Retrofit retrofit = gsonRetrofit("https://dog.ceo/api/").build();
        Call<BreedListResponse> call = fix
                ? retrofit.create(DogApi.class).getBreeds()
                : retrofit.create(MistakeApis.LeadingSlashApi.class).getBreeds();
        call.enqueue(new ShowCallback<BreedListResponse>(out) {
            @Override
            protected String handle(Response<BreedListResponse> r) {
                String url = "request: " + r.raw().request().url();   // the real address
                return r.isSuccessful() && r.body() != null
                        ? url + "\nbreeds: " + r.body().breeds.size()
                        : url + "\nnot successful, no exception";
            }
        });
    }
}
