# GraphQL

## Idea

GraphQL is a query language for an API. The client writes which fields it needs. There is one address, and the answer repeats the shape of the query. The transport is usually an HTTP POST, so Retrofit is enough: no new library.

## Roles

| Role | What it does | In this project |
|---|---|---|
| Schema | Types and operations of the API, on the server | GraphQL Zero (not ours) |
| Query | Reads data | `{ post(id: 1) { title } }` |
| Mutation | Changes data | `mutation { createPost(...) { id title } }` |
| Subscription | A stream of events (WebSocket) | Not in GraphQL Zero. Slide only |
| `GraphQlApi` | One POST method | `graphql` module |

## How it works

Post and author in one request, REST against GraphQL:

```
 REST                                         GraphQL
 GET /posts/1  → {id, userId, title, body}    POST /api
 GET /users/1  → {id, name, username, email,  { post(id: 1) { title user { name } } }
                  address, phone, …}           → {"data": {"post": {"title": "...",
 2 requests, 2 fields needed out of 12                         "user": {"name": "Leanne Graham"}}}}
```

Transport: the body is `{"query": "...", "variables": {...}}`, the answer is `{"data": ..., "errors": [...]}`.

```java
public interface GraphQlApi {
    @POST
    Call<GraphQlResponse<JsonObject>> query(@Url String endpoint, @Body GraphQlRequest request);
}
```

`data` is a `JsonObject` because its shape depends on the query. `GraphQlRequest` has `query` and `variables`. Gson skips a null `variables`.

Endpoints in `GraphQlActivity`: `https://graphqlzero.almansi.me/api` and the backup `https://countries.trevorblades.com/`. Retrofit needs a `baseUrl` even with `@Url`; a full `@Url` replaces it.

Errors are the main trap. The code of HTTP depends on the server:

| Server | A wrong field name | Where the error is |
|---|---|---|
| GraphQL Zero | HTTP 400 | `body()` is `null`, `errors` are in `errorBody()` |
| Countries | HTTP 200 | `data` is `null`, `errors` are in `body()` |

Rule: for 2xx check `body().errors`. For a code that is not 2xx parse `errorBody()` as `GraphQlResponse`. `GraphQlActivity.readErrorBody` does the second one and shows the raw text if the body is not GraphQL JSON.

## Demo (`GraphQlActivity`)

The `Spinner` has five ready queries. The text can be edited before "Send". A query 5 switches the server to Countries.

| # | Query | What to see |
|---|---|---|
| 1 | `{ post(id: 1) { title } }` | Only `title` |
| 2 | `{ post(id: 1) { title user { name } } }` | `user.name` appeared, Java code is the same |
| 3 | `mutation { createPost(...) { id title } }` | `id` of the new post (the server does not save) |
| 4 | `{ post(id: 1) { titel } }` | HTTP 400, `errors` from `errorBody()` |
| 5 | `{ country(code: "RU") { name capital currency } }` | Backup server |

The text of the error for the typo `titel` is expected to be `Cannot query field "titel" on type "Post"`. It was not compared with the real answer.

Logcat tag: `GraphQlDemo`.

## Advantages and disadvantages

Advantages:
- The client takes only the fields it needs. Related data come in one request.
- One address. The schema checks a query before it runs.

Disadvantages:
- A POST is not cached by HTTP. A client cache is needed (Apollo).
- The server is more complex (schema, protection from heavy queries).
- The error is in the body, and the HTTP code depends on the server.

For an Android app with many screens and different sets of related data GraphQL is convenient. For a simple API REST is simpler. A typed client for GraphQL is Apollo Kotlin (code generation); this project sends the query text by hand.
