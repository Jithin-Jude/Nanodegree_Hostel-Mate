package com.mountzoft.hostelmate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ReportedIssuesActivity extends AppCompatActivity {

    ReceptionIssueRecyclerViewAdapter adapter;

    public static List<Issue> issueList = new ArrayList<>();

    DatabaseReference databaseIssue;

    ProgressBar progressBarLodingIssues;

    static int numberOfIssuesOld;
    static int numberOfIssuesNew;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reported_issues);
        setTitle(getString(R.string.reported_issues));

        //Disable widget in Admin mode
        PackageManager pacman = getApplicationContext().getPackageManager();
        pacman.setComponentEnabledSetting(new ComponentName(getApplicationContext(), WidgetForInmates.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

        progressBarLodingIssues = findViewById(R.id.loading_issues);
        progressBarLodingIssues.setVisibility(View.VISIBLE);

        databaseIssue = FirebaseDatabase.getInstance().getReference("issues");
        databaseIssue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                issueList.clear();
                for (DataSnapshot issueSnapshot : dataSnapshot.getChildren()) {
                    Issue issue = issueSnapshot.getValue(Issue.class);
                    issueList.add(issue);
                }

                if(!sharedPreferences.getBoolean("NOTIFICATIONS_ON", false)){
                    editor.putInt("NUMBER_OF_ISSUES",issueList.size());
                    editor.commit();
                }

                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_reported_issues);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new ReceptionIssueRecyclerViewAdapter(getApplicationContext(), issueList);
                recyclerView.setAdapter(adapter);
                progressBarLodingIssues.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.control_panel:
                Intent intent = new Intent(this, ControlPanelActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
