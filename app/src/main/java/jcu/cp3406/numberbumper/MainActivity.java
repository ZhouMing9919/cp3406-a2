package jcu.cp3406.numberbumper;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import twitter4j.TwitterException;

public class MainActivity extends DefaultActivity {

    DataWrapper dataWrapper;

    TwitterWrapper twitterWrapper;

    MediaPlayer groundMusic;

    Switch musicToggle;

    ImageButton
            playButton1,
            playButton2,
            playButton3,
            playButton4,
            playButton5;

    AlertDialog.Builder scoreDialog;

    static final String KEY_LEVEL = "level";
    static final String KEY_DIFFICULTY = "difficulty";
    static final String KEY_PERCENTAGE = "percentage";

    int difficulty = Exercise.DIFFICULTY_NORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        twitterWrapper = new TwitterWrapper(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret),
                getString(R.string.twitter_access_token),
                getString(R.string.twitter_access_secret));

        dataWrapper = new DataWrapper(getApplicationContext());

        playButton1 = findViewById(R.id.playButton1);
        playButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchGame(Exercise.LEVEL_ADDITION);
            }
        });
        playButton2 = findViewById(R.id.playButton2);
        playButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchGame(Exercise.LEVEL_SUBTRACTION);
            }
        });
        playButton3 = findViewById(R.id.playButton3);
        playButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchGame(Exercise.LEVEL_MULTIPLICATION);
            }
        });
        playButton4 = findViewById(R.id.playButton4);
        playButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchGame(Exercise.LEVEL_DIVISION);
            }
        });
        playButton5 = findViewById(R.id.playButton5);
        playButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchGame(Exercise.LEVEL_EXPONENTIATION);
            }
        });

        groundMusic = MediaPlayer.create(this, R.raw.myg_greenpeace);
        groundMusic.setAudioStreamType(AudioManager.STREAM_MUSIC);
        groundMusic.setLooping(true);
        groundMusic.start();

        scoreDialog = new AlertDialog.Builder(this);
        scoreDialog.setIcon(R.drawable.bump);
        scoreDialog.setTitle(getString(R.string.game_summary));
        scoreDialog.setPositiveButton(android.R.string.ok, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        onCreateMusicToggle(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_button:
                onLaunchSettings();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onCreateMusicToggle(@NotNull Menu menu) {
        musicToggle = menu.findItem(R.id.music_toggle_item).getActionView().findViewById(R.id.music_toggle);
        musicToggle.setChecked(groundMusic.isPlaying());
        musicToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !groundMusic.isPlaying()) {
                    groundMusic.start();
                } else {
                    groundMusic.pause();
                }
            }
        });
    }

    @Override
    void onShake() {
        onLaunchScores();
    }

    static final int REQUEST_GAME = 1;
    static final int REQUEST_SETTINGS = 2;

    private void onLaunchGame(int level) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(KEY_LEVEL, level);
        intent.putExtra(KEY_DIFFICULTY, difficulty);
        startActivityForResult(intent, REQUEST_GAME);
    }

    private void onFinishGame(@NotNull Intent intent) {
        int scoredCountAllQuests = intent.getIntExtra(GameActivity.KEY_COUNT_ALL_QUESTIONS, -1);
        int scoredCountCorrectQuests = intent.getIntExtra(GameActivity.KEY_COUNT_CORRECT_QUESTIONS, -1);
        if (scoredCountAllQuests > -1 && scoredCountCorrectQuests > -1) {
            scoreDialog.setMessage(String.format(locale, getString(R.string.game_questions_correct), scoredCountCorrectQuests, scoredCountAllQuests));
            scoreDialog.show();
        }
        int scoredLevel = intent.getIntExtra(KEY_LEVEL, Exercise.LEVEL_ADDITION);
        int scoredDifficulty = intent.getIntExtra(KEY_DIFFICULTY, Exercise.DIFFICULTY_NORMAL);
        int scoredPercentage = intent.getIntExtra(KEY_PERCENTAGE, 0);
        dataWrapper.insertScore(scoredLevel, scoredDifficulty, scoredPercentage);
        if (scoredPercentage >= 100) {
            onTweetScore(scoredLevel, scoredDifficulty);
        }
    }

    private void onLaunchSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(KEY_DIFFICULTY, difficulty);
        startActivityForResult(intent, REQUEST_SETTINGS);
    }

    private void onLaunchScores() {
        Intent intent = new Intent(this, ScoresActivity.class);
        startActivity(intent);
    }

    private void onFinishSettings(@NotNull Intent intent) {
        difficulty = intent.getIntExtra(KEY_DIFFICULTY, Exercise.DIFFICULTY_NORMAL);
    }

    private void onTweetScore(int scoredLevel, int scoredDifficulty) {
        String tweetMessage = String.format(
                locale,
                getString(R.string.score_tweet_message),
                getString(R.string.app_name),
                Exercise.getLevelWord(scoredLevel, getApplicationContext()),
                Exercise.getDifficultyWord(scoredDifficulty, getApplicationContext())
        );
        String toastMessage = getString(R.string.score_has_tweeted);
        try {
            twitterWrapper.tweet(tweetMessage);
        } catch (TwitterException exception) {
            toastMessage = getString(R.string.score_cannot_tweet);
        } catch (NetworkOnMainThreadException exception) {
            toastMessage = getString(R.string.score_cannot_tweet);
        }
        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_GAME:
                if (resultCode == RESULT_OK) {
                    onFinishGame(intent);
                }
                break;
            case REQUEST_SETTINGS:
                if (resultCode == RESULT_OK) {
                    onFinishSettings(intent);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        groundMusic.release();
    }
}
