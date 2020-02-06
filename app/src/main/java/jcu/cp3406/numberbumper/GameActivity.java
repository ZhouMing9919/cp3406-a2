package jcu.cp3406.numberbumper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends DefaultActivity {

    private Handler handler;

    ProgressBar timeBar;
    TableLayout grid;
    TextView statusText;
    TextView answerText;
    TextView formulaText;

    Exercise exercise;

    boolean active = false;

    AlertDialog.Builder startDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        timeBar = findViewById(R.id.progress);

        grid = findViewById(R.id.grid);

        statusText = findViewById(R.id.status);
        answerText = findViewById(R.id.answer);
        formulaText = findViewById(R.id.formula);

        Intent intent = getIntent();
        int exerciseDifficulty = intent.getIntExtra(MainActivity.KEY_DIFFICULTY, Exercise.DIFFICULTY_NORMAL);
        int exerciseLevel = intent.getIntExtra(MainActivity.KEY_LEVEL, Exercise.LEVEL_ADDITION);
        exercise = new Exercise(exerciseLevel, exerciseDifficulty);

        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                if (active) {
                    onTimeLapse();
                }
            }
        });

        startDialog = new AlertDialog.Builder(this);
        startDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                onBeginExercise();
            }
        });
        startDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onBeginExercise();
            }
        });
        startDialog.setIcon(R.drawable.bump);
        startDialog.setTitle(Exercise.getLevelWord(exercise.level, this));
        startDialog.setMessage(String.format(locale, getString(R.string.game_start_message), Exercise.getLevelWord(exercise.level, this).toLowerCase(locale), Exercise.getLevelSymbol(exercise.level)));
        startDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.retry_button:
                onRetry();
                return true;
            case android.R.id.home:
                doFinishExercise();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onBeginExercise() {
        doNextQuestion();
        active = true;
    }

    private void doGridSetup(@NonNull Question question) {
        grid.removeAllViews();
        for (int y = 0; y < question.getSize(); ++y) {
            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams tableRowLayoutParams = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(tableRowLayoutParams);
            for (int x = 0; x < question.getSize(); ++x) {
                int buttonStyle = R.style.EntryButton;
                Button button = new Button(new ContextThemeWrapper(this, buttonStyle), null, buttonStyle);
                tableRow.addView(button, x);
            }
            grid.addView(tableRow);
        }
    }

    private void doLoadQuestion(@NonNull Question question) {
        doGridSetup(question);
        for (int y = 0; y < question.getSize(); ++y) {
            View tableRowBuffer = grid.getChildAt(y);
            if (tableRowBuffer instanceof TableRow) {
                TableRow tableRow = (TableRow) tableRowBuffer;
                for (int x = 0; x < question.getSize(); ++x) {
                    View buttonBuffer = tableRow.getChildAt(x);
                    if (buttonBuffer instanceof Button) {
                        final Button button = (Button) buttonBuffer;
                        final int choice = question.getChoice(question.getSize() * y + x);
                        button.setText(String.format(locale, "%d", choice));
                        button.setEnabled(true);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onQuestionEntry(choice);
                                button.setEnabled(false);
                                button.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }
            }
        }
        statusText.setText(String.format(locale, getString(R.string.game_questions_completed), Exercise.getLevelWord(exercise.level, this), exercise.getQuestionId() + 1, exercise.countAllQuestions()));
        answerText.setText(String.format(locale, "%d", question.getAnswer()));
        doRotateAnswer();
        onUpdateFormula(question);
        onUpdateTimeBar(question);
        doVibrate();
    }

    private void doNextQuestion() {
        Question question = exercise.getNextQuestion();
        if (question != null) {
            doLoadQuestion(question);
        }
    }

    private void doRetryQuestion() {
        Question question = exercise.getRetryQuestion();
        if (question != null) {
            doLoadQuestion(question);
        }
    }

    private void onRetry() {
        if (!active) {
            return;
        }
        String message;
        if (exercise.decrementTries()) {
            doRetryQuestion();
            message = String.format(locale, getString(R.string.game_retries_left), exercise.getTries(), Exercise.MAX_TRIES);

        } else {
            message = getString(R.string.game_retries_non);
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void onTimeLapse() {
        if (exercise.isFinished()) {
            active = false;
            doFinishExercise();
        } else {
            Question question = exercise.getCurrentQuestion();
            if (question != null) {
                question.setTimeLapse(question.getTimeLapse() + 1);
                if (question.getTimeLapse() >= question.getTimeLimit()) {
                    doNextQuestion();
                }
                onUpdateTimeBar(question);
            }
        }
    }

    private void onUpdateTimeBar(@Nullable Question question) {
        int progress = 0;
        if (question != null) {
            progress = timeBar.getMax() / question.getTimeLimit() * question.getTimeLapse();
        }
        timeBar.setProgress(progress);
    }

    private void onQuestionEntry(int operand) {
        Question question = exercise.getCurrentQuestion();
        if (question != null) {
            question.addEntry(operand);
            onUpdateFormula(question);
            if (question.status() != Question.Status.PENDING) {
                doNextQuestion();
            }
        }
    }

    private static final String FORMULA_TEXT_BLANK = "...";

    private void onUpdateFormula(@Nullable Question question) {
        if (question == null) {
            formulaText.setText(FORMULA_TEXT_BLANK);
        } else {
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < question.countEntries(); ++i) {
                int operand = question.getEntry(i);
                text.append(operand).append(" ").append(Question.getLevelSymbol(exercise.level)).append(" ");
            }
            text.append(FORMULA_TEXT_BLANK);
            formulaText.setText(text);
        }
    }

    @Override
    void onShake() {
        onRetry();
    }

    private void doRotateAnswer() {
        RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        answerText.startAnimation(animation);
    }

    static final String KEY_COUNT_ALL_QUESTIONS = "count_all_quests";
    static final String KEY_COUNT_CORRECT_QUESTIONS = "count_correct_quests";

    private void doFinishExercise() {
        Intent intent = getIntent();
        intent.putExtra(MainActivity.KEY_PERCENTAGE, 100 / exercise.countAllQuestions() * exercise.countCorrectQuestions());
        intent.putExtra(KEY_COUNT_ALL_QUESTIONS, exercise.countAllQuestions());
        intent.putExtra(KEY_COUNT_CORRECT_QUESTIONS, exercise.countCorrectQuestions());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        doFinishExercise();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        active = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        active = false;
    }
}
