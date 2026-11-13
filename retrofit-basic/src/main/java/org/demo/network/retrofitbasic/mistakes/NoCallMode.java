package org.demo.network.retrofitbasic.mistakes;

import org.demo.network.core.api.DogApi;

import java.util.List;

/** Mistake 6: the return type is List<String>, not Call<T>. Retrofit throws at the first call. */
public class NoCallMode extends MistakeMode {
    public NoCallMode() {
        super("No Call");
    }

    @Override
    public void run(boolean fix, Reporter out) {
        retrofit2.Retrofit retrofit = gsonRetrofit("https://dog.ceo/api/").build();
        if (fix) {
            retrofit.create(DogApi.class).getBreeds().enqueue(countBreeds(out));
        } else {
            List<String> names = retrofit.create(MistakeApis.NoCallApi.class).getBreedNames();
            out.show("never shown: " + names);
        }
    }
}
