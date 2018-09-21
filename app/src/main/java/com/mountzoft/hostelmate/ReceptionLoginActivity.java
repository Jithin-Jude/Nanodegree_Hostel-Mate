package com.mountzoft.hostelmate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReceptionLoginActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    DatabaseReference databaseIssue;

    List<AdminLogin> adminLoginList = new ArrayList<>();

    String userNamefromServer;
    String passwordfromServer;

    EditText editTextUserName;
    EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reception_login);
        getSupportActionBar().hide();

        editTextUserName = findViewById(R.id.ed_user_name);
        editTextPassword = findViewById(R.id.ed_password);

        databaseIssue = FirebaseDatabase.getInstance().getReference("admin_login");
        databaseIssue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adminLoginList.clear();
                for(DataSnapshot issueSnapshot : dataSnapshot.getChildren()){
                    AdminLogin adminLogin = issueSnapshot.getValue(AdminLogin.class);
                    adminLoginList.add(adminLogin);
                }
                userNamefromServer = adminLoginList.get(0).userName;
                passwordfromServer = adminLoginList.get(0).password;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void receptionLogin(View view){

        String givenUserName = editTextUserName.getText().toString();
        String givenPassword = editTextPassword.getText().toString();

        /*
        String id = databaseIssue.push().getKey();
        AdminLogin adminLogin = new AdminLogin(userName,password);
        databaseIssue.child(id).setValue(adminLogin);
        Toast.makeText(this,"Admin Created!",Toast.LENGTH_LONG).show();
        */
        if(givenUserName.equals(userNamefromServer) && givenPassword.equals(passwordfromServer)) {
            Intent intent = new Intent(this, ReportedIssuesActivity.class);
            startActivity(intent);

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            editor = sharedPreferences.edit();
            editor.putString("FIRST_TIME_CHECK","admin");
            editor.commit();
            finish();
        }else{
            Toast.makeText(this,"Authentication failed! Please try again. Maybe a network issue!",Toast.LENGTH_LONG)
                    .show();
        }
    }
}
