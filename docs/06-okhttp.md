# OkHttp under Retrofit

## Idea

Retrofit builds the request. OkHttp sends it. A custom `OkHttpClient` is passed to `Retrofit.Builder().client(...)`. In the client you set timeouts, interceptors and a cache.

## Roles

| Role | What it does | In this project |
|---|---|---|
| `OkHttpClient` | Connections, timeouts, cache, interceptors | `NetworkModule.createClient()` |
| Interceptor | Sees every request before sending and every answer | A header `Accept`, `HttpLoggingInterceptor` |
| `HttpLoggingInterceptor` | Writes requests and answers to Logcat (tag `OkHttp`) | `retrofit-basic`, `graphql`, `picasso` |

## How it works

Version: Retrofit 2.11.0 brings OkHttp 3.14.9 (checked in the POM of `retrofit-2.11.0`). `logging-interceptor` and `mockwebserver` use the same 3.14.9.

```java
HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
logging.setLevel(BuildConfig.DEBUG ? Level.BODY : Level.NONE);   // BODY only in debug

OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(chain -> chain.proceed(
                chain.request().newBuilder().header("Accept", "application/json").build()))
        .addInterceptor(logging)
        .build();
```

Order matters. The own interceptor is first, so the log (second) shows the `Accept` header:

```
 Retrofit → [own interceptor] → [log] → network
          ←                   ←       ← the answer
```

Timeouts: the default is 10 seconds for connect, read and write. A timeout comes to `onFailure` as `SocketTimeoutException`.

`BODY` logs everything, also tokens and personal data. Use it only in debug builds (`BuildConfig.DEBUG`; the modules set `buildFeatures { buildConfig = true }` because AGP 8 turns it off by default). `HttpLoggingInterceptor` does not print the bytes of binary bodies (photos), it writes a short line.

Expected log of `Load breeds` in `retrofit-basic`: `--> GET https://dog.ceo/api/breeds/list/all`, the header `Accept: application/json`, `<-- 200 … (… ms)`, the body.

One `OkHttpClient` per app. A client has a connection pool and threads. In `retrofit-basic` the dog and the posts `Retrofit` share one client. In `picasso` the client serves Retrofit (the list) and Picasso (the photos).

## Advantages and disadvantages

Advantages:
- One place for timeouts, headers, log and cache.
- Interceptors work for all requests, also for a library that uses the same client (Picasso).

Disadvantages:
- Glide has its own network code and does not use this client. Its requests are not in the log.
- An interceptor with a mistake affects every request.
