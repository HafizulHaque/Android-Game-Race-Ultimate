package com.example.hafizulhaqueshanto.myracinggame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

/**
 * Created by Hafizul Haque Shanto on 6/24/2017.
 */

public class Blast {
    private int x;
    private int y;
    private Bitmap bitmap;
    private RectF blastRect;

    Blast(Context context, int screenX, int screenY){

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.boom);
        x = -(bitmap.getWidth()+10);
        y = -(bitmap.getHeight()+10);
        blastRect = new RectF(x, y, x+bitmap.getWidth(), y+bitmap.getHeight());
    }

    public void update(){
        x = -(bitmap.getWidth()+10);
        y = -(bitmap.getHeight()+10);
        blastRect.set(x, y, x+bitmap.getWidth(), y+bitmap.getHeight());
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public RectF getBlastRect() {
        return blastRect;
    }

    public void setBlastRect(RectF blastRect) {
        this.blastRect = blastRect;
    }
}
