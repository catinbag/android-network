package org.demo.network.retrofitbasic;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import org.demo.network.retrofitbasic.mistakes.BaseUrlSlashMode;
import org.demo.network.retrofitbasic.mistakes.CleartextMode;
import org.demo.network.retrofitbasic.mistakes.ExecuteTwiceMode;
import org.demo.network.retrofitbasic.mistakes.LeadingSlashMode;
import org.demo.network.retrofitbasic.mistakes.MistakeMode;
import org.demo.network.retrofitbasic.mistakes.NoCallMode;
import org.demo.network.retrofitbasic.mistakes.NoConverterMode;
import org.demo.network.retrofitbasic.mistakes.NoIsSuccessfulMode;
import org.demo.network.retrofitbasic.mistakes.WrongDtoShapeMode;
import org.demo.network.retrofitbasic.mistakes.WrongFieldNameMode;

import java.util.Arrays;
import java.util.List;

/**
 * Stages 5 and 12: common mistakes of Retrofit code, one per Spinner item.
 * Run shows the mistake, "Fix it" + Run shows the right code. The result is in Logcat (tag MistakesDemo).
 */
public class MistakesActivity extends AppCompatActivity {
    private static final String TAG = "MistakesDemo";

    private TextView output;
    private SwitchCompat fixIt;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mistakes);
        output = findViewById(R.id.output);
        fixIt = findViewById(R.id.fix_it);
        spinner = findViewById(R.id.mode_spinner);

        List<MistakeMode> modes = Arrays.asList(
                new CleartextMode(),
                new BaseUrlSlashMode(),
                new LeadingSlashMode(),
                new NoCallMode(),
                new NoConverterMode(),
                new WrongDtoShapeMode(),
                new WrongFieldNameMode(),
                new NoIsSuccessfulMode(),
                new ExecuteTwiceMode());
        ArrayAdapter<MistakeMode> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                output.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        findViewById(R.id.run).setOnClickListener(v -> run((MistakeMode) spinner.getSelectedItem()));
    }

    private void run(MistakeMode mode) {
        output.setText("");
        MistakeMode.Reporter out = text -> {
            Log.d(TAG, mode + " (fix=" + fixIt.isChecked() + "): " + text);
            output.append(text + "\n\n");
        };
        try {
            mode.run(fixIt.isChecked(), out);
        } catch (RuntimeException e) {
            out.show("exception thrown by the call:\n" + MistakeMode.describe(e));
        }
    }
}
