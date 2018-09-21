package com.mountzoft.hostelmate;

import android.support.annotation.Keep;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
@Keep
public class Issue {
    String issueId;
    String issueTitle;
    String issueBlock;
    String issueRoom;
    String issueDescription;
    String issueReportedBy;
    String issueDate;
    String issueStatus;
    String imageEncoded;

    public Issue(String issueId, String issueTitle, String issueBlock,
                 String issueRoom, String issueDescription, String issueReportedBy,
                 String issueDate, String issueStatus, String imageEncoded){
        this.issueId = issueId;
        this.issueTitle = issueTitle;
        this.issueBlock = issueBlock;
        this.issueRoom = issueRoom;
        this.issueDescription = issueDescription;
        this.issueReportedBy = issueReportedBy;
        this.issueDate = issueDate;
        this.issueStatus = issueStatus;
        this.imageEncoded = imageEncoded;
    }

    public Issue(){}

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getIssueTitle() {
        return issueTitle;
    }

    public void setIssueTitle(String issueTitle) {
        this.issueTitle = issueTitle;
    }

    public String getIssueBlock() {
        return issueBlock;
    }

    public void setIssueBlock(String issueBlock) {
        this.issueBlock = issueBlock;
    }

    public String getIssueRoom() {
        return issueRoom;
    }

    public void setIssueRoom(String issueRoom) {
        this.issueRoom = issueRoom;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public String getIssueReportedBy() {
        return issueReportedBy;
    }

    public void setIssueReportedBy(String issueReportedBy) {
        this.issueReportedBy = issueReportedBy;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
    }

    public String getImageEncoded() {
        return imageEncoded;
    }

    public void setImageEncoded(String imageEncoded) {
        this.imageEncoded = imageEncoded;
    }
}
