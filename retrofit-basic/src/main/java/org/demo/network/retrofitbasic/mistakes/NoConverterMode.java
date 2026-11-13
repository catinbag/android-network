package org.demo.network.retrofitbasic.mistakes;

import org.demo.network.core.api.DogApi;

import retrofit2.Retrofit;

/** Mistake 7: no addConverterFactory. Retrofit throws at the first call. */
public class NoConverterMode extends MistakeMode {
    public NoConverterMode() {
        super("No converter");
    }

    @Override
    public void run(boolean fix, Reporter out) {
        Retrofit.Builder builder = retrofit("https://dog.ceo/api/");
        if (fix) builder.addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create());
        DogApi api = builder.build().create(DogApi.class);
        api.getBreeds().enqueue(countBreeds(out));
    }
}
