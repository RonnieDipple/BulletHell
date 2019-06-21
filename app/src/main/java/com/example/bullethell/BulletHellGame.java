package com.example.bullethell;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;


class BulletHellGame extends SurfaceView implements Runnable {
    // Are we currently debugging
    boolean mDebugging = true;

    //Objects for the game loop/thread
    private Thread mGameThread = null;
    private volatile boolean mPlaying;
    private boolean mPaused = true;

    // Objects for drawing
    private SurfaceHolder mOurHolder;
    private Canvas mCanvas;
    private Paint mPaint;

    // Keep track of the frame rate
    private long mFPS;

    // The number of milliseconds in a second
    private final int MILLIS_IN_SECOND = 1000;

    // Holds the resolution of the screen
    private int mScreenX;
    private int mScreenY;

    // How big will the text be?
    private int mFontSize;
    private int mFontMargin;

    // These are for the sound
    private SoundPool mSP;
    private int mBeepID = -1;
    private int mTeleportID = -1;

    // This is the constructor method that gets called
    // from BulletHellActivity
    public BulletHellGame(Context context, int x, int y) {
        super(context);

        mScreenX = x;
        mScreenY = y;
        // Font is 5% of screen width
        mFontSize = mScreenX / 20;
        // Margin is 2% of screen width
        mFontMargin = mScreenX / 50;

        mOurHolder = getHolder();
        mPaint = new Paint();

        // Initialize the SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes =
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build();
            mSP = new SoundPool.Builder()
                    .setMaxStreams(5)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSP = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            descriptor = assetManager.openFd("beep.ogg");
            mBeepID = mSP.load(descriptor,0);

            descriptor = assetManager.openFd("teleport.ogg");
            mTeleportID = mSP.load(descriptor, 0);
        }catch (IOException e){
            Log.e("error", "failed to load sound files");
        }

        startGame();


    }

    // Called to start a new game
    public void startGame(){

    }

    // Spawns ANOTHER bullet
    private void spawnBullet(){

    }

    // Handles the game loop
    @Override
    public void run (){
        while (mPlaying){

            long frameStartTime = System.currentTimeMillis();

            if(!mPaused){
                update();
                //Now all the bullets have been moved
                //we can detect any collisions
                detectCollisions();
            }
            draw();

            long timeThisFrame = System.currentTimeMillis() - frameStartTime;
            if (timeThisFrame >= 1){
                mFPS = MILLIS_IN_SECOND / timeThisFrame;
            }

        }
    }

    // Update all game objects
    private void update(){

    }

    // Detects collisions

    private void detectCollisions(){

    }

    private void draw(){
        if (mOurHolder.getSurface().isValid()){
            mCanvas = mOurHolder.lockCanvas();
            mCanvas.drawColor(Color.argb(255, 243, 111, 36));


            // All the drawing code will go here

            if(mDebugging){
                printDebuggingText();
            }

            mOurHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        return true;
    }

    public void pause(){
        mPlaying = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e){
            Log.e("Error:", "joining thread");
        }
    }

    public void resume(){
        mPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    private void printDebuggingText(){
        int debugSize = 35;
        int debugStart = 150;
        mPaint.setTextSize(debugSize);

        mCanvas.drawText("FPS: " + mFPS, 10, debugStart + debugSize, mPaint);
    }

}
