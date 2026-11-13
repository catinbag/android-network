package org.demo.network.core.domain;

import java.util.List;

public class GetBreedsUseCase {
    private final BreedRepository repository;

    public GetBreedsUseCase(BreedRepository repository) {
        this.repository = repository;
    }

    /** Asynchronous: returns at once, the result comes to the callback. Not Call.execute(). */
    public Cancellable execute(ResultCallback<List<Breed>> callback) {
        return repository.getBreeds(callback);
    }
}
