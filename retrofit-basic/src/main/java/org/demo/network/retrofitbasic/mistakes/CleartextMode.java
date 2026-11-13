package org.demo.network.retrofitbasic.mistakes;

import org.demo.network.core.dto.PostDto;
import org.demo.network.retrofitbasic.PostApi;

import retrofit2.Response;

/** Mistake 3: http:// without TLS. targetSdk 28+ blocks it by default. */
public class CleartextMode extends MistakeMode {
    public CleartextMode() {
        super("Cleartext");
    }

    @Override
    public void run(boolean fix, Reporter out) {
        String baseUrl = fix
                ? "https://jsonplaceholder.typicode.com/"
                : "http://jsonplaceholder.typicode.com/";      // the mistake
        PostApi api = gsonRetrofit(baseUrl).build().create(PostApi.class);
        api.getPost(1).enqueue(new ShowCallback<PostDto>(out) {
            @Override
            protected String handle(Response<PostDto> r) {
                return r.isSuccessful() && r.body() != null ? "title: " + r.body().title : "not successful";
            }
        });
    }
}
