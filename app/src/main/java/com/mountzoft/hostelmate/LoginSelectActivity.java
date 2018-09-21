package com.mountzoft.hostelmate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_select);
        getSupportActionBar().hide();
    }

    public void receptionLoginSelected(View view){
        Intent intent = new Intent(this,ReceptionLoginActivity.class);
        startActivity(intent);
    }

    public void inmatesLoginSelected(View view){
        Intent intent = new Intent(this,InmatesLoginActivity.class);
        startActivity(intent);
    }
}
