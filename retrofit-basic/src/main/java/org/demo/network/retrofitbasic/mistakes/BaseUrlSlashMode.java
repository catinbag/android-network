package org.demo.network.retrofitbasic.mistakes;

import org.demo.network.core.api.DogApi;

/** Mistake 4: baseUrl without a trailing slash. Retrofit throws before any request. */
public class BaseUrlSlashMode extends MistakeMode {
    public BaseUrlSlashMode() {
        super("baseUrl without /");
    }

    @Override
    public void run(boolean fix, Reporter out) {
        String baseUrl = fix ? "https://dog.ceo/api/" : "https://dog.ceo/api";   // the mistake: no "/"
        DogApi api = gsonRetrofit(baseUrl).build().create(DogApi.class);
        api.getBreeds().enqueue(countBreeds(out));
    }
}
