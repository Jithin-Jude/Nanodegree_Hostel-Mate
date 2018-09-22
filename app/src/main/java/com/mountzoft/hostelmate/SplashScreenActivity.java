package com.mountzoft.hostelmate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import static java.lang.Thread.sleep;


public class SplashScreenActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String firstTimeCheck = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new AsyncCaller().execute();
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
    @Override
    public void onBackPressed() {}

//=========================================AsyncTask================================================
    private class AsyncCaller extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
        }
        @Override
        protected Void doInBackground(Void... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            try {
                sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            editor = sharedPreferences.edit();
            firstTimeCheck = sharedPreferences.getString("FIRST_TIME_CHECK",null);
            editor.apply();

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
//=========================================AsyncTask================================================
    boolean isNetwork(){
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
}
}