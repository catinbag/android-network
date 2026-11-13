# Retrofit

## Idea

Retrofit turns a Java interface into an HTTP client. A method is a request, the return type is the answer. The library writes the request code. Retrofit builds the client and creates the implementation of the interface. OkHttp sends the request.

## Roles

| Role | What it does | In this project |
|---|---|---|
| Interface | Describes the requests | `DogApi` (`core`), `PostApi` (`retrofit-basic`) |
| `Retrofit.Builder` | Sets `baseUrl`, client, converter | `NetworkModule` |
| Converter | Body to object and back | `GsonConverterFactory` |
| `Call<T>` | One request, not sent yet | Return type of every method |
| `Callback<T>` | Receives the result | `BreedsActivity`, `RetrofitBreedRepository` |

## How it works

Dependencies (one version for both, see `versions.md`):

```kotlin
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
```

The interface:

```java
public interface DogApi {
    @GET("breeds/list/all")
    Call<BreedListResponse> getBreeds();

    @GET("breed/{breed}/images/random/{count}")
    Call<ImagesResponse> getRandomImages(@Path("breed") String breed, @Path("count") int count);
}
```

The return type must be `Call<T>`. Without a call adapter Retrofit in Java supports only `Call<T>`. Another type gives `IllegalArgumentException: Unable to create call adapter for …`.

The builder:

```java
Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://dog.ceo/api/")                    // the trailing slash is required
        .addConverterFactory(GsonConverterFactory.create())
        .build();
DogApi api = retrofit.create(DogApi.class);                 // Retrofit writes the implementation
```

The `baseUrl` rule:

| `baseUrl` | Path in `@GET` | Address |
|---|---|---|
| `https://dog.ceo/api/` | `breeds/list/all` | `https://dog.ceo/api/breeds/list/all` — right |
| `https://dog.ceo/api/` | `/breeds/list/all` | `https://dog.ceo/breeds/list/all` — a leading slash drops `api/` |
| `https://dog.ceo/api` | any | `IllegalArgumentException: baseUrl must end in /` |

The path of a call:

```
 api.getBreeds()          → Call<BreedListResponse>   (nothing is sent yet)
 call.enqueue(callback)   → OkHttp, background thread: HTTP request
                          ← the answer: bytes of JSON
 GsonConverterFactory     → BreedListResponse         (background thread)
 callback.onResponse(...) ← main thread (Android)
```

| Method | Thread | Result | Where to use |
|---|---|---|---|
| `call.execute()` | Blocks the current thread | `Response<T>`. Throws `IOException` or a converter exception | Background thread, tests |
| `call.enqueue(callback)` | Does not block | The result comes to `Callback`; on Android in the main thread | Screens, `ViewModel` |

A `Call` runs once. For a repeat use `call.clone()`. To stop a request use `call.cancel()`. In a JVM test (no Android) the callback runs in an OkHttp thread, so a test must wait for it.

`Response<T>`:

| Method | Returns |
|---|---|
| `isSuccessful()` | `true` for 200–299 |
| `code()` | HTTP code |
| `body()` | The object `T`. `null` if the code is not 2xx |
| `errorBody()` | The body of an error answer (`ResponseBody`). Read it once |
| `headers()` | Headers |
| `message()` | Status text. Empty with HTTP/2 |

A 404 comes to `onResponse`, not to `onFailure`. `onFailure` means there is no answer (no network, timeout) or the body could not be parsed.

One `Retrofit` and one `OkHttpClient` serve the whole app. A new one per request makes a new connection pool every time.

## Where to look

- `core/…/api/DogApi.java`, `retrofit-basic/…/PostApi.java`, `retrofit-basic/…/NetworkModule.java`.
- `retrofit-basic/…/BreedsActivity.java` — the first request (`loadBreeds`, `loadHoundImages`). Logcat tag `BreedsDemo`.
- `core/src/test/…/api/DogApiTest` — the same interface against `MockWebServer`.

## Advantages and disadvantages

Advantages:
- Short code: an interface and one call.
- The converter and the HTTP client are replaceable.

Disadvantages:
- Errors of the setup show late: at the first call of a method, not at `create()`.
- The two error paths (`onFailure` and the code of `onResponse`) are easy to mix.
