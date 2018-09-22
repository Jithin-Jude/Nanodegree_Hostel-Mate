package com.mountzoft.hostelmate;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.mountzoft.hostelmate.InmatesLoginActivity.mGoogleSignInClient;

public class IssueStatusActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerViewAdapter adapter;

    public static List<Issue> issueList = new ArrayList<>();

    DatabaseReference databaseIssue;

    ProgressBar progressBarLodingIssuesForInmates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBarLodingIssuesForInmates = findViewById(R.id.loading_issues_for_inmates);
        progressBarLodingIssuesForInmates.setVisibility(View.VISIBLE);

        String personName = getIntent().getExtras().get("PERSON_NAME").toString();
        final String personEmail = getIntent().getExtras().get("PERSON_EMAIL").toString();
        String profilePicUri = getIntent().getExtras().get("PROFILE_PIC").toString();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ReportAnIssueActivity.class);
                intent.putExtra("PERSON_EMAIL",personEmail);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView personNameTV = header.findViewById(R.id.person_name);
        TextView personEmailTV = header.findViewById(R.id.person_email);
        ImageView profilePic = header.findViewById(R.id.profile_pic);

        Glide.with(this)
                .load(profilePicUri)
                .apply(new RequestOptions()
                        .circleCrop())
                .into(profilePic);

        personNameTV.setText(personName);
        personEmailTV.setText(personEmail);

        databaseIssue = FirebaseDatabase.getInstance().getReference("issues");
        databaseIssue.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                issueList.clear();
                for(DataSnapshot issueSnapshot : dataSnapshot.getChildren()){
                    Issue issue = issueSnapshot.getValue(Issue.class);
                    if(issue.issueReportedBy.equals(personEmail)) {
                        issueList.add(issue);
                    }
                }

                if(issueList.size() != 0)
                updateWidget(issueList.get(issueList.size()-1).issueStatus);

                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_issue_status);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new RecyclerViewAdapter(getApplicationContext(), issueList);
                recyclerView.setAdapter(adapter);
                progressBarLodingIssuesForInmates.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                        Intent intent = new Intent(getApplicationContext(),InmatesLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
        Toast.makeText(this,"Logged out ",Toast.LENGTH_LONG).show();
    }

    void updateWidget(String isFixed){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.widget_for_inmates);
        ComponentName thisWidget = new ComponentName(this, WidgetForInmates.class);
        String widgetText = issueList.get(issueList.size()-1).issueTitle;
        remoteViews.setTextViewText(R.id.appwidget_text, widgetText);
        if(isFixed.equals("Fixed")) {
            remoteViews.setImageViewResource(R.id.img_widget, R.drawable.hostel_green);
        }else {
            remoteViews.setImageViewResource(R.id.img_widget, R.drawable.hostel_red);
        }
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

    }
}
