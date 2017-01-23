package com.example.trustring.myfirstapp;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

public class FloatingFaceBubbleService extends Service {
    private WindowManager windowManager;

    private ImageView floatingFaceBubble;
    public String TAG = "print";
    Handler handler = new Handler();
    int numberOfOutsideTaps = 0;
    boolean disableMenu= false;
    int numberOfTaps = 0;
    long lastTapTimeMs = 0;
    long OutsideTapTimeMs = 0;

    public void onCreate() {
        super.onCreate();

        floatingFaceBubble = new ImageView(this);
        //a face floating bubble as imageView
        floatingFaceBubble.setImageResource(R.drawable.floating_bubble);
        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        //here is all the science of params
        final LayoutParams myParams = new WindowManager.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_PHONE,
                LayoutParams.FLAG_NOT_FOCUSABLE |
                LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        myParams.gravity = Gravity.TOP | Gravity.LEFT;
        myParams.x=0;
        myParams.y=100;
        // add a floatingfacebubble icon in window
        windowManager.addView(floatingFaceBubble, myParams);
        try{
            //for moving the picture on touch and slide
            floatingFaceBubble.setOnTouchListener(new View.OnTouchListener() {
                WindowManager.LayoutParams paramsT = myParams;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;
                private long touchStartTime = 0;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: ON TOUCHHHHHH");

                    //remove face bubble on long press
//                    if(System.currentTimeMillis()-touchStartTime>ViewConfiguration.getLongPressTimeout() && initialTouchX== event.getX()){
//
//                        windowManager.removeView(floatingFaceBubble);
//                        stopSelf();
//                        Log.d(TAG, "onTouch: EXITTTTTTTTT");
//                        return false;
//                    }

                    switch(event.getAction()){

                        case MotionEvent.ACTION_DOWN:
                            touchStartTime = System.currentTimeMillis();
                            initialX = myParams.x;
                            initialY = myParams.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            Log.d(TAG, "onTouch: DOWNNNNNNNNNNNN");
                            break;
                        case MotionEvent.ACTION_UP:
                            Log.d(TAG, "onTouch: UPPPPPPPPPPPPPP");
                            handler.removeCallbacksAndMessages(null);

                            if ((System.currentTimeMillis() - touchStartTime) > ViewConfiguration.getTapTimeout()) {
                                //it was not a tap
                                numberOfTaps = 0;
                                lastTapTimeMs = 0;
                                break;
                            }

                            if (numberOfTaps > 0
                                    && (System.currentTimeMillis() - lastTapTimeMs) < ViewConfiguration.getDoubleTapTimeout()) {
                                numberOfTaps += 1;
                            } else {
                                numberOfTaps = 1;
                            }

                            lastTapTimeMs = System.currentTimeMillis();

                            if (numberOfTaps == 3) {
                                Toast.makeText(getApplicationContext(), "triple", Toast.LENGTH_SHORT).show();
                                //windowManager.removeView(floatingFaceBubble);
                                //handle triple tap
                            } else if (numberOfTaps == 2) {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //handle double tap
                                        Toast.makeText(getApplicationContext(), "double", Toast.LENGTH_SHORT).show();


                                    }
                                }, ViewConfiguration.getDoubleTapTimeout());
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            myParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                            myParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(v, myParams);
                            Log.d(TAG, "onTouch: MOVEEEEEEEEEEEEE");
                            break;
                        case MotionEvent.ACTION_OUTSIDE:
                            if ((System.currentTimeMillis() - OutsideTapTimeMs) > ViewConfiguration.getTapTimeout())
                            {
                                numberOfOutsideTaps = 1;
                            }else{
                                numberOfOutsideTaps +=1;
                            }
                            OutsideTapTimeMs = System.currentTimeMillis();
                            if (numberOfOutsideTaps == 2){
                                Log.d(TAG, "onTouch: Doble OUTSIDEEEEEEE");
                                if (disableMenu){
                                    floatingFaceBubble.setVisibility(View.VISIBLE);
                                    disableMenu = false;
                                }else{
                                    //windowManager.removeView(floatingFaceBubble);
                                    floatingFaceBubble.setVisibility(View.INVISIBLE);
                                    // Anh muon them phan chup anh vao day !
                                    floatingFaceBubble.setVisibility(View.VISIBLE);
                                    disableMenu = true;
                                }
                            }
                            Log.d(TAG, "onTouch: OUTSIDEEEEEEE"+MotionEvent.ACTION_OUTSIDE);
                            break;

                    }
                    return false;
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}