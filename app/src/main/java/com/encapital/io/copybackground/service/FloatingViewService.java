package com.encapital.io.copybackground.service;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.icu.lang.UCharacter;
import android.os.Handler;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.encapital.io.copybackground.MySharedPreferences;
import com.encapital.io.copybackground.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class FloatingViewService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private TextView tvScriptNumber;
    private int currentPosition=0;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Handler mTimerHandler = new Handler();

    private void stopTimer(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
        }
    }

    private void startTimer(int period, final List<String> data){
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run(){
                        if(currentPosition>=data.size()){
                            currentPosition=0;
                        }
                        tvScriptNumber.setText(String.valueOf(currentPosition));
                        String[] temp = data.get(currentPosition).split("<br/>" );
                        String des ="";
                        for (String item:temp
                             ) {
                            if(des.length()>0){
                                des+="\n";
                            }
                            des+=item;
                        }

                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", des);
                        clipboard.setPrimaryClip(clip);
                        currentPosition++;

                    }
                });
            }
        };

        mTimer.schedule(mTimerTask, 1, period);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        final WindowManager.LayoutParams params;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
          MySharedPreferences.init(getApplicationContext());
        tvScriptNumber= mFloatingView.findViewById(R.id.tvScriptNumber);
        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> scriptCopy = new Gson().fromJson(MySharedPreferences.getScriptCopy(), listType);
        startTimer(MySharedPreferences.getPeriod()*1000,scriptCopy );
        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.RIGHT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 180;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mWindowManager.addView(mFloatingView,params);



        //Set the close button
        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);

        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
                stopTimer();
                stopSelf();
            }
        });


        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.

                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }
}