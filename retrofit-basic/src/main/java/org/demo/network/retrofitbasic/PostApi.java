package org.demo.network.retrofitbasic;

import org.demo.network.core.dto.PostDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/** JSONPlaceholder. baseUrl: https://jsonplaceholder.typicode.com/ */
public interface PostApi {
    @GET("posts")
    Call<List<PostDto>> getPosts(@Query("userId") int userId, @Query("_limit") int limit);

    @POST("posts")
    Call<PostDto> createPost(@Body PostDto post);

    @GET("posts/{id}")
    Call<PostDto> getPost(@Path("id") int id);      // check: does post 101 exist

    @PUT("posts/{id}")
    Call<PostDto> replacePost(@Path("id") int id, @Body PostDto post);

    @DELETE("posts/{id}")
    Call<Void> deletePost(@Path("id") int id);      // the answer body is not needed
}
