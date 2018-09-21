package com.mountzoft.hostelmate;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by JithinJude on 15-03-2018.
 */

public class ReceptionIssueRecyclerViewAdapter extends RecyclerView.Adapter<ReceptionIssueRecyclerViewHolder> {

    Context context;
    private List<Issue> issueList;

    private LayoutInflater mInflater;
    private RecyclerViewClickListener mClickListener;

    // data is passed into the constructor
    ReceptionIssueRecyclerViewAdapter(Context context, List<Issue> issueList) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.issueList = issueList;
    }

    // inflates the row layout from xml when needed
    @Override
    public ReceptionIssueRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ReceptionIssueRecyclerViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ReceptionIssueRecyclerViewHolder holder, int pos) {
        holder.title.setText(issueList.get(pos).issueTitle);
        String issueLocation = issueList.get(pos).issueBlock+", "+issueList.get(pos).issueRoom;
        holder.blockAndRoom.setText(issueLocation);
        holder.date.setText(issueList.get(pos).issueDate);
        holder.issueStatus.setText(issueList.get(pos).issueStatus);
        if(issueList.get(pos).issueStatus.equals("Fixed")){
            holder.issueStatus.setTextColor(context.getResources().getColor(R.color.green));
        }else {
            holder.issueStatus.setTextColor(context.getResources().getColor(R.color.red));
        }

        try {
            Bitmap imageBitmap = decodeFromFirebaseBase64(ReportedIssuesActivity.issueList.get(pos).imageEncoded);
            holder.imageView.setImageBitmap(imageBitmap);
        }catch (Exception e){
            e.printStackTrace();
        }

        holder.setItemClickListener(new RecyclerViewClickListener() {
            @Override
            public void onItemClick(int pos) {}
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return issueList.size();
        //return mCategoryRecyclerviewData.size();
    }

    public static Bitmap decodeFromFirebaseBase64(String image) {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }


}
