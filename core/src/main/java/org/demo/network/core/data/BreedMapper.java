package org.demo.network.core.data;

import org.demo.network.core.domain.Breed;
import org.demo.network.core.dto.BreedListResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Server shape (DTO) -> app model. A change of the server format changes only this class. */
public class BreedMapper {
    public List<Breed> map(BreedListResponse dto) {
        List<Breed> result = new ArrayList<>();
        if (dto.breeds == null) return result;
        for (Map.Entry<String, List<String>> e : dto.breeds.entrySet()) {
            result.add(new Breed(e.getKey(), e.getValue()));
        }
        sortByName(result);
        return result;
    }

    private static void sortByName(List<Breed> breeds) {
        Collections.sort(breeds, (a, b) -> a.getName().compareTo(b.getName()));
    }
}
