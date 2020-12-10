package com.example.hafizulhaqueshanto.myracinggame;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static com.example.hafizulhaqueshanto.myracinggame.R.color.black;
import static com.example.hafizulhaqueshanto.myracinggame.R.color.nightRoadColor;

/**
 * Created by Hafizul Haque Shanto on 6/20/2017.
 */

public class CustomView extends SurfaceView implements Runnable{
    private final int NO_COLLISION = 0;
    private final int SAFE_COLLISION = 1;
    private final int UNSAFE_COLLISOIN = 2;
    private final int TIME_LIMIT_GAME_TIME = 60;
    private final Blast blast;

    // saving variables
    SharedPreferences prefs;
    int scores[];   // for storing best scores

    //time counting variables
    private long initialTime, currentTime, timeElapsed;

    // game control
    volatile boolean playing ;
    private boolean timeEnded;
    private float score;
    private boolean boostMode;

    // necessaray tools
    Context context;
    private Thread viewThread = null;
    private int screenX, screenY;
    int navBarHeight;

    // for road drawing
    private Path road;
    private float eile[];
    private float eileOneWay[];
    private Bitmap roadLeft;
    private Bitmap roadRight;
    private float leftY[];
    private float rightY[];

    // for the buttons & scoreText
    private Rect speedButton;
    private Rect breakButton;

    // drawing tools
    private SurfaceHolder surfaceHolder;
    private Paint paint, speedPress, breakPress;
    private Canvas canvas;

    //


    // game variables
    public Player player;
    private Opponent opponent;
    private Companion companion;
    private OppositeCompanion oppositeCompanion;

    //sounds
    private MediaPlayer gameOnSound;
    private MediaPlayer clashSound;
    private MediaPlayer gearSound;

    //prefered modes
    private int streetMode, timeMode, envMode;


    //constructor
    public CustomView(Context context, int screenX, int screenY, int streetMode, int timeMode, int envMode) {
        super(context);
        score = 0;
        boostMode = false;
        timeEnded = false;

        this.streetMode = streetMode;
        this.timeMode = timeMode;
        this.envMode = envMode;

        prefs = context.getSharedPreferences(StartUpActivity.GAME_SHAR_PREF, Context.MODE_PRIVATE);
        scores = new int[5];
        for(int i = 0; i < 5; ++i){
            scores[i] = prefs.getInt(getKey(streetMode, timeMode, i), 0);
        }

        initialTime = System.currentTimeMillis();
        currentTime = System.currentTimeMillis();
        timeElapsed = 0;

        navBarHeight = context.getResources().getDimensionPixelSize(getResources().getIdentifier("navigation_bar_height" , "dimen", "android"));
        this.context  = context;
        this.screenX = screenX;
        this.screenY = screenY;

        speedButton = new Rect(0, 0, 0, 0);
        breakButton = new Rect(0, 0, 0, 0);


        road = new Path();
        road.reset();
        road.moveTo(50, 0);
        road.lineTo(screenX-50, 0);
        road.lineTo(screenX-25, screenY);
        road.lineTo(25, screenY);
        road.lineTo(50, 0);

        roadLeft = BitmapFactory.decodeResource(context.getResources(), R.drawable.left);
        roadRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.right);
        leftY = new float[3];
        rightY = new float[3];
        leftY[2] = screenY-roadLeft.getHeight();
        leftY[1] = leftY[2]-roadLeft.getHeight();
        leftY[0] = leftY[1]-roadLeft.getHeight();
        rightY[2] = screenY-roadRight.getHeight();
        rightY[1] = rightY[2]-roadRight.getHeight();
        rightY[0] = rightY[1]-roadRight.getHeight();

        eile = new float[3];
        eile[0] = -(2*screenY)/3;
        eile[1] = 0;
        eile[2] = screenY-screenY/3;

        eileOneWay = new float[2];
        eileOneWay[0] = -screenY;
        eileOneWay[1] = -(2*screenY);


