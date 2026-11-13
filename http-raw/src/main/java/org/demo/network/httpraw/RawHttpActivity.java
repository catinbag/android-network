package org.demo.network.httpraw;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.demo.network.core.dto.BreedListResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Stage 4: a request without libraries. "On main thread" fails with NetworkOnMainThreadException.
 * "On background thread" is the working version: eight tasks for one request.
 */
public class RawHttpActivity extends AppCompatActivity {
    private static final String TAG = "RawHttpDemo";
    private static final String BREEDS_URL = "https://dog.ceo/api/breeds/list/all";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Gson gson = new Gson();
    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw_http);
        output = findViewById(R.id.output);
        findViewById(R.id.on_main_thread).setOnClickListener(v -> loadOnMainThread());
        findViewById(R.id.on_background_thread).setOnClickListener(v -> loadBreeds());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    /** The mistake: the request runs in the main thread. */
    private void loadOnMainThread() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(BREEDS_URL).openConnection();
            int code = conn.getResponseCode();                  // NetworkOnMainThreadException is thrown here
            output.setText("HTTP " + code);
            conn.disconnect();
        } catch (IOException | RuntimeException e) {            // caught only to keep the app open
            showError(e);
        }
    }

    private void loadBreeds() {
        executor.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(BREEDS_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10_000);
                int code = conn.getResponseCode();
                String body = readAll(conn.getInputStream());     // own method: bytes -> string
                BreedListResponse dto = gson.fromJson(body, BreedListResponse.class);
                mainHandler.post(() -> show(code, dto));           // back to the main thread
            } catch (IOException e) {
                mainHandler.post(() -> showError(e));
            } finally {
                if (conn != null) conn.disconnect();
            }
        });
    }

    private static String readAll(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while ((n = in.read(buffer)) != -1) out.write(buffer, 0, n);
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    private void show(int code, BreedListResponse dto) {
        String text = "HTTP " + code + ", breeds: " + dto.breeds.size();
        Log.d(TAG, text);
        output.setText(text);
    }

    private void showError(Exception e) {
        Log.e(TAG, "request failed", e);                           // the exception and its stack are in Logcat
        output.setText(e.getClass().getName() + "\n" + e.getMessage());
    }
}
