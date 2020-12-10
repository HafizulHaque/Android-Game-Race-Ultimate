package com.example.hafizulhaqueshanto.myracinggame;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class GameTypeActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int ONE_WAY_MODE = 1;
    public static final int TWO_WAY_MODE = 2;
    public static final int LIMITED_MODE = 1;
    public static final int ENDLESS_MODE = 2;
    public static final int DAY_MODE = 1;
    public static final int NIGHT_MODE = 2;

    public static final String STREET_MODE = "STREET_MODE"; // 1 - one way   ,  2 - two way
    public static final String TIME_MODE = "TIME_MODE";     // 1 - limited ,  2 - endless
    public static final String ENV_MODE = "ENV_MODE";       // 1 - day   ,  2 - night

    final String BREAK_KEY = "BREAK_KEY";
    final String SPEED_KEY = "SPEED_KEY";
    final String ACC_KEY = "ACC_KEY";

    private int speedValue, accValue, breakValue;
    private int streetChecked;
    private int timeChecked;
    private int envChecked;
    private ImageButton speedButton, accButton, breakButton;
    private TextView speedTextView, accTextView, breakTextView;

    SharedPreferences prefs;


    RadioGroup streetModeButtonGroup, timeModeButtonGroup, envModeButtonGroup;
    RadioButton oneWayButton, twoWayButton, timeLimitButton, endlissButton, dayModeButton, nightModeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_type);

        prefs = getSharedPreferences(StartUpActivity.GAME_SHAR_PREF, MODE_PRIVATE);

        speedButton = (ImageButton)findViewById(R.id.speed_upgrade_button);
        accButton = (ImageButton) findViewById(R.id.accleration_upgrade_button);
        breakButton = (ImageButton) findViewById(R.id.break_upgrade_button);

        speedValue = prefs.getInt(SPEED_KEY, 50);
        accValue = prefs.getInt(ACC_KEY, 50);
        breakValue = prefs.getInt(BREAK_KEY, 50);

        speedTextView = ((TextView)findViewById(R.id.speed_text));
        speedTextView.setText(speedValue+"%");
        accTextView=((TextView)findViewById(R.id.accleration_text));
        accTextView.setText(accValue+"%");
        breakTextView=((TextView)findViewById(R.id.break_text));
        breakTextView.setText(breakValue+"%");

        speedButton.setOnClickListener(this);
        accButton.setOnClickListener(this);
        breakButton.setOnClickListener(this);

        streetChecked = prefs.getInt(STREET_MODE, ONE_WAY_MODE);
        timeChecked = prefs.getInt(TIME_MODE, ENDLESS_MODE);
        envChecked = prefs.getInt(ENV_MODE, DAY_MODE);
        speedValue = prefs.getInt(SPEED_KEY, 50);
        accValue = prefs.getInt(ACC_KEY, 50);
        breakValue = prefs.getInt(BREAK_KEY, 50);

        streetModeButtonGroup = (RadioGroup)findViewById(R.id.activity_game_type_street_type_radio_group);
        timeModeButtonGroup = (RadioGroup) findViewById(R.id.activity_game_type_game_mode_radio_group);
        envModeButtonGroup = (RadioGroup) findViewById(R.id.activity_game_type_env_mode_radio_group);

        oneWayButton = (RadioButton)findViewById(R.id.one_Way_type);
        twoWayButton = (RadioButton)findViewById(R.id.two_way_type);
        timeLimitButton = (RadioButton)findViewById(R.id.limited_time);
        endlissButton = (RadioButton)findViewById(R.id.endless);
        dayModeButton = (RadioButton) findViewById(R.id.day_mode);
        nightModeButton = (RadioButton) findViewById(R.id.night_mode);

        if(streetChecked == ONE_WAY_MODE){
            oneWayButton.setChecked(true);
            twoWayButton.setChecked(false);
        }
        else{
            twoWayButton.setChecked(true);
            oneWayButton.setChecked(false);
        }

        if(timeChecked == LIMITED_MODE){
            timeLimitButton.setChecked(true);
            endlissButton.setChecked(false);
        }
        else {
            endlissButton.setChecked(true);
            timeLimitButton.setChecked(false);
        }
        if(envChecked==DAY_MODE){
            dayModeButton.setChecked(true);
            nightModeButton.setChecked(false);
        }
        else {
            nightModeButton.setChecked(true);
            dayModeButton.setChecked(false);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        int selectedWay = streetModeButtonGroup.getCheckedRadioButtonId();
        int selectedTime = timeModeButtonGroup.getCheckedRadioButtonId();
        int selectedEnv = envModeButtonGroup.getCheckedRadioButtonId();
        int oneWay = oneWayButton.getId();
        int twoWay = twoWayButton.getId();
        int limited = timeLimitButton.getId();
        int endless = endlissButton.getId();
        int day = dayModeButton.getId();
        int night = nightModeButton.getId();

        SharedPreferences.Editor editor = prefs.edit();

        if(selectedTime== endless){
            editor.putInt(TIME_MODE, ENDLESS_MODE);
        }
        else{
            editor.putInt(TIME_MODE, LIMITED_MODE);
        }
        if(selectedWay== oneWay){
            editor.putInt(STREET_MODE,ONE_WAY_MODE );
        }
        else{
            editor.putInt(STREET_MODE, TWO_WAY_MODE);
        }
        if(selectedEnv==day){
            editor.putInt(ENV_MODE, DAY_MODE);
        }
        else{
            editor.putInt(ENV_MODE, NIGHT_MODE);
        }
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.speed_upgrade_button){
            if(speedValue<100){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final int currencyNeeded = (speedValue-40)*400;
                builder.setMessage("Upgrade requires currency "+currencyNeeded+"$. Want to proceed?")
                        .setCancelable(false)
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int currency = prefs.getInt(StartUpActivity.COIN_COUNT_KEY, 0);
                                if(currency>currencyNeeded){
                                    speedValue += 10;
                                    speedTextView.setText(speedValue+"%");
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putInt(SPEED_KEY, speedValue);
                                    editor.putInt(Player.DEFAULT_SPEED_KEY, prefs.getInt(Player.DEFAULT_SPEED_KEY, 10)+3);
                                    editor.putInt(Player.MAX_SPEED_KEY, prefs.getInt(Player.MAX_SPEED_KEY, 40)+6);
                                    editor.putInt(StartUpActivity.COIN_COUNT_KEY, currency-currencyNeeded);
                                    editor.apply();
                                }
                                else{
                                    Toast.makeText(GameTypeActivity.this, "You don't have enough currency needed for upgrade.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog updateConfirm = builder.create();
                updateConfirm.show();
            }
            else{
                Toast.makeText(this, "Vehicle speed is currenly at best mode.", Toast.LENGTH_SHORT).show();
            }
        }
        if(view.getId()==R.id.accleration_upgrade_button){
            if(accValue<100){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final int currencyNeeded = (accValue-40)*400;
                builder.setMessage("Upgrade requies currency "+currencyNeeded+"$. Want to proceed?")
                        .setCancelable(false)
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int currency = prefs.getInt(StartUpActivity.COIN_COUNT_KEY, 0);
                                if(currency>=currencyNeeded){
                                    accValue += 10;
                                    accTextView.setText(accValue+"%");
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putInt(ACC_KEY, accValue);
                                    editor.putFloat(Player.ACCLERATION_KEY, prefs.getFloat(Player.ACCLERATION_KEY, Player.INITIAL_ACCLELARATION_RATE)+0.06f );
                                    editor.putFloat(Player.DEFAULT_ACC_KEY, prefs.getFloat(Player.DEFAULT_ACC_KEY, Player.DEFAULT_ACCLERATION_RATE)+.06f);
                                    editor.putInt(StartUpActivity.COIN_COUNT_KEY, currency-currencyNeeded);
                                    editor.apply();
                                }
                                else{
                                    Toast.makeText(GameTypeActivity.this, "You don't have enough currency needed for upgrade", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog updateConfirm = builder.create();
                updateConfirm.show();
            }
            else{
                Toast.makeText(this, "Vehicle accleration is currently at best mode.", Toast.LENGTH_SHORT).show();
            }
        }
        if(view.getId()==R.id.break_upgrade_button){
            if(breakValue<100){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final int currencyNeeded = (breakValue-40)*400;
                builder.setMessage("Upgrade requires currency "+currencyNeeded+"$. Want to proceed?")
                        .setCancelable(false)
                        .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int currency = prefs.getInt(StartUpActivity.COIN_COUNT_KEY, 0);
                                if(currency>currencyNeeded){
                                    breakValue += 10;
                                    breakTextView.setText(breakValue+"%");
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putInt(BREAK_KEY, breakValue);
                                    editor.putFloat(Player.HANDLE_KEY, prefs.getFloat(Player.HANDLE_KEY, Player.INITIAL_HANDLE_RATE)+.1f );
                                    editor.putInt(StartUpActivity.COIN_COUNT_KEY, currency-currencyNeeded);
                                    editor.apply();
                                }
                                else{
                                    Toast.makeText(GameTypeActivity.this, "You don't have enough coin neeeded for upgrade.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog updateConfirm = builder.create();
                updateConfirm.show();
            }
            else{
                Toast.makeText(this, "Vehicle handling is currently at best mode.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
