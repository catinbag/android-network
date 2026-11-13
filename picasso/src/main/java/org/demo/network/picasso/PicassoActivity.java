package org.demo.network.picasso;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.demo.network.core.api.DogApi;
import org.demo.network.core.data.BreedMapper;
import org.demo.network.core.data.ErrorParser;
import org.demo.network.core.data.RetrofitBreedRepository;
import org.demo.network.core.domain.BreedRepository;
import org.demo.network.core.domain.Cancellable;
import org.demo.network.core.domain.HttpError;
import org.demo.network.core.domain.ResultCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Stage 11 (second library): Picasso. The same screen as the glide module.
 * The difference to see: Picasso loads photos through OkHttp, so one OkHttpClient
 * serves both the list (Retrofit) and the photos (Picasso). Logcat, filter "OkHttp",
 * shows both kinds of requests. Glide uses its own network code and is not in this log.
 *
 * The request is in the Activity to keep the example short. A rotation sends it again.
 * The right place is a ViewModel (stage 10).
 */
public class PicassoActivity extends AppCompatActivity {
    private static final int MAX_PHOTOS = 120;
    private static final String[] BREEDS = {"hound", "husky", "bulldog", "unknown"};

    private static OkHttpClient sharedClient;

    private final ExecutorService background = Executors.newSingleThreadExecutor();
    private OkHttpClient client;
    private Picasso picasso;
    private ImageAdapter adapter;
    private BreedRepository repository;
    private Cancellable request;

    private Spinner breedSpinner;
    private SwitchCompat localImages;
    private SwitchCompat brokenUrls;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picasso);
        client = createClient();
        picasso = createPicasso();
        adapter = new ImageAdapter(picasso);
        repository = createRepository(client);

        breedSpinner = findViewById(R.id.breed_spinner);
        localImages = findViewById(R.id.local_images);
        brokenUrls = findViewById(R.id.broken_urls);
        status = findViewById(R.id.status);

        RecyclerView grid = findViewById(R.id.images);
        grid.setLayoutManager(new GridLayoutManager(this, 3));
        grid.setAdapter(adapter);

        ArrayAdapter<String> breeds = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, BREEDS);
        breeds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        breedSpinner.setAdapter(breeds);
        // The Spinner calls onItemSelected once at start: this is the first load.
        breedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                load();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        CompoundButton.OnCheckedChangeListener reload = (button, checked) -> load();
        localImages.setOnCheckedChangeListener(reload);
        brokenUrls.setOnCheckedChangeListener(reload);

        findViewById(R.id.clear_cache).setOnClickListener(v -> clearCache());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (request != null) request.cancel();
        picasso.shutdown();
        background.shutdown();
    }

    /** One client for the list and for the photos: disk cache, log (debug only). */
    private OkHttpClient createClient() {
        synchronized (PicassoActivity.class) {
            // One client for the whole process: a rotation must not open a second Cache
            // on the same directory (OkHttp allows one Cache instance per directory).
            if (sharedClient == null) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(BuildConfig.DEBUG
                        ? HttpLoggingInterceptor.Level.BASIC     // a line per request: no photo bytes in the log
                        : HttpLoggingInterceptor.Level.NONE);
                sharedClient = new OkHttpClient.Builder()
                        .cache(new Cache(new File(getApplicationContext().getCacheDir(), "picasso-http"),
                                50L * 1024 * 1024))
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .addInterceptor(logging)
                        .build();
            }
            return sharedClient;
        }
    }

    private Picasso createPicasso() {
        return new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(client))   // photos go through our OkHttpClient
                .build();
    }

    private static BreedRepository createRepository(OkHttpClient client) {
        Gson gson = new Gson();
        DogApi api = new Retrofit.Builder()
                .baseUrl("https://dog.ceo/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(DogApi.class);
        return new RetrofitBreedRepository(api, new BreedMapper(), new ErrorParser(gson));
    }

    private void load() {
        if (request != null) request.cancel();
        if (localImages.isChecked()) {
            show(localUrls());
            return;
        }
        status.setText(R.string.loading);
        adapter.setImageUrls(Collections.<String>emptyList());
        String breed = (String) breedSpinner.getSelectedItem();
        request = repository.getImages(breed, new ResultCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> urls) {
                show(urls.size() > MAX_PHOTOS ? urls.subList(0, MAX_PHOTOS) : urls);
            }

            @Override
            public void onError(Throwable error) {
                // HttpError: the text of the server (404 for "unknown"). Other errors: no network, timeout.
                status.setText(error instanceof HttpError && ((HttpError) error).getServerMessage() != null
                        ? ((HttpError) error).getServerMessage()
                        : error.toString());
            }
        });
    }

    /** Six local files repeated: shows the same photo in many cells (memory cache). */
    private static List<String> localUrls() {
        List<String> urls = new ArrayList<>();
        for (int round = 0; round < 4; round++) {
            for (int i = 1; i <= 6; i++) {
                urls.add("file:///android_asset/images/" + i + ".png");
            }
        }
        return urls;
    }

    private void show(List<String> urls) {
        List<String> shown = urls;
        if (brokenUrls.isChecked()) {
            // A host that does not exist: Picasso cannot load the photo and shows the error image
            shown = new ArrayList<>();
            for (String url : urls) shown.add(url.replace("images.dog.ceo", "images.invalid"));
        }
        status.setText(getString(R.string.images_count, shown.size()));
        adapter.setImageUrls(shown);
    }

    /**
     * Disk cache first (not in the main thread), then a new Picasso: shutdown() drops
     * the memory cache. The old instance is shut down only after the new one is ready,
     * so no cell is bound to a shut down Picasso.
     */
    private void clearCache() {
        background.execute(() -> {
            try {
                client.cache().evictAll();
            } catch (IOException ignored) {
                // the demo only needs the cache to be empty; a failure leaves old files
            }
            runOnUiThread(() -> {
                Picasso old = picasso;
                picasso = createPicasso();
                adapter.setPicasso(picasso);
                old.shutdown();
                Toast.makeText(this, R.string.cache_cleared, Toast.LENGTH_SHORT).show();
            });
        });
    }
}
