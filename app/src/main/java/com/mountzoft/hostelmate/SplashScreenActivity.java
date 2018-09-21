package com.mountzoft.hostelmate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


public class SplashScreenActivity extends Activity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String firstTimeCheck = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    editor = sharedPreferences.edit();
                    firstTimeCheck = sharedPreferences.getString("FIRST_TIME_CHECK",null);

                    if(isNetwork()){
                        if(firstTimeCheck == null){
                            Intent intent = new Intent(SplashScreenActivity.this,LoginSelectActivity.class);
                            startActivity(intent);
                        }else if(firstTimeCheck.equals("admin")){
                            Intent intent = new Intent(SplashScreenActivity.this,ReceptionLoginActivity.class);
                            startActivity(intent);
                        }else if(firstTimeCheck.equals("inmate")){
                            Intent intent = new Intent(SplashScreenActivity.this,InmatesLoginActivity.class);
                            startActivity(intent);
                        }
                    }else {
                        finish();
                        Intent intent = new Intent(SplashScreenActivity.this,NoNetwork.class);
                        startActivity(intent);
                    }
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
    @Override
    public void onBackPressed() {}

    boolean isNetwork(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

}