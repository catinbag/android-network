# Network in the architecture

## Idea

The network lives in the data layer, behind a repository interface. The screen and the use case do not know Retrofit or Gson. A network answer comes later, so the repository is asynchronous (a callback and a way to cancel).

## Status in this repository

`core` has the domain and data layers with tests. The presentation layer (`ViewModel`, `LiveData`, states, `Activity`) is **not** in this repository: the module `app` is an empty stub from the IDE wizard. The lecture shows the `ViewModel` code only as slides (stage 10). The `glide` and `picasso` modules request the list of photos from `RetrofitBreedRepository` directly in an `Activity` (a comment in the code says this is not the right place).

## Roles

| Role | What it does | Class (`core`, package `org.demo.network.core`) |
|---|---|---|
| DTO | Shape of the JSON | `dto/BreedListResponse`, `ImagesResponse`, `ErrorDto`, `PostDto` |
| API | Retrofit interface | `api/DogApi` |
| Mapper | DTO to model, sorted by name | `data/BreedMapper` |
| Error parser | `errorBody()` to `HttpError` | `data/ErrorParser` |
| Repository (impl) | Calls `DogApi`, maps, parses errors | `data/RetrofitBreedRepository` |
| Repository (interface) | What the rest of the app sees | `domain/BreedRepository` |
| Model | Immutable `Breed` | `domain/Breed` |
| Callback and handle | Result and cancel, own interfaces | `domain/ResultCallback`, `domain/Cancellable` |
| Error | HTTP error of the server | `domain/HttpError` (`code`, text of the server) |
| Use cases | Ask the repository | `domain/GetBreedsUseCase`, `GetBreedImagesUseCase` |

## How it works

The change from lecture 4: a synchronous repository does not work for the network.

```java
// lecture 4: the answer at once. A network cannot do this
List<Movie> getAll();

// here: the answer comes later
public interface BreedRepository {
    Cancellable getBreeds(ResultCallback<List<Breed>> callback);
    Cancellable getImages(String breed, ResultCallback<List<String>> callback);
}
```

Layers:

```
 Activity ──► ViewModel ──► GetBreedsUseCase ──► BreedRepository (interface, domain)
 (slides)     (slides)                                  ▲
                                         RetrofitBreedRepository (data)
                                           DogApi, BreedMapper, ErrorParser
```

`RetrofitBreedRepository.getBreeds`:
- success: `mapper.map(r.body())` goes to `onSuccess`;
- a code that is not 2xx: `errorParser.toError(r)` makes an `HttpError`, which goes to `onError`;
- `onFailure`: the original exception goes to `onError`, but not when `call.isCanceled()` (a cancelled request is silent);
- it returns `call::cancel` as a `Cancellable`.

`ErrorParser` reads `errorBody()` once and closes it (`try`-with-resources). If the body is not JSON, only the code stays: `HttpError(code, null)`.

`execute` of a use case is asynchronous and takes a callback. It is not `Call.execute()` of Retrofit.

## The `ViewModel` (slides only)

```java
public BreedsViewModel(GetBreedsUseCase getBreeds) {
    this.getBreeds = getBreeds;
    load();                       // once: after a rotation the ViewModel is the same
}
```

`load()` goes in the constructor, not in `onCreate`: a rotation creates a new `Activity` and would send the request again. The state is one `LiveData`:

| State | On the screen |
|---|---|
| `LOADING` | Progress |
| `DATA` | The list |
| `EMPTY` | "No breeds" |
| `ERROR` | The text of the error and "Retry" |

`onCleared` cancels the request. `setValue` is allowed because Retrofit calls the callback in the main thread on Android.

## Advantages and disadvantages

Advantages:
- Domain and screen are tested and changed without Retrofit.
- A change of the server format touches the DTO and the mapper only.
- The screen gets one error type (`HttpError`) for answers with an error code.

Disadvantages:
- More classes than a request in an `Activity`.
- Manual assembly of objects (no DI library) is long for a big app.
