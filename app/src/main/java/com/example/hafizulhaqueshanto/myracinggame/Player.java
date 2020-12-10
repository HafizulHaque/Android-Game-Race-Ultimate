package com.example.hafizulhaqueshanto.myracinggame;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by Hafizul Haque Shanto on 6/12/2017.
 */

public class Player {
    public boolean speedUpFlag, speedDownFlag;

    public static final String DEFAULT_SPEED_KEY = "DEFAULT_SPEED_KEY";
    public static final String MAX_SPEED_KEY = "MAX_SPEED_KEY";
    public static final String ACCLERATION_KEY = "ACCLERATION_KEY";
    public static final String DEFAULT_ACC_KEY = "DEF_ACC_KEY";
    public static final String DEFAULT_HANDLE_KEY = "DEFAULT_HANDLE_KEY";
    public static final String HANDLE_KEY = "HANDLE_KEY";
    public static final int INITIAL_SPEED = 40;
    public static final int INITIAL_DEFAULT_SPEED = 10;
    public static final float INITIAL_ACCLELARATION_RATE = 0.3f;
    public static final float DEFAULT_ACCLERATION_RATE = 0.1f;
    public static final float INITIAL_HANDLE_RATE = 0.5f;

    private int screenX, screenY;
    private Bitmap bitmap;
    private RectF playerRect;
    private float x;
    private float y;
    static private float minX, minY, maxX, maxY;
    private float speed;
    private int maxSpeed;
    private final int minSpeed = 0;
    private int defalultSpeed;
    private float accleratioRate;
    private float defaultAcclerationRate;
    private float handleRate;
    private float defaultHandleRate;
    Random rand;
    private int safeClashCount;
    SharedPreferences prefs;

    public Player(Context context, int screenX, int screenY){
        this.screenX = screenX;
        this.screenY = screenY;
        rand = new Random();
        prefs = context.getSharedPreferences(StartUpActivity.GAME_SHAR_PREF, Context.MODE_PRIVATE);

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.player);

        maxSpeed = prefs.getInt(MAX_SPEED_KEY, INITIAL_SPEED);
        defalultSpeed = prefs.getInt(DEFAULT_SPEED_KEY, INITIAL_DEFAULT_SPEED);
        accleratioRate = prefs.getFloat(ACCLERATION_KEY, INITIAL_ACCLELARATION_RATE);
        defaultAcclerationRate = prefs.getFloat(DEFAULT_ACC_KEY, DEFAULT_ACCLERATION_RATE);
        handleRate = prefs.getFloat(HANDLE_KEY, INITIAL_HANDLE_RATE);
        defaultHandleRate = prefs.getFloat(DEFAULT_HANDLE_KEY, .05f);

        minX = 60;
        maxX = screenX-60;
        minY = 0;

        maxY = screenY;

        x = maxX/2-(bitmap.getWidth()+100);
        y = maxY-(bitmap.getHeight()+150);
        speed = 10;

        playerRect = new RectF((int)x+8, (int)y+15, (int) (x+bitmap.getWidth())-8,(int) (y+bitmap.getHeight())-10);
        speedUpFlag = false;
        speedDownFlag = false;

        safeClashCount = 0;
    }

    public void update(){
        if(x < minX)
            x = minX;
        if(x > maxX-bitmap.getWidth())
            x = maxX-bitmap.getWidth();

        playerRect = new RectF(x+8, y+15, (x+bitmap.getWidth())-8, (y+bitmap.getHeight())-10);

        if(speed>defalultSpeed){
            speed -= defaultHandleRate;
        }
        if(speed<defalultSpeed){
            speed += defaultAcclerationRate;
        }

        if(speedUpFlag)
            speed += accleratioRate;
        if(speedDownFlag)
            speed -= handleRate;

        if(speed>maxSpeed)
            speed = maxSpeed;
        if(speed<minSpeed)
            speed = minSpeed;
    }

    public void changePlayerPos(float gX) {
        x += (gX*speed)/maxSpeed;
    }

    public boolean isSpeedUpFlag() {
        return speedUpFlag;
    }

    public void setSpeedUpFlag(boolean speedUpFlag) {
        this.speedUpFlag = speedUpFlag;
    }

    public boolean isSpeedDownFlag() {
        return speedDownFlag;
    }

    public void setSpeedDownFlag(boolean speedDownFlag) {
        this.speedDownFlag = speedDownFlag;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public RectF getPlayerRect() {
        return playerRect;
    }

    public void setPlayerRect(RectF playerRect) {
        this.playerRect = playerRect;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public static float getMinX() {
        return minX;
    }

    public static void setMinX(float minX) {
        Player.minX = minX;
    }

    public static float getMinY() {
        return minY;
    }

    public static void setMinY(float minY) {
        Player.minY = minY;
    }

    public static float getMaxX() {
        return maxX;
    }

    public static void setMaxX(float maxX) {
        Player.maxX = maxX;
    }

    public static float getMaxY() {
        return maxY;
    }

    public static void setMaxY(float maxY) {
        Player.maxY = maxY;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public int getMinSpeed() {
        return minSpeed;
    }

    public int getDefalultSpeed() {
        return defalultSpeed;
    }

    public int getSafeClashCount() {
        return safeClashCount;
    }

    public void setSafeClashCount(int safeClashCount) {
        this.safeClashCount = safeClashCount;
    }
}
