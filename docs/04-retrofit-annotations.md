# Retrofit annotations

## Idea

Annotations describe the request: the method, the path and where each parameter goes (path, query, body, header).

## Table

| Annotation | Where | What it does |
|---|---|---|
| `@GET`, `@POST`, `@PUT`, `@PATCH`, `@DELETE` | Method | HTTP method and path |
| `@Path("x")` | Parameter | Value in place of `{x}` in the path |
| `@Query("x")` | Parameter | Parameter `?x=…` |
| `@Body` | Parameter | Object to JSON in the request body (converter) |
| `@Header("x")` | Parameter | One header with the value of the parameter |
| `@Headers({...})` | Method | Constant headers of the method |
| `@FormUrlEncoded` + `@Field("x")` | Method + parameter | Body as a form `x=1&y=2` |
| `@FieldMap` | Parameter | All form fields from a `Map` |
| `@Multipart` + `@Part` | Method + parameter | Body of parts: file, image, text |
| `@Url` | Parameter | The address as a parameter. A full address replaces `baseUrl`, a relative one is added to it |

`@Path` goes into the path, `@Query` after the question mark:

| Annotation | Call | Request |
|---|---|---|
| `@Path("breed")`, `@Path("count")` | `getRandomImages("hound", 3)` | `/breed/hound/images/random/3` |
| `@Query("userId")`, `@Query("_limit")` | `getPosts(1, 5)` | `/posts?userId=1&_limit=5` |

## In the project: `PostApi` (JSONPlaceholder)

```java
@GET("posts")
Call<List<PostDto>> getPosts(@Query("userId") int userId, @Query("_limit") int limit);

@POST("posts")
Call<PostDto> createPost(@Body PostDto post);

@GET("posts/{id}")
Call<PostDto> getPost(@Path("id") int id);

@PUT("posts/{id}")
Call<PostDto> replacePost(@Path("id") int id, @Body PostDto post);

@DELETE("posts/{id}")
Call<Void> deletePost(@Path("id") int id);      // the answer body is not needed
```

`Call<Void>` is for answers without a useful body. The `PostsActivity` screen uses `createPost`, `getPost` and `deletePost`. `getPosts` and `replacePost` are in the interface but no button calls them.

`@Url` is used in the `graphql` module: the address of the server is a parameter of `query`, so one interface works with two servers.

## Forms and files (not in the code, only in the lecture)

```java
@FormUrlEncoded
@POST("login")
Call<TokenDto> login(@Field("user") String user, @Field("password") String password);

@Multipart
@POST("upload")
Call<ResponseBody> upload(@Part MultipartBody.Part photo);

@GET
Call<ResponseBody> download(@Url String fullUrl);
```

The addresses `login` and `upload` are examples. JSONPlaceholder does not have them.
