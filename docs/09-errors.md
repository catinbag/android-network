# Network errors

## Idea

A request ends in one of two ways. Either there is no answer (`onFailure`), or there is an answer with a code (`onResponse`). A code that is not 2xx is also an answer.

## Which method

| What happened | `Callback` method | Sign | What to show |
|---|---|---|---|
| No network | `onFailure` | `UnknownHostException`, `ConnectException` | "No connection", "Retry" |
| The server did not answer in time | `onFailure` | `SocketTimeoutException` | "Server is not responding", "Retry" |
| The JSON does not fit the DTO | `onFailure` | `JsonSyntaxException` (wrong shape) | "Data error". A mistake in the code or the API |
| No resource | `onResponse` | `code() == 404` | "Not found" |
| Server error | `onResponse` | `code() >= 500` | "Service is not available", "Retry" |
| Request cancelled | `onFailure` | `call.isCanceled()` | Nothing |

Decision tree:

```
 did an answer come?
   no  → onFailure ── cancelled? → nothing
                   └─ else       → no network / timeout / parse error
   yes → onResponse ── isSuccessful()? yes → body()
                                       no  → code(), errorBody()
```

Dog CEO answers an error with JSON: `{"status":"error","message":"Breed not found (main breed does not exist)","code":404}`. `ErrorParser` turns it into `HttpError(404, "Breed not found …")`.

Do not retry a POST after a timeout without a check: it can make two objects.

## The mistakes screen

`retrofit-basic`, screen `Mistakes`: choose a mode, press `Run` (the mistake), switch on `Fix it`, press `Run` (the right code). The result is on the screen and in Logcat (tag `MistakesDemo`). The code of the modes is in `retrofit-basic/…/mistakes/`.

The texts below are expected from the sources of Retrofit, OkHttp, Gson and Android. They were **not compared** with the output of a device.

| Mode | Run (the mistake) | `Fix it` |
|---|---|---|
| `Cleartext` | `onFailure`: `java.net.UnknownServiceException: CLEARTEXT communication to jsonplaceholder.typicode.com not permitted by network security policy` | `HTTP 200`, `title: …` |
| `baseUrl without /` | Exception of the call: `IllegalArgumentException: baseUrl must end in /: https://dog.ceo/api` | `breeds: …` |
| `Leading slash` | `HTTP 404` expected, no exception; `request: https://dog.ceo/breeds/list/all` | `HTTP 200`, `request: https://dog.ceo/api/breeds/list/all` |
| `No Call` | Exception of the call: `IllegalArgumentException: Unable to create call adapter for java.util.List<java.lang.String>` | `breeds: …` |
| `No converter` | Exception of the call: `IllegalArgumentException: Unable to create converter for class …BreedListResponse` | `breeds: …` |
| `Wrong DTO shape` | `onFailure`: `com.google.gson.JsonSyntaxException: … Expected BEGIN_ARRAY but was BEGIN_OBJECT …`. The server answered 200 (see the `OkHttp` log) | `breeds: …` |
| `Wrong field name` | `imageUrls = null`, `status = success` | The list of three URLs |
| `No isSuccessful` | `onResponse, HTTP 404`, then `java.lang.NullPointerException` inside `onResponse` | `HTTP 404`, `errorBody:` the JSON of the server |
| `Execute twice` | Exception of the call: `java.lang.IllegalStateException: Already executed.`, then the result of the first call | Two results: `call.clone()` |

An exception that leaves `onResponse` would close the app on Android. The screen catches it inside the callback (`ShowCallback`) to show it.

## Other mistakes (slides only)

| Mistake | Cause | Fix |
|---|---|---|
| No `INTERNET` permission | Missing line in the manifest | Add it |
| Network in the main thread | `NetworkOnMainThreadException` | Background thread or `enqueue` (`http-raw`) |
| `retrofit` and `converter-gson` with different versions | Build errors or `NoSuchMethodError` | One version (`libs.versions.toml`) |
| Request in `Activity` without cancel | A rotation repeats it; the answer comes to a closed `Activity` | `ViewModel` and `onCleared` |
| New `Retrofit` for each request | A new connection pool each time | One object per app |
| `BODY` log in a release build | Tokens and personal data in the log | `BuildConfig.DEBUG` |
| GraphQL: only the HTTP code is checked | `errors` are in `body()` (200) or in `errorBody()` (400) | See `05-graphql.md` |
