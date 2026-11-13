package org.demo.network.core.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;

import org.demo.network.core.TestResources;
import org.demo.network.core.api.DogApi;
import org.demo.network.core.domain.Breed;
import org.demo.network.core.domain.Cancellable;
import org.demo.network.core.domain.HttpError;
import org.demo.network.core.domain.ResultCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBreedRepositoryTest {
    private MockWebServer server;
    private RetrofitBreedRepository repository;

    @Before
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        DogApi api = new Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DogApi.class);
        repository = new RetrofitBreedRepository(api, new BreedMapper(), new ErrorParser(new Gson()));
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void getBreeds_success_returnsSortedBreeds() throws Exception {
        server.enqueue(new MockResponse().setBody(TestResources.read("breeds_list.json")));
        Capture<List<Breed>> capture = new Capture<>();

        repository.getBreeds(capture);

        assertTrue(capture.await());
        assertNull(capture.error);
        assertFalse(capture.data.isEmpty());
        assertEquals("affenpinscher", capture.data.get(0).getName());
    }

    @Test
    public void getImages_notFound_returnsHttpErrorWithServerText() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(404).setBody(TestResources.read("error_404.json")));
        Capture<List<String>> capture = new Capture<>();

        repository.getImages("unknown", capture);

        assertTrue(capture.await());
        assertNull(capture.data);
        assertTrue(capture.error instanceof HttpError);
        HttpError error = (HttpError) capture.error;
        assertEquals(404, error.getCode());
        assertTrue(error.getServerMessage().startsWith("Breed not found"));
    }

    @Test
    public void getBreeds_errorBodyNotJson_keepsOnlyCode() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(500).setBody("<html>oops</html>"));
        Capture<List<Breed>> capture = new Capture<>();

        repository.getBreeds(capture);

        assertTrue(capture.await());
        HttpError error = (HttpError) capture.error;
        assertEquals(500, error.getCode());
        assertNull(error.getServerMessage());
    }

    @Test
    public void getBreeds_brokenJson_goesToOnError() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"message\":[1,2],\"status\":\"success\"}"));
        Capture<List<Breed>> capture = new Capture<>();

        repository.getBreeds(capture);

        assertTrue(capture.await());
        assertNotNull(capture.error);
        assertFalse(capture.error instanceof HttpError);   // 200 answer, but the shape is wrong
    }

    @Test
    public void cancel_callsNoCallback() throws Exception {
        server.enqueue(new MockResponse()
                .setBody(TestResources.read("breeds_list.json"))
                .setHeadersDelay(500, TimeUnit.MILLISECONDS));
        Capture<List<Breed>> capture = new Capture<>();

        Cancellable request = repository.getBreeds(capture);
        assertNotNull("The request must reach the server before the cancel",
                server.takeRequest(2, TimeUnit.SECONDS));
        request.cancel();

        // The wait is longer than the server delay: an answer that was not cancelled would arrive.
        assertFalse("A cancelled request must stay silent", capture.await(1500));
    }

    /** Collects the callback result. The callback runs in an OkHttp thread, so the test waits. */
    private static class Capture<T> implements ResultCallback<T> {
        private final CountDownLatch latch = new CountDownLatch(1);
        volatile T data;
        volatile Throwable error;

        @Override
        public void onSuccess(T data) {
            this.data = data;
            latch.countDown();
        }

        @Override
        public void onError(Throwable error) {
            this.error = error;
            latch.countDown();
        }

        boolean await() throws InterruptedException {
            return await(5000);
        }

        boolean await(long millis) throws InterruptedException {
            return latch.await(millis, TimeUnit.MILLISECONDS);
        }
    }
}
