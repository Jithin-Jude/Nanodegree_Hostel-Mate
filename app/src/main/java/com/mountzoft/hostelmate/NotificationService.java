package com.mountzoft.hostelmate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mountzoft.hostelmate.ReportedIssuesActivity.numberOfIssuesNew;
import static com.mountzoft.hostelmate.ReportedIssuesActivity.numberOfIssuesOld;

public class NotificationService extends Service {
    String CHANNEL_ID = "hostelmate_notification";

    DatabaseReference databaseIssue;

    ValueEventListener valueEventListener;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public static List<Issue> issueList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

        Toast.makeText(this,"Notifications activated",Toast.LENGTH_LONG).show();

        databaseIssue = FirebaseDatabase.getInstance().getReference("issues");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                issueList.clear();
                for(DataSnapshot issueSnapshot : dataSnapshot.getChildren()){
                    Issue issue = issueSnapshot.getValue(Issue.class);
                    issueList.add(issue);
                }
                numberOfIssuesOld = sharedPreferences.getInt("NUMBER_OF_ISSUES", 0);
                //Toast.makeText(getApplicationContext(),"num Old : "+numberOfIssuesOld,Toast.LENGTH_LONG).show();
                numberOfIssuesNew = issueList.size();
                //Toast.makeText(getApplicationContext(),"num New : "+numberOfIssuesNew,Toast.LENGTH_LONG).show();

                if(numberOfIssuesNew > numberOfIssuesOld) {
                    displayNotification();
                }

                editor.putInt("NUMBER_OF_ISSUES",issueList.size());
                editor.commit();
                //Toast.makeText(getApplicationContext(),"num New update : "+issueList.size(),Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        databaseIssue.addValueEventListener(valueEventListener);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        databaseIssue.removeEventListener(valueEventListener);
        Toast.makeText(this,"Notifications deactivated",Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void displayNotification(){
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_notifications_active_white_24dp);
        builder.setContentTitle("Hostel Mate");
        builder.setContentText("New issue reported!");
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setAutoCancel(true);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ReceptionLoginActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        int notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManagerCompat.notify(notificationId, builder.build());
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name  = "Hostelmate notification";
            String description = "Hi";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
