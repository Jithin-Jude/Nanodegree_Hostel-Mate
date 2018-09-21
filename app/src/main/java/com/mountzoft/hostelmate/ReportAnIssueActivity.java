package com.mountzoft.hostelmate;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportAnIssueActivity extends AppCompatActivity {

    EditText titleEditText;
    Spinner spinnerBlock;
    Spinner spinnerRoom;
    EditText descriptionEditText;

    DatabaseReference databaseIssue;

    String personEmail;

    ImageView mImageView;

    Bitmap photo;
    String imageEncoded = null;

    private static final int CAMERA_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_an_issue);

        personEmail = getIntent().getExtras().get("PERSON_EMAIL").toString();

        databaseIssue = FirebaseDatabase.getInstance().getReference("issues");

        titleEditText = findViewById(R.id.ed_title);
        spinnerBlock = findViewById(R.id.sp_block);
        spinnerRoom = findViewById(R.id.sp_room);
        descriptionEditText = findViewById(R.id.ed_description);
        mImageView = findViewById(R.id.camera_img);

        ArrayAdapter<CharSequence> adapterSpinnerBlock = ArrayAdapter.createFromResource(this,
                R.array.block_list, android.R.layout.simple_spinner_item);
        adapterSpinnerBlock.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBlock.setAdapter(adapterSpinnerBlock);

        ArrayAdapter<CharSequence> adapterSpinnerRoom = ArrayAdapter.createFromResource(this,
                R.array.room_list, android.R.layout.simple_spinner_item);
        adapterSpinnerRoom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoom.setAdapter(adapterSpinnerRoom);
    }

    public void addIssue(View view){
        if(imageEncoded == null){
            Toast.makeText(this,"Take photo of Issue",Toast.LENGTH_LONG).show();
            return;
        }
        String title = titleEditText.getText().toString();
        String block = spinnerBlock.getSelectedItem().toString();
        String room = spinnerRoom.getSelectedItem().toString();
        String description = descriptionEditText.getText().toString();
        String reportedBy = personEmail;

        String id = databaseIssue.push().getKey();
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String status = "Not Fixed";

        Issue issue = new Issue(id, title, block, room, description, reportedBy, date, status, imageEncoded);

        databaseIssue.child(id).setValue(issue);

        Toast.makeText(this,"Issue added",Toast.LENGTH_LONG).show();

        updateWidget(title);
    }

    public void takePhoto(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            mImageView.setImageBitmap(photo);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
            imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        }
    }

    void updateWidget(String widgetText){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.widget_for_inmates);

        Intent intent = new Intent(this, InmatesLoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.img_widget, pendingIntent);

        ComponentName thisWidget = new ComponentName(this, WidgetForInmates.class);
        remoteViews.setTextViewText(R.id.appwidget_text, widgetText);
        remoteViews.setImageViewResource(R.id.img_widget, R.drawable.hostel_red);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);

    }
}