        surfaceHolder = getHolder();
        paint = new Paint();
        speedPress = new Paint();
        breakPress = new Paint();
        speedPress.setColor(Color.parseColor("#3300ff00"));
        speedPress.setStyle(Paint.Style.FILL);
        breakPress.setColor(Color.parseColor("#33ff0000"));
        breakPress.setStyle(Paint.Style.FILL);

        player = new Player(context, screenX, screenY);
        player.setY(screenY-(navBarHeight+180+player.getBitmap().getHeight()));
        opponent = new Opponent(context, screenX, screenY);
        companion = new Companion(context, screenX, screenY);
        oppositeCompanion = new OppositeCompanion(context, screenX, screenY);
        blast = new Blast(context, screenX, screenY);

        gameOnSound = MediaPlayer.create(context, R.raw.gameon);
        clashSound = MediaPlayer.create(context, R.raw.clash);

        playing = true;
        gameOnSound.setLooping(true);
        gameOnSound.setVolume(0.5f, 0.5f);
        gameOnSound.start();

        gearSound=MediaPlayer.create(context, R.raw.gear);
        gearSound.setLooping(true);
        gearSound.setVolume(.2f, .2f);
    }


    @Override
    public void run() {
        while (playing==true && timeEnded==false){
            update();
            draw();
            control();
        }
    }

    private void update(){

        currentTime = System.currentTimeMillis();
        timeElapsed = (currentTime-initialTime)/1000;

        if(timeElapsed>60  && timeMode==GameTypeActivity.LIMITED_MODE){
            saveScoreAndUpdateCoin((int)score);
            timeEnded = true;
        }

        if(streetMode==GameTypeActivity.TWO_WAY_MODE){          // road-eile updating
            for(int i = 0; i < 3; ++i){
                eile[i] += player.getSpeed();
                if(eile[i] > screenY)
                    eile[i] = eile[(i+1)%3]-(2*screenY)/3 + player.getSpeed();
            }
        }
        if(streetMode==GameTypeActivity.ONE_WAY_MODE){
            for(int i = 0;  i < 2; ++i){
                eileOneWay[i] += player.getSpeed();
                if(eileOneWay[i]>screenY)
                    eileOneWay[i] = -screenY + player.getSpeed();
            }
        }


        for(int i = 0; i < 3; i++){                 // roadside updating
            rightY[i] += player.getSpeed();
            leftY[i] += player.getSpeed();

            if(leftY[i] > screenY)
                leftY[i] = leftY[(i+1)%3]-(roadLeft.getHeight()-player.getSpeed());
            if(rightY[i]>screenY)
                rightY[i] = rightY[(i+1)%3]-(roadRight.getHeight()-player.getSpeed());
        }

        blast.update();

        player.update();
        companion.update(player.getSpeed());
        if(streetMode==GameTypeActivity.TWO_WAY_MODE){
            opponent.update(player.getSpeed());
        }
        if(streetMode==GameTypeActivity.ONE_WAY_MODE){
            oppositeCompanion.update(player.getSpeed());
        }


        int companionCollision = detectCollision(player.getPlayerRect(), player.getSpeed(), companion.getCompanionRect(), companion.getSpeed());
        int opponentCollision = detectCollision(player.getPlayerRect(), player.getSpeed(), opponent.getOpponentRect(), opponent.getSpeed());
        int oppositeCompanionCollision = detectCollision(player.getPlayerRect(), player.getSpeed(), oppositeCompanion.getCompanionRect(), oppositeCompanion.getSpeed());

        switch (companionCollision){
            case NO_COLLISION:
                break;
            case SAFE_COLLISION:
                if(player.getSafeClashCount()>=5){
                    playing = false;
                    gameOnSound.stop();
                    clashSound.setVolume(.8f, .8f);
                    clashSound.start();
                    blast.getBlastRect().setIntersect(player.getPlayerRect(), companion.getCompanionRect());
                    saveScoreAndUpdateCoin((int)score);
                    break;
                }
                if(player.getY()>companion.getY()){
                    player.setSpeed(0);
                }
                else{
                    companion.setSpeed(0);
                }
                if(player.getX()<companion.getX()){
                    player.setX(player.getX()-8);
                }
                else{
                    player.setX(player.getX()+8);
                }
                clashSound.setVolume(.4f, .4f);
                clashSound.start();
                player.setSafeClashCount(player.getSafeClashCount()+1);
                break;
            case UNSAFE_COLLISOIN:
                playing = false;
                gameOnSound.stop();
                clashSound.setVolume(.8f, .8f);
                clashSound.start();
                blast.getBlastRect().setIntersect(player.getPlayerRect(), companion.getCompanionRect());
                saveScoreAndUpdateCoin((int)score);
                break;
        }
        switch (opponentCollision){
            case NO_COLLISION:
                break;
            case SAFE_COLLISION:
            case UNSAFE_COLLISOIN:
                playing = false;
                gameOnSound.stop();
                clashSound.setVolume(.8f, .8f);
                clashSound.start();
                blast.getBlastRect().setIntersect(player.getPlayerRect(), opponent.getOpponentRect());
                saveScoreAndUpdateCoin((int)score);
                break;
        }

        switch (oppositeCompanionCollision){
            case NO_COLLISION:
                break;
            case SAFE_COLLISION:
                if(player.getSafeClashCount()>=5){
                    playing = false;
                    gameOnSound.stop();
                    clashSound.setVolume(.8f, .8f);
                    clashSound.start();
                    blast.getBlastRect().setIntersect(player.getPlayerRect(), oppositeCompanion.getCompanionRect());
                    saveScoreAndUpdateCoin((int)score);
                    break;
                }
                if(player.getY()>oppositeCompanion.getY()){
                    player.setSpeed(0);
                }
                else{
                    oppositeCompanion.setSpeed(0);
                }
                if(player.getX()<oppositeCompanion.getX()){
                    player.setX(player.getX()-8);
                }
                else{
                    player.setX(player.getX()+8);
                }
                clashSound.setVolume(.4f, .4f);
                clashSound.start();
                player.setSafeClashCount(player.getSafeClashCount()+1);
                break;
            case UNSAFE_COLLISOIN:
                playing = false;
                gameOnSound.stop();
                clashSound.setVolume(.8f, .8f);
                clashSound.start();
                blast.getBlastRect().setIntersect(player.getPlayerRect(), oppositeCompanion.getCompanionRect());
                saveScoreAndUpdateCoin((int)score);
                break;
        }

        if(timeMode==GameTypeActivity.ENDLESS_MODE){
            score += .1;
        }
        else if(timeMode==GameTypeActivity.LIMITED_MODE){
            score += .2;
        }

        if(boostMode){
            // do something for gear button press
        }

        if(player.getSpeed()>20){
            score += (player.getSpeed()/80.0);
            if(player.getX()>screenX/2  && streetMode==GameTypeActivity.TWO_WAY_MODE){
                score += 1;
            }
        }
    }



    public void draw(){
        if(surfaceHolder.getSurface().isValid()){

            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.YELLOW);

            drawRoad();

            if(streetMode==GameTypeActivity.TWO_WAY_MODE){
                canvas.drawBitmap(opponent.getBitmap(), opponent.getX(), opponent.getY(), paint);
            }
            if(streetMode==GameTypeActivity.ONE_WAY_MODE){
                canvas.drawBitmap(oppositeCompanion.getBitmap(), oppositeCompanion.getX(), oppositeCompanion.getY(), paint);
            }
            canvas.drawBitmap(companion.getBitmap(), companion.getX(), companion.getY(), paint);
            canvas.drawBitmap(player.getBitmap(), player.getX(), player.getY(), paint);

            if(envMode==GameTypeActivity.NIGHT_MODE){
                paint.setColor(Color.parseColor("#66000000"));
                canvas.drawRect(0, 0, screenX, screenY, paint);
            }

            drawButtonScoreAndTime();

            if(!playing){
                paint.reset();
                canvas.drawBitmap(blast.getBitmap(), blast.getBlastRect().left-blast.getBitmap().getWidth()/2, blast.getBlastRect().top-blast.getBitmap().getHeight()/2, paint);
                paint.setColor(Color.parseColor("#66000000"));
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(0, 0, screenX, screenY, paint);
                int xPos = canvas.getWidth()/2;
                int yPos = canvas.getHeight()/2;
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(Color.RED);
                paint.setShadowLayer(5, -5, 5, Color.BLUE);
                paint.setFakeBoldText(true);
                paint.setTextSize(60);
                canvas.drawText("Game Over", xPos, yPos, paint);
                paint.setShadowLayer(1, -1, 2, Color.BLUE);
                paint.setTextSize(50);
                canvas.drawText("SCORE: "+(int)score, xPos, yPos+50, paint);
            }

            if(timeEnded){
                paint.reset();
                paint.setColor(Color.parseColor("#66000000"));
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRect(0, 0, screenX, screenY, paint);
                int xPos = canvas.getWidth()/2;
                int yPos = canvas.getHeight()/2;
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(Color.RED);
                paint.setShadowLayer(5, -5, 5, Color.BLUE);
                paint.setFakeBoldText(true);
                paint.setTextSize(60);
                canvas.drawText("Time Exceeded", xPos, yPos, paint);
                paint.setShadowLayer(1, -1, 2, Color.BLUE);
                paint.setTextSize(50);
                canvas.drawText("SCORE: "+(int)score, xPos, yPos+50, paint);
            }


            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawRoad(){
        //drawing roadside , edge line & main gray road
        canvas.drawBitmap(roadLeft, 0, leftY[0], paint);
        canvas.drawBitmap(roadLeft, 0, leftY[1], paint);
        canvas.drawBitmap(roadLeft, 0, leftY[2], paint);
        canvas.drawBitmap(roadRight, screenX-roadRight.getWidth(), rightY[0], paint);
        canvas.drawBitmap(roadRight, screenX-roadRight.getWidth(), rightY[1], paint);
        canvas.drawBitmap(roadRight, screenX-roadRight.getWidth(), rightY[2], paint);
        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        if(envMode==GameTypeActivity.NIGHT_MODE){
            paint.setColor(Color.parseColor("#777777"));
        }
        else{
            paint.setColor(Color.GRAY);
        }
        canvas.drawPath(road, paint);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10);
        canvas.drawLine(70, 0, 50, screenY, paint);
        canvas.drawLine(screenX-70, 0, screenX-50, screenY, paint);

        if(streetMode==GameTypeActivity.TWO_WAY_MODE){
            paint.setStrokeWidth(20);
            canvas.drawLine(screenX/2, eile[0], screenX/2, eile[0]+screenY/3, paint);
            canvas.drawLine(screenX/2, eile[1], screenX/2, eile[1]+screenY/3, paint);
            canvas.drawLine(screenX/2, eile[2], screenX/2, eile[2]+screenY/3, paint);
        }

        else if(streetMode==GameTypeActivity.ONE_WAY_MODE){
            paint.setStrokeWidth(15);
            canvas.drawLine(screenX/3, 0, screenX/3, screenY, paint);
            canvas.drawLine(screenX-screenX/3, 0, screenX-screenX/3, screenY, paint);
            paint.reset();
            paint.setColor(Color.GRAY);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(screenX/3-20, eileOneWay[0], screenX/3+20, eileOneWay[0]+screenY/32, paint);
            canvas.drawRect(screenX-screenX/3-20, eileOneWay[1], screenX-screenX/3+20, eileOneWay[1]+screenY/32, paint);
        }
    }

    private void drawButtonScoreAndTime(){

        int xPos = canvas.getWidth()/4;
        int yPos = canvas.getHeight()-(navBarHeight+70);
        int buttonWidth = canvas.getWidth()/4;

        breakButton.set(xPos-buttonWidth/2, yPos-50, xPos+buttonWidth/2, yPos+50);
        speedButton.set(canvas.getWidth()-xPos-buttonWidth/2, yPos-50, canvas.getWidth()-xPos+buttonWidth/2, yPos+50);

        canvas.drawRect(breakButton, breakPress);
        canvas.drawRect(speedButton, speedPress);

        paint.reset();                                          // button draw & leveling
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
        paint.setTextSize(40);
        paint.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("BREAK", xPos, yPos+20, paint);
        canvas.drawText("SPEED", canvas.getWidth()-xPos, yPos+20, paint);

        paint.reset();                                         // score & elapsed time drawing
        paint.setColor(Color.parseColor("#55000000"));
        paint.setStyle(Paint.Style.FILL);

        if(timeMode==GameTypeActivity.LIMITED_MODE){
            canvas.drawRect(canvas.getWidth()-220, 20, canvas.getWidth()-20, 120, paint);
        }
        else{
            canvas.drawRect(20, 20, 220, 120, paint);
        }
        paint.setColor(Color.RED);
        if(timeMode==GameTypeActivity.LIMITED_MODE){
            paint.setTextSize(25);
            canvas.drawText("Elapsed Time: ", canvas.getWidth()-200, 60, paint);
            String zeroString = (timeElapsed%60<10)? "0":"";
            canvas.drawText("0"+timeElapsed/60+":"+ zeroString +timeElapsed%60, canvas.getWidth()-200, 100, paint);
        }
        else{
            paint.setTextSize(30);
            canvas.drawText("Score", 60, 60, paint);
            canvas.drawText(Integer.toString((int)score), 60, 100, paint);
        }
    }


    private void control(){
        try {
            viewThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause(){
        playing = false;
        gameOnSound.pause();
        try {
            viewThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume(){
        playing = true;
        viewThread = new Thread(this);
        viewThread.start();
        gameOnSound.start();
    }


    int detectCollision(RectF player, float playerSpeed, RectF another, float anotherSpeed){
        int  ret = NO_COLLISION;
        if(player.intersect(another)){
            ret = SAFE_COLLISION;
            if((playerSpeed+anotherSpeed)>20){
                ret = UNSAFE_COLLISOIN;
            }
        }
        return ret;
    }

    private void saveScoreAndUpdateCoin(int score) {

        SharedPreferences.Editor editor = prefs.edit();
        int  temp, current;


        for(int i = 0; i < 5; ++i){
            if(score>scores[i]){
                current = scores[i];
                scores[i] = score;
                for(int j = i+1; j < 5; ++j){
                    temp = scores[j];
                    scores[j] = current;
                    current = temp;
                }
                break;
            }
        }

        for(int i = 0; i < 5; ++i){
            editor.putInt(getKey(streetMode, timeMode, i), scores[i]);
        }
        editor.apply();

        SharedPreferences.Editor editor2 = prefs.edit();
        int newCurrency = prefs.getInt(StartUpActivity.COIN_COUNT_KEY, 0);
        editor2.putInt(StartUpActivity.COIN_COUNT_KEY, newCurrency+(int)score);
        editor2.apply();
    }


    String getKey(int wayMode, int timeMode, int i){
        String retString = (wayMode==GameTypeActivity.ONE_WAY_MODE)? "ONE_WAY" : "TWO_WAY";
        retString += (timeMode==GameTypeActivity.ENDLESS_MODE)? "_ENDLESS_" : "_TIME_LIMIT_";
        retString += i;
        return  retString;
    }



    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int tapX, tapY;
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                tapX = (int) motionEvent.getX();
                tapY = (int) motionEvent.getY();
                if(speedButton.contains(tapX, tapY)){
                    player.setSpeedUpFlag(true);
                    boostMode = true;
                    speedPress.setColor(Color.parseColor("#8800ff00"));
                    gearSound=MediaPlayer.create(context, R.raw.gear);
                    gearSound.setLooping(true);
                    gearSound.setVolume(.2f, .2f);
                    gearSound.start();
                }
                if(breakButton.contains(tapX, tapY)){
                    player.setSpeedDownFlag(true);
                    breakPress.setColor(Color.parseColor("#88ff0000"));
                }
                if(!playing){
                    ((Activity)context).finish();
                }
                if(timeEnded){
                    ((Activity)context).finish();
                }
                break;

            case MotionEvent.ACTION_UP:
                player.setSpeedDownFlag(false);
                player.setSpeedUpFlag(false);
                boostMode = false;
                speedPress.setColor(Color.parseColor("#3300ff00"));
                breakPress.setColor(Color.parseColor("#33ff0000"));
                gearSound.stop();
                break;
        }

        return true;
    }

}
