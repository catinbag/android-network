package org.demo.network.retrofitbasic;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/** List of the three screens of the module. */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.open_breeds).setOnClickListener(
                v -> startActivity(new Intent(this, BreedsActivity.class)));
        findViewById(R.id.open_posts).setOnClickListener(
                v -> startActivity(new Intent(this, PostsActivity.class)));
        findViewById(R.id.open_mistakes).setOnClickListener(
                v -> startActivity(new Intent(this, MistakesActivity.class)));
    }
}
