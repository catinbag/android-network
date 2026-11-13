package org.demo.network.graphql;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Stage 7: GraphQL through Retrofit. Pick a ready query, press Send.
 * The result (HTTP code, data, errors) is in Logcat (tag GraphQlDemo) and on the screen.
 */
public class GraphQlActivity extends AppCompatActivity {
    private static final String TAG = "GraphQlDemo";
    private static final String ZERO_URL = "https://graphqlzero.almansi.me/api";
    private static final String COUNTRIES_URL = "https://countries.trevorblades.com/";
    private static final Type RESPONSE_TYPE = new TypeToken<GraphQlResponse<JsonObject>>() { }.getType();

    /** A ready query for the Spinner. */
    private static class Sample {
        final String title;
        final String text;
        final boolean countries;

        Sample(String title, String text, boolean countries) {
            this.title = title;
            this.text = text;
            this.countries = countries;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private final Gson gson = new Gson();
    private GraphQlApi api;
    private EditText queryText;
    private SwitchCompat countriesServer;
    private Spinner spinner;
    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphql);
        api = createApi();
        queryText = findViewById(R.id.query_text);
        countriesServer = findViewById(R.id.countries_server);
        spinner = findViewById(R.id.query_spinner);
        output = findViewById(R.id.output);

        List<Sample> samples = Arrays.asList(
                new Sample("1. Title only", "{ post(id: 1) { title } }", false),
                new Sample("2. Title and author", "{ post(id: 1) { title user { name } } }", false),
                new Sample("3. Mutation createPost",
                        "mutation { createPost(input: { title: \"Hi\", body: \"Text\" }) { id title } }", false),
                new Sample("4. Typo in a field", "{ post(id: 1) { titel } }", false),
                new Sample("5. Countries (backup)", "{ country(code: \"RU\") { name capital currency } }", true));
        ArrayAdapter<Sample> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, samples);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Sample sample = samples.get(position);
                queryText.setText(sample.text);                 // the text can be edited before Send
                countriesServer.setChecked(sample.countries);
                output.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        findViewById(R.id.send).setOnClickListener(v -> send());
    }

    private static GraphQlApi createApi() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
        // baseUrl is required by Retrofit, but a full @Url replaces it
        return new Retrofit.Builder()
                .baseUrl("https://graphqlzero.almansi.me/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GraphQlApi.class);
    }

    private void send() {
        String endpoint = countriesServer.isChecked() ? COUNTRIES_URL : ZERO_URL;
        GraphQlRequest request = new GraphQlRequest(queryText.getText().toString(), null);
        output.setText("");
        api.query(endpoint, request).enqueue(new Callback<GraphQlResponse<JsonObject>>() {
            @Override
            public void onResponse(Call<GraphQlResponse<JsonObject>> call,
                                   Response<GraphQlResponse<JsonObject>> response) {
                if (response.isSuccessful()) {
                    // HTTP 2xx: data and errors are in body() (Countries answers 200 even for an error)
                    report(response.code(), response.body(), false);
                } else {
                    // HTTP 400 (GraphQL Zero): body() is null, errors are in errorBody()
                    report(response.code(), readErrorBody(response), true);
                }
            }

            @Override
            public void onFailure(Call<GraphQlResponse<JsonObject>> call, Throwable t) {
                Log.e(TAG, "no response", t);
                output.setText("no response: " + t);
            }
        });
    }

    /** errorBody() is read once. If the text is not JSON of GraphQlResponse, we show the raw text. */
    private GraphQlResponse<JsonObject> readErrorBody(Response<?> response) {
        GraphQlResponse<JsonObject> parsed = new GraphQlResponse<>();
        try (ResponseBody body = response.errorBody()) {
            String raw = body == null ? "" : body.string();
            try {
                GraphQlResponse<JsonObject> result = gson.fromJson(raw, RESPONSE_TYPE);
                if (result != null) return result;
            } catch (RuntimeException e) {
                // not GraphQL JSON: fall through to the raw text
            }
            GraphQlError error = new GraphQlError();
            error.message = "(not GraphQL JSON) " + raw;
            parsed.errors = Arrays.asList(error);
        } catch (IOException e) {
            GraphQlError error = new GraphQlError();
            error.message = "cannot read errorBody(): " + e;
            parsed.errors = Arrays.asList(error);
        }
        return parsed;
    }

    private void report(int code, GraphQlResponse<JsonObject> answer, boolean fromErrorBody) {
        StringBuilder text = new StringBuilder();
        text.append("HTTP ").append(code).append('\n');
        text.append("body() == null: ").append(fromErrorBody).append('\n');
        text.append("read from: ").append(fromErrorBody ? "errorBody()" : "body()").append("\n\n");
        text.append("data: ").append(answer == null ? "(no body)" : answer.data).append("\n\n");
        text.append("errors: ");
        if (answer == null || answer.errors == null || answer.errors.isEmpty()) {
            text.append("none");
        } else {
            for (GraphQlError error : answer.errors) text.append("\n  ").append(error.message);
        }
        Log.d(TAG, text.toString());
        output.setText(text);
    }
}
