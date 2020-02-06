package jcu.cp3406.numberbumper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends DefaultActivity {

    RadioGroup difficultyInput;

    int difficulty = Exercise.DIFFICULTY_NORMAL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        difficultyInput = findViewById(R.id.difficulty_input);
        difficultyInput.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onChangeDifficultyInput(checkedId);
            }
        });

        Intent intent = getIntent();
        difficulty = intent.getIntExtra(MainActivity.KEY_DIFFICULTY, Exercise.DIFFICULTY_NORMAL);
        onUpdateDifficultyInput();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onUpdateDifficultyResult();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onChangeDifficultyInput(int checkedId) {
        switch (checkedId) {
            case R.id.difficulty_input_easiest:
                difficulty = Exercise.DIFFICULTY_EASIEST;
                break;
            case R.id.difficulty_input_normal:
                difficulty = Exercise.DIFFICULTY_NORMAL;
                break;
            case R.id.difficulty_input_hardest:
                difficulty = Exercise.DIFFICULTY_HARDEST;
                break;
        }
        onUpdateDifficultyInput();
    }

    private void onUpdateDifficultyInput() {
        switch (difficulty) {
            case Exercise.DIFFICULTY_EASIEST:
                difficultyInput.check(R.id.difficulty_input_easiest);
                break;
            case Exercise.DIFFICULTY_NORMAL:
                difficultyInput.check(R.id.difficulty_input_normal);
                break;
            case Exercise.DIFFICULTY_HARDEST:
                difficultyInput.check(R.id.difficulty_input_hardest);
                break;
        }
        onUpdateDifficultyAppearance();
        onUpdateDifficultyResult();
    }

    private void onUpdateDifficultyAppearance() {
        for (int i = 0; i < difficultyInput.getChildCount(); ++i) {
            Object buffer = difficultyInput.getChildAt(i);
            if (buffer instanceof RadioButton) {
                RadioButton button = (RadioButton) buffer;
                int styleId;
                if (button.isChecked()) {
                    styleId = R.style.DifficultyButtonSelected;
                } else {
                    styleId = R.style.DifficultyButton;
                }
                button.setTextAppearance(getBaseContext(), styleId);
            }
        }
    }

    private void onUpdateDifficultyResult() {
        Intent intent = getIntent();
        intent.putExtra(MainActivity.KEY_DIFFICULTY, difficulty);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onBackPressed() {
        onUpdateDifficultyResult();
        super.onBackPressed();
    }

    @Override
    void onShake() {
        finish();
    }
}
