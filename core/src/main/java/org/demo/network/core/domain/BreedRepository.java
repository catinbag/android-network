package org.demo.network.core.domain;

import java.util.List;

/** Asynchronous repository: the answer comes later, so the methods take a callback. */
public interface BreedRepository {
    Cancellable getBreeds(ResultCallback<List<Breed>> callback);

    Cancellable getImages(String breed, ResultCallback<List<String>> callback);
}
