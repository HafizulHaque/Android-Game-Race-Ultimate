package com.example.hafizulhaqueshanto.myracinggame;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HighScoreActivity extends AppCompatActivity {

    public static final String[] TYPE_CODE = {"ONE_WAY_ENDLESS", "TWO_WAY_ENDLESS", "ONE_WAY_TIME_LIMIT", "TWO_WAY_TIME_LIMIT"};
    private String selectedMode; //               0           1               2                     3

    SharedPreferences prefs;
    int streetType, timeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        prefs = getSharedPreferences(StartUpActivity.GAME_SHAR_PREF, MODE_PRIVATE);

        streetType = prefs.getInt(GameTypeActivity.STREET_MODE, GameTypeActivity.ONE_WAY_MODE);
        timeType = prefs.getInt(GameTypeActivity.TIME_MODE, GameTypeActivity.ENDLESS_MODE);

        selectedMode = (streetType==GameTypeActivity.ONE_WAY_MODE)? "ONE_WAY" : "TWO_WAY" ;
        selectedMode += (timeType==GameTypeActivity.ENDLESS_MODE)? "_ENDLESS" : "_TIME_LIMIT";

        ((TextView)findViewById(R.id.first_score)).setText(Integer.toString(prefs.getInt(selectedMode+"_0", 0)));
        ((TextView)findViewById(R.id.second_score)).setText(Integer.toString(prefs.getInt(selectedMode+"_1", 0)));
        ((TextView)findViewById(R.id.third_score)).setText(Integer.toString(prefs.getInt(selectedMode+"_2", 0)));
        ((TextView)findViewById(R.id.fourth_score)).setText(Integer.toString(prefs.getInt(selectedMode+"_3", 0)));
        ((TextView)findViewById(R.id.fifth_score)).setText(Integer.toString(prefs.getInt(selectedMode+"_4", 0)));

    }
}
