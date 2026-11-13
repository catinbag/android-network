package org.demo.network.retrofitbasic;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.demo.network.core.dto.PostDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Stage 6: write requests. JSONPlaceholder answers "created", but it does not save:
 * POST gives 201 and id 101, then GET /posts/101 gives 404.
 */
public class PostsActivity extends AppCompatActivity {
    private static final String TAG = "PostsDemo";
    private static final int CREATED_ID = 101;

    private final PostApi api = NetworkModule.get().postApi();
    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);
        output = findViewById(R.id.output);
        findViewById(R.id.create_post).setOnClickListener(v -> createPost());
        findViewById(R.id.get_created).setOnClickListener(v -> getCreated());
        findViewById(R.id.delete_post).setOnClickListener(v -> deletePost());
    }

    private void createPost() {
        PostDto post = new PostDto(1, "Hello", "Text of the post");
        api.createPost(post).enqueue(new Callback<PostDto>() {
            @Override
            public void onResponse(Call<PostDto> call, Response<PostDto> response) {
                String text = response.isSuccessful() && response.body() != null
                        ? "create: HTTP " + response.code() + ", id = " + response.body().id
                        : "create: HTTP " + response.code();
                report(text);
            }

            @Override
            public void onFailure(Call<PostDto> call, Throwable t) {
                report("create: no response, " + t);
            }
        });
    }

    private void getCreated() {
        api.getPost(CREATED_ID).enqueue(new Callback<PostDto>() {
            @Override
            public void onResponse(Call<PostDto> call, Response<PostDto> response) {
                report("get /posts/" + CREATED_ID + ": HTTP " + response.code());
            }

            @Override
            public void onFailure(Call<PostDto> call, Throwable t) {
                report("get: no response, " + t);
            }
        });
    }

    private void deletePost() {
        api.deletePost(CREATED_ID).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                report("delete /posts/" + CREATED_ID + ": HTTP " + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                report("delete: no response, " + t);
            }
        });
    }

    private void report(String text) {
        Log.d(TAG, text);
        output.setText(text);
    }
}
