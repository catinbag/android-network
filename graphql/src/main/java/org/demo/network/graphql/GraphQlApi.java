package org.demo.network.graphql;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/** GraphQL is a plain POST with the query text in the body. The address is a parameter (@Url). */
public interface GraphQlApi {
    @POST
    Call<GraphQlResponse<JsonObject>> query(@Url String endpoint,
                                            @Body GraphQlRequest request);
}
