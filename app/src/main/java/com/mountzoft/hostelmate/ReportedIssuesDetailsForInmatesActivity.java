package com.mountzoft.hostelmate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class ReportedIssuesDetailsForInmatesActivity extends AppCompatActivity{

    TextView blockTextView;
    TextView roomTextView;
    TextView descriptionTextView;
    TextView statusTextView;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reported_issues_details_for_inmates);

        int position = getIntent().getIntExtra("POSITION_ID",0);

        blockTextView = findViewById(R.id.tv_block);
        roomTextView = findViewById(R.id.tv_room);
        descriptionTextView = findViewById(R.id.tv_description);
        statusTextView = findViewById(R.id.tv_status_inmates);
        mImageView = findViewById(R.id.img_issue_inmates);

        setTitle(IssueStatusActivity.issueList.get(position).issueTitle);
        blockTextView.setText(IssueStatusActivity.issueList.get(position).issueBlock);
        roomTextView.setText(IssueStatusActivity.issueList.get(position).issueRoom);
        descriptionTextView.setText(IssueStatusActivity.issueList.get(position).issueDescription);
        statusTextView.setText(IssueStatusActivity.issueList.get(position).issueStatus);

        try {
            Bitmap imageBitmap = decodeFromFirebaseBase64(IssueStatusActivity.issueList.get(position).imageEncoded);
            mImageView.setImageBitmap(imageBitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Bitmap decodeFromFirebaseBase64(String image){
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }
}
