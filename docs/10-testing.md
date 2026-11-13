# Testing the network layer

## Idea

A test through the internet is slow and unstable, and the server can change its data. `MockWebServer` is a local HTTP server inside the test. A test checks which request left (path, method) and how the answer was parsed.

## Roles

| Role | What it does | Class (`core/src/test`) |
|---|---|---|
| Saved answers | Real answers of Dog CEO | `resources/breeds_list.json`, `images.json`, `error_404.json` |
| Resource reader | Reads a saved answer | `TestResources` |
| Parsing test | Gson and DTO, no network | `dto/DogResponseParsingTest` |
| API test | `DogApi` against `MockWebServer` | `api/DogApiTest` |
| Mapper test | DTO to `Breed` | `data/BreedMapperTest` |
| Repository test | Success, error code, bad JSON, cancel | `data/RetrofitBreedRepositoryTest` |

15 tests in four classes.

## How it works

```java
MockWebServer server = new MockWebServer();
server.start();
server.enqueue(new MockResponse().setBody(TestResources.read("breeds_list.json")));

DogApi api = new Retrofit.Builder()
        .baseUrl(server.url("/"))
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(DogApi.class);

Response<BreedListResponse> r = api.getBreeds().execute();   // execute is allowed in a test

assertTrue(r.isSuccessful());
assertEquals("/breeds/list/all", server.takeRequest().getPath());   // the right request left
```

What the tests check:
- `DogApiTest`: the path of each method, the parsing, and the 404 case (`isSuccessful()` is false, `body()` is null, `errorBody()` has the text).
- `RetrofitBreedRepositoryTest`:
  - success (sorted list);
  - 404 to `HttpError` with the text of the server;
  - an error body that is not JSON (only the code stays);
  - a wrong JSON shape (goes to `onError`, not `HttpError`);
  - a cancelled request (no callback).
- In a JVM test the callback runs in an OkHttp thread. The repository test waits with a `CountDownLatch`.
- The cancel test depends on timing: the server delays the answer by 500 ms, the test waits for the request to arrive, cancels, then waits 1500 ms for silence. The wait is longer than the delay, so a broken `cancel()` makes the test fail.

Dependencies: `testImplementation(libs.okhttp.mockwebserver)` with the same OkHttp version as Retrofit (3.14.9), package `okhttp3.mockwebserver`.

## Run

```
./gradlew :core:test
```

The tests work without the internet. The author ran this command and it was OK. The other modules have no tests (`app` has only the unit test of the wizard).
