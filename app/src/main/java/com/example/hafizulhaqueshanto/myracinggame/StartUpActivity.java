package com.example.hafizulhaqueshanto.myracinggame;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartUpActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String GAME_SHAR_PREF = "GAME_SHAR_PREF";

    public static final String STREET_TYPE_KEY = "STREET_TYPE_KEY";
    public static final String TIME_TYPE_KEY = "TIME_TYPE_KEY";
    public static final String COIN_COUNT_KEY = "COIN_COUNT_KEY";

    private int coin;

    SharedPreferences prefs;
    long start;
    int stop;

    Button newGameButton;
    Button highScoreButton;
    Button gameTypeButton;
    Button instructionButton;
    TextView coinTextView;

    public static MediaPlayer backgroundMusic;
    private HardwareKeyWatcher mHardwareKeyWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        prefs = getSharedPreferences(GAME_SHAR_PREF, MODE_PRIVATE);
        coin = prefs.getInt(COIN_COUNT_KEY, 0);


        newGameButton = (Button) findViewById(R.id.activity_start_up_new_game);
        highScoreButton = (Button) findViewById(R.id.start_up_high_score_button);
        gameTypeButton = (Button) findViewById(R.id.start_up_game_type_button);
        instructionButton = (Button)findViewById(R.id.start_up_game_instruction);
        coinTextView = (TextView) findViewById(R.id.activity_start_up_coin_text);

        newGameButton.setOnClickListener(this);
        highScoreButton.setOnClickListener(this);
        gameTypeButton.setOnClickListener(this);
        instructionButton.setOnClickListener(this);

        coinTextView.setText("COIN: " + coin);

        backgroundMusic = MediaPlayer.create(this, R.raw.playnormal);
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(1, 1);
        backgroundMusic.start();

        mHardwareKeyWatcher = new HardwareKeyWatcher(this);
        mHardwareKeyWatcher.setOnHardwareKeysPressedListenerListener(new HardwareKeyWatcher.OnHardwareKeysPressedListener() {
            @Override
            public void onHomePressed() {
                backgroundMusic.pause();
            }

            @Override
            public void onRecentAppsPressed() {
                backgroundMusic.pause();
            }
        });
        mHardwareKeyWatcher.startWatch();

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.activity_start_up_new_game){
            Intent intent = new Intent(StartUpActivity.this, GameActivity.class);
            startActivity(intent);
        }
        if(view.getId()==R.id.start_up_game_type_button){
            startActivity(new Intent(StartUpActivity.this, GameTypeActivity.class));
        }
        if(view.getId()==R.id.start_up_high_score_button){
            startActivity(new Intent(StartUpActivity.this, HighScoreActivity.class));
        }
        if(view.getId()==R.id.start_up_game_instruction){
            startActivity(new Intent(StartUpActivity.this, InstructionActivity.class));
        }
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(!backgroundMusic.isPlaying()){
            backgroundMusic.start();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(!backgroundMusic.isPlaying()){
            backgroundMusic.start();
        }
        int changedCoin = prefs.getInt(COIN_COUNT_KEY, 0);
        coinTextView.setText("COIN: " + changedCoin);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        backgroundMusic.reset();
                        backgroundMusic.release();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
