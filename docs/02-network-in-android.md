# Network in Android

## Idea

Android has three rules for the network. A request without a library needs many lines: this is the reason to use Retrofit and OkHttp.

## Rules

| Rule | What to do | Without it |
|---|---|---|
| Permission | `<uses-permission android:name="android.permission.INTERNET"/>` in the manifest | The request fails with a connection exception. The text depends on the Android version |
| Not the main thread | Run the request in a background thread | `android.os.NetworkOnMainThreadException` |
| HTTPS | Use `https://` | `targetSdk` 28+: `UnknownServiceException: CLEARTEXT communication … not permitted` |

The main thread draws the screen. A request takes hundreds of milliseconds or more, and the screen would freeze:

```
 main thread:  [frame][frame][frame][ request 800 ms ........ ][frame]
 right:
 main thread:  [frame][frame][frame][frame][frame][show the answer]
 background:          [ request 800 ms ........ ]──┘
```

## How it works: `HttpURLConnection`

`http-raw`, `RawHttpActivity.loadBreeds` makes one request without libraries. Eight tasks:

1. Start a background thread (`executor.execute`).
2. Set a timeout (`setConnectTimeout`).
3. Get the code (`getResponseCode`).
4. Read the bytes (`readAll`, own method).
5. Parse the JSON (`gson.fromJson`).
6. Return to the main thread (`mainHandler.post`).
7. Handle errors (`catch`).
8. Close the connection (`disconnect`).

`loadOnMainThread` does the same request in the main thread. The exception is thrown at `getResponseCode()`. The screen catches it to keep the app open and shows the class and the text.

Libraries:

```
 Retrofit   an interface of the API becomes HTTP requests; an answer becomes an object (Gson)
    │
 OkHttp     connections, timeouts, interceptors, cache, HTTP/2
    │
 Okio       reading and writing bytes
    │
 socket, TLS
```

Volley (Google) is another HTTP library. This project does not use it.

## Where to look

- `http-raw/src/main/AndroidManifest.xml` — the permission.
- `http-raw/…/RawHttpActivity.java` — both methods. Logcat tag `RawHttpDemo`.

## Advantages and disadvantages

`HttpURLConnection` has no dependency. But the code for threads, timeouts, reading and parsing is yours, and an error in one of the eight tasks is easy to make.
