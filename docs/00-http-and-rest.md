# HTTP and REST

## Idea

HTTP is a request-response protocol. The client always starts: it sends one request and gets one answer. REST is a style of API on top of HTTP: data are resources, a resource has an address (URI), and the HTTP method says what to do with it.

## Roles

| Role | What it does | In this project |
|---|---|---|
| Client | Builds a request, reads the answer | `retrofit-basic`, `http-raw`, `graphql`, `glide`, `picasso` |
| Server | Answers requests | Dog CEO, JSONPlaceholder, GraphQL Zero (public, no key) |
| Resource | A thing with an address | `breeds/list/all`, `posts/1` |

## How it works

URL parts:

```
 https://jsonplaceholder.typicode.com/posts?userId=1&_limit=5
 └─┬─┘   └────────────┬────────────┘ └─┬─┘ └───────┬────────┘
 scheme            host             path     query (parameters)
```

Request and answer:

```
 GET /api/breeds/list/all HTTP/1.1      HTTP/1.1 200 OK
 Host: dog.ceo                          Content-Type: application/json
 Accept: application/json
                                        {"message": {...}, "status": "success"}
```

Status code classes:

| Class | Meaning | Examples |
|---|---|---|
| 2xx | Success | 200, 201, 204 |
| 3xx | Redirect, cache | 301, 304 |
| 4xx | Client error | 400, 401, 403, 404, 429 |
| 5xx | Server error | 500, 503 |

A 404 is a normal answer: the server answered, and the body can be JSON (Dog CEO: `{"status":"error","message":"Breed not found …","code":404}`).

HTTPS is HTTP over TLS. An app with `targetSdk` 28 or higher (Android 9) does not open `http://` by default.

## REST on JSONPlaceholder

| Action | Method | Address | Body | Code |
|---|---|---|---|---|
| List of posts | GET | `/posts` | none | 200 |
| One post | GET | `/posts/1` | none | 200 (no post: 404) |
| Posts of an author | GET | `/posts?userId=1` | none | 200 |
| Comments of a post | GET | `/posts/1/comments` | none | 200 |
| Create | POST | `/posts` | post JSON | 201 |
| Replace | PUT | `/posts/1` | whole post | 200 |
| Change a field | PATCH | `/posts/1` | one field | 200 |
| Delete | DELETE | `/posts/1` | none | 200 |

JSONPlaceholder answers "created" but does not save: `POST /posts` gives `201` and `id = 101`, then `GET /posts/101` gives `404`. The `PostsActivity` screen shows this.

| Method | Safe (changes nothing) | Idempotent (a repeat gives the same result) |
|---|---|---|
| GET | yes | yes |
| PUT | no | yes |
| DELETE | no | yes |
| POST | no | no: two POST calls make two posts |
| PATCH | no | not guaranteed |

A client can repeat an idempotent request after a broken connection. It must not repeat a POST without a check.

REST rules: client-server, stateless (each request carries all it needs), cache (`Cache-Control`, `ETag`), uniform interface (resource, URI, method, representation), layers. A URI has a noun, not a verb: `/posts`, not `/getAllPosts`.

## Advantages and disadvantages

Advantages:
- Simple and widely known. Works in a browser.
- GET answers can be cached by HTTP.
- Many tools: Retrofit, OkHttp, any HTTP client.

Disadvantages:
- Several requests for related data (a post, then its author).
- The server decides the shape of the answer: the client gets fields it does not need.
