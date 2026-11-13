package org.demo.network.retrofitbasic;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.demo.network.core.api.DogApi;
import org.demo.network.core.dto.BreedListResponse;
import org.demo.network.core.dto.ImagesResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Stage 5: the first request. The result goes to Logcat (tag BreedsDemo) and to the screen.
 * The request is in the Activity on purpose: the right place is stage 10 (the app module).
 */
public class BreedsActivity extends AppCompatActivity {
    private static final String TAG = "BreedsDemo";

    private final DogApi api = NetworkModule.get().dogApi();
    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breeds);
        output = findViewById(R.id.output);
        findViewById(R.id.load_breeds).setOnClickListener(v -> loadBreeds());
        findViewById(R.id.load_hound_images).setOnClickListener(v -> loadHoundImages());
    }

    private void loadBreeds() {
        api.getBreeds().enqueue(new Callback<BreedListResponse>() {
            @Override
            public void onResponse(Call<BreedListResponse> call, Response<BreedListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "breeds: " + response.body().breeds.size());
                    output.setText("breeds: " + response.body().breeds.size());
                } else {
                    Log.w(TAG, "HTTP " + response.code());   // 404, 500: this is onResponse too
                    output.setText("HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BreedListResponse> call, Throwable t) {
                Log.e(TAG, "no response", t);                 // no network, timeout, parse error
                output.setText("no response: " + t);
            }
        });
    }

    private void loadHoundImages() {
        api.getRandomImages("hound", 3).enqueue(new Callback<ImagesResponse>() {
            @Override
            public void onResponse(Call<ImagesResponse> call, Response<ImagesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StringBuilder text = new StringBuilder();
                    for (String url : response.body().imageUrls) {
                        Log.d(TAG, "image: " + url);
                        text.append(url).append('\n');
                    }
                    output.setText(text);
                } else {
                    Log.w(TAG, "HTTP " + response.code());
                    output.setText("HTTP " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ImagesResponse> call, Throwable t) {
                Log.e(TAG, "no response", t);
                output.setText("no response: " + t);
            }
        });
    }
}
