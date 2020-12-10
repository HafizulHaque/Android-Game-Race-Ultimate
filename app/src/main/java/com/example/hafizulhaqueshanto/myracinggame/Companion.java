package com.example.hafizulhaqueshanto.myracinggame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageButton;

import java.util.Random;

/**
 * Created by Hafizul Haque Shanto on 6/13/2017.
 */

public class Companion {
    private float screenX, screenY;
    private float x;
    private float y;
    private static int maxX, maxY, minX, minY;
    private final int minSpeed = -5, maxSpeed = -12;
    private float speed;
    private Bitmap bitmap;
    private RectF companionRect;
    private static Bitmap companionBitmap[];
    private int routeNo;
    private static float route[];
    private Random rand;

    //constructor
    Companion(Context context, int screenX, int screenY){
        this.screenX = screenX;
        this.screenY = screenY;
        rand = new Random();
        companionBitmap = new Bitmap[7];
        companionBitmap[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.companion_1);
        companionBitmap[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.companion_2);
        companionBitmap[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.companion_3);
        companionBitmap[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.companion_4);
        companionBitmap[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.companion_5);
        companionBitmap[5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.companion_6);
        companionBitmap[6] = BitmapFactory.decodeResource(context.getResources(), R.drawable.companion_7);

        bitmap = companionBitmap[rand.nextInt(7)];

        maxX = screenX/2;
        maxY = screenY;
        minX = 60;
        minY = 0;

        route = new float[3];
        route[0] = minX+20;
        route[1] = ((minX+maxX)-bitmap.getWidth())/2;
        route[2] = maxX-(bitmap.getWidth()+20);

        speed = minSpeed-rand.nextInt(minSpeed-maxSpeed);
        routeNo = rand.nextInt(3);

        x = route[routeNo];
        y = minY-(2+rand.nextInt(2))*bitmap.getHeight();

        companionRect = new RectF(x+8, y+15, (x+bitmap.getWidth())-8, (y+bitmap.getHeight())-10);
    }

    public void update(float playerSpeed){
        y += (speed+playerSpeed);
        if(y > screenY){
            bitmap = companionBitmap[rand.nextInt(7)];
            routeNo = rand.nextInt(3);
            speed = minSpeed-rand.nextInt(minSpeed-maxSpeed);
            x = route[routeNo];
            y = minY-(rand.nextInt(3)*maxY+bitmap.getHeight());
        }
        companionRect = new RectF(x+8, y+15, (x+bitmap.getWidth())-8, (y+bitmap.getHeight())-10);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public float getSpeed() {
        return speed;
    }

    public RectF getCompanionRect() {
        return companionRect;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public static void setMaxX(int maxX) {
        Companion.maxX = maxX;
    }

    public static void setMaxY(int maxY) {
        Companion.maxY = maxY;
    }

    public static void setMinX(int minX) {
        Companion.minX = minX;
    }

    public static void setMinY(int minY) {
        Companion.minY = minY;
    }

    public void setCompanionRect(RectF companionRect) {
        this.companionRect = companionRect;
    }
}
