package com.encapital.io.copybackground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    final int SMS_PERMISSION_CODE = 1;
    final String[] PERMISSION_LIST = new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE};
    private boolean mIsFirst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(isPermissionsGranted()){
            handlerPermissionsGranted();
        }else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    PERMISSION_LIST,
                    SMS_PERMISSION_CODE);
        }
    }





    public boolean isPermissionsGranted() {
        for (String permission:PERMISSION_LIST
        ) {
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    private void handlerPermissionsGranted(){
        handlerPermissionsGranted(1000);
    }

    private void handlerPermissionsGranted(long delayMillis){
        if(!mIsFirst) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    finish();
                }
            }, delayMillis);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == SMS_PERMISSION_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && isGranted(grantResults)) {

                handlerPermissionsGranted(500);

            }
        }
    }

    private boolean isGranted(int[] grantResults){
        for(int grantResult:grantResults){
            if(grantResult != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

}
