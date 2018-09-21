package com.mountzoft.hostelmate;

import android.support.annotation.Keep;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
@Keep
public class AdminLogin {
    String userName;
    String password;

    public AdminLogin(String userName, String password){
        this.userName = userName;
        this.password = password;
    }
    public AdminLogin(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
