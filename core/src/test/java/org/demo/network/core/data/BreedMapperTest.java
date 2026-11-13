package org.demo.network.core.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.demo.network.core.domain.Breed;
import org.demo.network.core.dto.BreedListResponse;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BreedMapperTest {
    private final BreedMapper mapper = new BreedMapper();

    @Test
    public void map_sortsByName_andKeepsSubBreeds() {
        BreedListResponse dto = new BreedListResponse();
        dto.breeds = new LinkedHashMap<>();   // server order: not sorted
        dto.breeds.put("hound", Arrays.asList("afghan", "basset"));
        dto.breeds.put("african", Collections.singletonList("wild"));
        dto.breeds.put("akita", Collections.<String>emptyList());

        List<Breed> result = mapper.map(dto);

        assertEquals(3, result.size());
        assertEquals("african", result.get(0).getName());
        assertEquals("akita", result.get(1).getName());
        assertEquals(new Breed("hound", Arrays.asList("afghan", "basset")), result.get(2));
    }

    @Test
    public void map_nullMap_givesEmptyList() {
        BreedListResponse dto = new BreedListResponse();
        dto.breeds = null;

        assertTrue(mapper.map(dto).isEmpty());
    }

    @Test
    public void breed_subBreedsAreCopiedAndReadOnly() {
        Map<String, List<String>> source = new LinkedHashMap<>();
        List<String> subs = new java.util.ArrayList<>(Collections.singletonList("wild"));
        source.put("african", subs);
        BreedListResponse dto = new BreedListResponse();
        dto.breeds = source;

        Breed breed = mapper.map(dto).get(0);
        subs.add("changed later");

        assertEquals(Collections.singletonList("wild"), breed.getSubBreeds());
    }
}
