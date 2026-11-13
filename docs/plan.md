# Plan: network demo project

## Goal

Show the stages of a network request on one set of data (dog breeds and photos): from `HttpURLConnection` to Retrofit, OkHttp, GraphQL and an image library. The demo is for a lecture: students watch, nobody writes code.

## Principles

- Java only (source level 1.8), `minSdk` 26, `compileSdk` and `targetSdk` 34.
- Async only with `Call.enqueue`, `Callback` and `LiveData`. No RxJava, no coroutines.
- `core` is a Java library: DTO, `DogApi`, domain, data, tests. No Android code.
- One topic per file: a code fragment for a slide is short.
- Public APIs without a key: Dog CEO, JSONPlaceholder, GraphQL Zero (backup: Countries).

## Modules and stages

| Module | Stage | What it shows |
|---|---|---|
| `core` | 3, 5, 10, 12, 13 | DTO, `DogApi`, mapper, `ErrorParser`, `HttpError`, `RetrofitBreedRepository`, 15 tests |
| `http-raw` | 4 | `HttpURLConnection`: main thread (exception) and background thread |
| `retrofit-basic` | 5, 6, 9, 12 | `BreedsActivity`, `PostsActivity`, `MistakesActivity` (9 modes), `NetworkModule` |
| `graphql` | 7 | GraphQL through `@POST` + `@Url`, 5 ready queries |
| `glide` | 11 | Photos of a breed with Glide |
| `picasso` | 11 | The same screen with Picasso (through OkHttp) |
| `app` | 0, 10 | Empty stub from the IDE wizard. No demo |

## Not in the code

These parts of the lecture exist only as slides:
- the `ViewModel` with `LiveData` and the states `LOADING`, `DATA`, `EMPTY`, `ERROR` (stage 10);
- the offline mode with an OkHttp interceptor that answers from `assets` (stage 12);
- the ready-made app "Dog breeds" with a list and a grid (stage 0);
- the stage 8 (gRPC): slides only by decision.

The `glide` and `picasso` modules have a local mode (photos from `assets`, no network) as a backup for the photo screen only.

## Status

| Part | Written | Built and run |
|---|---|---|
| `core` and its tests | yes | `:core:test` ran OK (author) |
| `http-raw`, `retrofit-basic`, `graphql`, `glide`, `picasso` | yes | Built and run by the author; the checklists were reported OK |
| Texts of the exceptions of `MistakesActivity` | Expected from the library sources | Not compared with the device (`09-errors.md`) |
| Branches `compat/agp-8.1.1`, `compat/agp-7.4` | no | no |

## Docs

| File | Topic |
|---|---|
| `00-http-and-rest.md` | HTTP, URL, codes, REST |
| `01-json-and-gson.md` | JSON, DTO, Gson, mapper |
| `02-network-in-android.md` | Rules of Android, `HttpURLConnection` |
| `03-retrofit.md` | Interface, builder, `Call`, `Callback`, `Response` |
| `04-retrofit-annotations.md` | Annotations, `PostApi` |
| `05-graphql.md` | GraphQL through Retrofit |
| `06-okhttp.md` | Client, timeouts, interceptors, log |
| `07-architecture.md` | Repository, mapper, errors, `ViewModel` (slides) |
| `08-images.md` | Glide and Picasso |
| `09-errors.md` | Errors of the network, the mistakes screen |
| `10-testing.md` | `MockWebServer` |
| `versions.md` | Versions, branches, JDK |
