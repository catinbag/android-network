package org.demo.network.core.domain;

import java.util.List;

public class GetBreedImagesUseCase {
    private final BreedRepository repository;

    public GetBreedImagesUseCase(BreedRepository repository) {
        this.repository = repository;
    }

    public Cancellable execute(String breed, ResultCallback<List<String>> callback) {
        return repository.getImages(breed, callback);
    }
}
