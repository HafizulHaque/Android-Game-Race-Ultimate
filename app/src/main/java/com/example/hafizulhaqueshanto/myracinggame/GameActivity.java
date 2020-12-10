package com.example.hafizulhaqueshanto.myracinggame;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Layout;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.hafizulhaqueshanto.myracinggame.R;


import java.nio.charset.MalformedInputException;

import static android.view.MotionEvent.ACTION_BUTTON_PRESS;


public class GameActivity extends AppCompatActivity{

    private CustomView gameView;

    // sensor variables
    SensorManager sensorManager;
    SensorEventListener sensorEventListener;
    Sensor acclerometerSensor;

    private int streetMode, timeMode, envMode;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initSensors();
        StartUpActivity.backgroundMusic.pause();

        prefs = getSharedPreferences(StartUpActivity.GAME_SHAR_PREF, MODE_PRIVATE);

        streetMode = prefs.getInt(GameTypeActivity.STREET_MODE, GameTypeActivity.ONE_WAY_MODE);
        timeMode = prefs.getInt(GameTypeActivity.TIME_MODE, GameTypeActivity.ENDLESS_MODE);
        envMode = prefs.getInt(GameTypeActivity.ENV_MODE, GameTypeActivity.DAY_MODE);

        Point size = new Point();
        this.getWindowManager().getDefaultDisplay().getRealSize(size);
        gameView = new CustomView(this, size.x, size.y, streetMode, timeMode, envMode);

        setContentView(gameView);

    }

    private void initSensors() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                    float gX =  -(sensorEvent.values[0]);
                    gameView.player.changePlayerPos(gX);
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        acclerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        startUsingSensor();
    }

    private void startUsingSensor() {
        sensorManager.registerListener(sensorEventListener, acclerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
    private void stopUsingSensor(){
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
        stopUsingSensor();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        gameView.resume();
        startUsingSensor();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopUsingSensor();
    }

    @Override
    protected void onStart() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StartUpActivity.backgroundMusic.start();
    }
}
