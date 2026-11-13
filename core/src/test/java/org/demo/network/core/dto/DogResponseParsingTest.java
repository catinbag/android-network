package org.demo.network.core.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;

import org.demo.network.core.TestResources;
import org.junit.Test;

/** Parsing of saved Dog CEO answers. No network. */
public class DogResponseParsingTest {
    private final Gson gson = new Gson();

    @Test
    public void breedList_isParsedToMap() {
        String json = TestResources.read("breeds_list.json");

        BreedListResponse dto = gson.fromJson(json, BreedListResponse.class);

        assertEquals("success", dto.status);
        assertFalse(dto.breeds.isEmpty());
        assertTrue(dto.breeds.get("hound").contains("afghan"));
    }

    @Test
    public void images_areParsedToList() {
        String json = TestResources.read("images.json");

        ImagesResponse dto = gson.fromJson(json, ImagesResponse.class);

        assertEquals("success", dto.status);
        assertFalse(dto.imageUrls.isEmpty());
        assertTrue(dto.imageUrls.get(0).startsWith("https://images.dog.ceo/"));
    }

    @Test
    public void error404_isParsed() {
        String json = TestResources.read("error_404.json");

        ErrorDto dto = gson.fromJson(json, ErrorDto.class);

        assertEquals("error", dto.status);
        assertEquals(404, dto.code);
        assertTrue(dto.message.startsWith("Breed not found"));
    }
}
