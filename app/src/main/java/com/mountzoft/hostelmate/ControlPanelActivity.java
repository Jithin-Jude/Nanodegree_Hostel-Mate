package com.mountzoft.hostelmate;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ControlPanelActivity extends AppCompatActivity {
    Switch notificationSwitch;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    List<AdminLogin> adminLoginList = new ArrayList<>();

    String NOTIFICATIONS_ON = "NOTIFICATIONS_ON";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);
        setTitle(getString(R.string.control_panel));

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

        notificationSwitch = findViewById(R.id.notification_switch);

        notificationSwitch.setChecked(sharedPreferences.getBoolean(NOTIFICATIONS_ON, false));

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if(isChecked){
                    turnOnNotifications();
                    editor.putBoolean(NOTIFICATIONS_ON,true);
                }else {
                    turnOffNotifications();
                    editor.putBoolean(NOTIFICATIONS_ON,false);
                }
                editor.commit();
            }
        });
    }

    private void turnOnNotifications(){
        Intent intent = new Intent(this, NotificationService.class);
        startService(intent);
    }
    private void turnOffNotifications(){
        Intent intent = new Intent(this, NotificationService.class);
        stopService(intent);
    }

    public void changePassword(View view){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        final EditText edittextNewPassword = new EditText(this);
        edittextNewPassword.setHint(R.string.new_password_please);
        edittextNewPassword.setHintTextColor(getResources().getColor(R.color.grey));
        edittextNewPassword.setTextColor(getResources().getColor(R.color.colorAccent));

        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.O)
        edittextNewPassword.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }

        };
        edittextNewPassword.setFilters(new InputFilter[] { filter });

        builder.setTitle(R.string.change_password)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String userName = getString(R.string.user_name_value);
                        String newPassword = edittextNewPassword.getText().toString();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("admin_login")
                                .child("-LLz9xijklx_GLw4DRSh");
                        AdminLogin adminLogin = new AdminLogin(userName, newPassword);
                        databaseReference.setValue(adminLogin);

                        Toast.makeText(getApplicationContext(), R.string.password_updated, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.ic_password)
                .setView(edittextNewPassword)
                .show();
    }

}
