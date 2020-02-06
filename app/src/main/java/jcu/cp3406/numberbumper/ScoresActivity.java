package jcu.cp3406.numberbumper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ScoresActivity extends DefaultActivity {

    DataWrapper dataWrapper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        dataWrapper = new DataWrapper(this);

        TextView textView;
        textView = findViewById(R.id.score_level_add);
        textView.setText(Exercise.getLevelSymbol(Exercise.LEVEL_ADDITION));
        textView = findViewById(R.id.score_level_sub);
        textView.setText(Exercise.getLevelSymbol(Exercise.LEVEL_SUBTRACTION));
        textView = findViewById(R.id.score_level_mul);
        textView.setText(Exercise.getLevelSymbol(Exercise.LEVEL_MULTIPLICATION));
        textView = findViewById(R.id.score_level_div);
        textView.setText(Exercise.getLevelSymbol(Exercise.LEVEL_DIVISION));
        textView = findViewById(R.id.score_level_exp);
        textView.setText(Exercise.getLevelSymbol(Exercise.LEVEL_EXPONENTIATION));

        onCompletionImageVerify(
                Exercise.LEVEL_ADDITION,
                Exercise.DIFFICULTY_EASIEST,
                R.id.completion_0_0);
        onCompletionImageVerify(
                Exercise.LEVEL_ADDITION,
                Exercise.DIFFICULTY_NORMAL,
                R.id.completion_0_1);
        onCompletionImageVerify(
                Exercise.LEVEL_ADDITION,
                Exercise.DIFFICULTY_HARDEST,
                R.id.completion_0_2);
        onCompletionImageVerify(
                Exercise.LEVEL_SUBTRACTION,
                Exercise.DIFFICULTY_EASIEST,
                R.id.completion_1_0);
        onCompletionImageVerify(
                Exercise.LEVEL_SUBTRACTION,
                Exercise.DIFFICULTY_NORMAL,
                R.id.completion_1_1);
        onCompletionImageVerify(
                Exercise.LEVEL_SUBTRACTION,
                Exercise.DIFFICULTY_HARDEST,
                R.id.completion_1_2);
        onCompletionImageVerify(
                Exercise.LEVEL_MULTIPLICATION,
                Exercise.DIFFICULTY_EASIEST,
                R.id.completion_2_0);
        onCompletionImageVerify(
                Exercise.LEVEL_MULTIPLICATION,
                Exercise.DIFFICULTY_NORMAL,
                R.id.completion_2_1);
        onCompletionImageVerify(
                Exercise.LEVEL_MULTIPLICATION,
                Exercise.DIFFICULTY_HARDEST,
                R.id.completion_2_2);
        onCompletionImageVerify(
                Exercise.LEVEL_DIVISION,
                Exercise.DIFFICULTY_EASIEST,
                R.id.completion_3_0);
        onCompletionImageVerify(
                Exercise.LEVEL_DIVISION,
                Exercise.DIFFICULTY_NORMAL,
                R.id.completion_3_1);
        onCompletionImageVerify(
                Exercise.LEVEL_DIVISION,
                Exercise.DIFFICULTY_HARDEST,
                R.id.completion_3_2);
        onCompletionImageVerify(
                Exercise.LEVEL_EXPONENTIATION,
                Exercise.DIFFICULTY_EASIEST,
                R.id.completion_4_0);
        onCompletionImageVerify(
                Exercise.LEVEL_EXPONENTIATION,
                Exercise.DIFFICULTY_NORMAL,
                R.id.completion_4_1);
        onCompletionImageVerify(
                Exercise.LEVEL_EXPONENTIATION,
                Exercise.DIFFICULTY_HARDEST,
                R.id.completion_4_2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scores, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.twitter_button:
                Uri uri = Uri.parse(getString(R.string.twitter_address));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    void onShake() {
        finish();
    }

    private void onCompletionImageVerify(int level, int difficulty, int resId) {
        if (dataWrapper.selectScore(level, difficulty).contains(100)) {
            ImageView image = findViewById(resId);
            image.setImageResource(R.drawable.complete);
        }
    }
}
