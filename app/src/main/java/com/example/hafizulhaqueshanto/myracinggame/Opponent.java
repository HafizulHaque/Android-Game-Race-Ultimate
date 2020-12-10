package com.example.hafizulhaqueshanto.myracinggame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by Hafizul Haque Shanto on 6/12/2017.
 */

public class Opponent {
    int screenX, screenY;
    static Bitmap opponentArray[];
    private Bitmap bitmap;
    private float x, y;
    private RectF opponentRect;
    private float maxX, minX, maxY, minY;
    private final int minSpeed = 5, maxSpeed = 8;
    private float speed;
    Random rand;
    private static float route[];
    private int routeNo;


    public Opponent(Context context, int screenX, int screenY){
        this.screenX = screenX;
        this.screenY = screenY;
        rand = new Random();
        opponentArray = new Bitmap[8];
        opponentArray[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.opponent_1);
        opponentArray[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.opponent_2);
        opponentArray[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.opponent_3);
        opponentArray[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.opponent_4);
        opponentArray[4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.opponent_5);
        opponentArray[5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.opponent_6);
        opponentArray[6] = BitmapFactory.decodeResource(context.getResources(), R.drawable.opponent_7);
        opponentArray[7] = BitmapFactory.decodeResource(context.getResources(), R.drawable.opponent_8);

        minX = screenX/2;
        maxX = screenX-60;
        minY = 0;
        maxY = screenY;
        speed = minSpeed + rand.nextInt(maxSpeed-minSpeed);

        bitmap = opponentArray[rand.nextInt(8)];

        route = new float[3];
        route[0] = minX + 20;
        route[1] = ((minX+maxX)-bitmap.getWidth())/2;
        route[2] = maxX-(bitmap.getWidth()+20);

        routeNo = rand.nextInt(3);

        x =  route[routeNo];
        y = minY-(2+rand.nextInt(2))*bitmap.getHeight();

        opponentRect = new RectF(x+8, y+10, (x+bitmap.getWidth())-8, (y+bitmap.getHeight())-15);
    }


    public void update(float playerSpeed){

        y += (speed+playerSpeed);

        if(y > maxY){
            bitmap = opponentArray[rand.nextInt(8)];
            routeNo = rand.nextInt(3);
            speed = minSpeed+rand.nextInt(maxSpeed-minSpeed);
            x =  route[routeNo];
            y = minY - (rand.nextInt(3)*maxY + bitmap.getHeight());
        }

        opponentRect = new RectF(x+8, y+10, (x+bitmap.getWidth())-8, (y+bitmap.getHeight())-15);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public RectF getOpponentRect() {
        return opponentRect;
    }

    public float getSpeed() {
        return speed;
    }
}
