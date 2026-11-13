package org.demo.network.retrofitbasic.mistakes;

import com.google.gson.annotations.SerializedName;

import org.demo.network.core.dto.BreedListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/** Wrong interfaces and DTOs for the mistakes screen. The right ones are in core. */
public final class MistakeApis {
    private MistakeApis() {
    }

    /** Mistake 5: a leading slash in the path drops "api/" from baseUrl. */
    public interface LeadingSlashApi {
        @GET("/breeds/list/all")
        Call<BreedListResponse> getBreeds();
    }

    /** Mistake 6: the return type is not Call<T>. */
    public interface NoCallApi {
        @GET("breeds/list/all")
        List<String> getBreedNames();
    }

    /** Mistake 9: "message" is an object (Map), the DTO says List. */
    public static class WrongBreedListResponse {
        @SerializedName("message")
        public List<String> breeds;
        public String status;
    }

    public interface WrongShapeApi {
        @GET("breeds/list/all")
        Call<WrongBreedListResponse> getBreeds();
    }

    /** Mistake 10: the field name differs from the JSON key and there is no @SerializedName. */
    public static class ImagesNoName {
        public List<String> imageUrls;      // the JSON key is "message"
        public String status;
    }

    public interface NoNameApi {
        @GET("breed/{breed}/images/random/{count}")
        Call<ImagesNoName> getRandomImages(@Path("breed") String breed, @Path("count") int count);
    }
}
