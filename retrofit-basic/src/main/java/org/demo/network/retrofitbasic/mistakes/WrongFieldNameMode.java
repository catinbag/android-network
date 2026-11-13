package org.demo.network.retrofitbasic.mistakes;

import org.demo.network.core.api.DogApi;
import org.demo.network.core.dto.ImagesResponse;

import retrofit2.Response;
import retrofit2.Retrofit;

/** Mistake 10: no @SerializedName. No error: the field stays null. */
public class WrongFieldNameMode extends MistakeMode {
    public WrongFieldNameMode() {
        super("Wrong field name");
    }

    @Override
    public void run(boolean fix, Reporter out) {
        Retrofit retrofit = gsonRetrofit("https://dog.ceo/api/").build();
        if (fix) {
            retrofit.create(DogApi.class).getRandomImages("hound", 3)
                    .enqueue(new ShowCallback<ImagesResponse>(out) {
                        @Override
                        protected String handle(Response<ImagesResponse> r) {
                            return "imageUrls = " + r.body().imageUrls;
                        }
                    });
        } else {
            retrofit.create(MistakeApis.NoNameApi.class).getRandomImages("hound", 3)
                    .enqueue(new ShowCallback<MistakeApis.ImagesNoName>(out) {
                        @Override
                        protected String handle(Response<MistakeApis.ImagesNoName> r) {
                            return "imageUrls = " + r.body().imageUrls + "\nstatus = " + r.body().status;
                        }
                    });
        }
    }
}
