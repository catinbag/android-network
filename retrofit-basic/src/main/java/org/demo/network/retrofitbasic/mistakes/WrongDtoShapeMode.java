package org.demo.network.retrofitbasic.mistakes;

import org.demo.network.core.api.DogApi;

import retrofit2.Response;
import retrofit2.Retrofit;

/** Mistake 9: the server answers 200, but the DTO has another shape: onFailure is called. */
public class WrongDtoShapeMode extends MistakeMode {
    public WrongDtoShapeMode() {
        super("Wrong DTO shape");
    }

    @Override
    public void run(boolean fix, Reporter out) {
        Retrofit retrofit = gsonRetrofit("https://dog.ceo/api/").build();
        if (fix) {
            retrofit.create(DogApi.class).getBreeds().enqueue(countBreeds(out));
        } else {
            retrofit.create(MistakeApis.WrongShapeApi.class).getBreeds()
                    .enqueue(new ShowCallback<MistakeApis.WrongBreedListResponse>(out) {
                        @Override
                        protected String handle(Response<MistakeApis.WrongBreedListResponse> r) {
                            return "parsed: " + r.body().breeds;
                        }
                    });
        }
    }
}
