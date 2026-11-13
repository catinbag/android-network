package org.demo.network.core.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.demo.network.core.TestResources;
import org.demo.network.core.dto.BreedListResponse;
import org.demo.network.core.dto.ImagesResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** The server is real, but it lives in the test. We check the request and the parsing. */
public class DogApiTest {
    private MockWebServer server;
    private DogApi api;

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        api = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DogApi.class);
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void getBreeds_sendsRightRequestAndParsesAnswer() throws Exception {
        server.enqueue(new MockResponse().setBody(TestResources.read("breeds_list.json")));

        Response<BreedListResponse> r = api.getBreeds().execute();   // execute: allowed in a test

        assertTrue(r.isSuccessful());
        assertTrue(r.body().breeds.get("hound").contains("afghan"));
        assertEquals("/breeds/list/all", server.takeRequest().getPath());
    }

    @Test
    public void getRandomImages_putsPathValuesIntoUrl() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"message\":[\"a.jpg\",\"b.jpg\"],\"status\":\"success\"}"));

        Response<ImagesResponse> r = api.getRandomImages("hound", 2).execute();

        assertEquals(2, r.body().imageUrls.size());
        assertEquals("/breed/hound/images/random/2", server.takeRequest().getPath());
    }

    @Test
    public void getImages_usesBreedPath() throws Exception {
        server.enqueue(new MockResponse().setBody(TestResources.read("images.json")));

        Response<ImagesResponse> r = api.getImages("hound").execute();

        assertFalse(r.body().imageUrls.isEmpty());
        assertEquals("/breed/hound/images", server.takeRequest().getPath());
    }

    @Test
    public void notFound_isNotSuccessful_bodyIsNull_errorBodyHasText() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404).setBody(TestResources.read("error_404.json")));

        Response<ImagesResponse> r = api.getImages("unknown").execute();

        assertFalse(r.isSuccessful());
        assertEquals(404, r.code());
        assertNull(r.body());
        assertTrue(r.errorBody().string().contains("Breed not found"));
    }
}
