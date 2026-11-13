# Android network

One topic, shown in several small apps: a network request in Android. The data are dog breeds and photos (Dog CEO), and posts (JSONPlaceholder). Each module is an app with one or three screens.

## Modules

| Module | What it shows |
|---|---|
| `core` | Pure Java. DTO, `DogApi` (Retrofit), domain (`Breed`, `BreedRepository`, `HttpError`, use cases), data (`BreedMapper`, `ErrorParser`, `RetrofitBreedRepository`), JUnit and `MockWebServer` tests |
| `http-raw` | `HttpURLConnection` without libraries: `On main thread` (the exception) and `On background thread` |
| `retrofit-basic` | `Breeds`, `Posts`, `Mistakes`: first requests, write requests, nine common mistakes with a "Fix it" switch |
| `graphql` | GraphQL through Retrofit: five ready queries, a server switch, errors from `errorBody()` |
| `glide` | Photos of a breed in a grid with Glide, cache, error image, local mode |
| `picasso` | The same screen with Picasso. Photos go through the OkHttp log |
| `app` | An empty module made by the IDE project wizard. The demos do not use it |

## Find a demo

| Module | Screen | Entry class | Logcat tag |
|---|---|---|---|
| `http-raw` | one screen, two buttons | `RawHttpActivity` | `RawHttpDemo` |
| `retrofit-basic` | `Breeds` (`Load breeds`, `Load hound images`) | `BreedsActivity` | `BreedsDemo` |
| `retrofit-basic` | `Posts` (`Create`, `Get created`, `Delete`) | `PostsActivity` | `PostsDemo` |
| `retrofit-basic` | `Mistakes` (`Spinner`, `Run`, `Fix it`) | `MistakesActivity`, `mistakes/*Mode` | `MistakesDemo` |
| `graphql` | one screen | `GraphQlActivity` | `GraphQlDemo` |
| `glide` | one screen | `GlideActivity`, `ImageAdapter` | none |
| `picasso` | one screen | `PicassoActivity`, `ImageAdapter` | filter `OkHttp` |

The modes of `Mistakes`: `Cleartext`, `baseUrl without /`, `Leading slash`, `No Call`, `No converter`, `Wrong DTO shape`, `Wrong field name`, `No isSuccessful`, `Execute twice`. A mode is the class name without `Mode`, with spaces.

Filter `OkHttp` in Logcat shows the requests of `retrofit-basic`, `graphql` and `picasso` (a debug build).

## What each module demonstrates

- **`core`**: `@SerializedName` and the shape of JSON, `Call<T>` interfaces, an asynchronous repository with `ResultCallback` and `Cancellable`, an own `HttpError`, `errorBody()` parsing, tests with saved answers and `MockWebServer`.
- **`http-raw`**: the permission, the main thread rule, the eight tasks of one raw request.
- **`retrofit-basic`**: `@GET`, `@Path`, `@Query`, `@POST`, `@PUT`, `@DELETE`, `@Body`, `Callback`, `isSuccessful()`, one `OkHttpClient` with timeouts, an own interceptor and a log.
- **`graphql`**: `@POST` + `@Url`, a generic response `GraphQlResponse<JsonObject>`, a 400 and a 200 with errors.
- **`glide`**, **`picasso`**: `placeholder`, `error`, `centerCrop`, cache, rows of a `RecyclerView`.

## Run

Needs JDK 17 (Gradle JDK in the IDE) and Android SDK 34. Open the project folder in Android Studio and select a module in the run configuration. The emulator needs the internet.

```
./gradlew :core:test                # tests, no internet
./gradlew assembleDebug             # all modules
./gradlew :retrofit-basic:installDebug
```

Versions, branches (`master`, `compat/agp-8.1.1`, `compat/agp-7.4`) and the JDK setting: `docs/versions.md`. The docs of each topic: `docs/`. The plan and what is not in the code: `docs/plan.md`.
