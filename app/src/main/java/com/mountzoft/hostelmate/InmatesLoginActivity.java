package com.mountzoft.hostelmate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class InmatesLoginActivity extends AppCompatActivity {

    String PERSON_NAME = "PERSON_NAME";
    String PERSON_EMAIL = "PERSON_EMAIL";
    String PROFILE_PIC = "PROFILE_PIC";
    String FIRST_TIME_CHECK = "FIRST_TIME_CHECK";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    SignInButton signInButton;
    ProgressBar signInProgress;

    public  static GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 1;

    private static final String TAG = "InmatesLoginActivity";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inmates_login);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        signInButton = findViewById(R.id.sign_in_button);
        signInProgress = findViewById(R.id.sign_in_progress);
        signInProgress.setVisibility(View.INVISIBLE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            successSignIn();
        }
        //updateUI(currentUser);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        signInProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                signInProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(this, R.string.google_sign_in_failed, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Toast.makeText(getApplicationContext(), "signInWithCredential:success", Toast.LENGTH_LONG).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            successSignIn();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            signInProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), R.string.signIn_with_credential_failure, Toast.LENGTH_LONG).show();
                            //updateUI(null);
                        }
                    }
                });
    }

    public void successSignIn(){
        signInProgress.setVisibility(View.INVISIBLE);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        String personName = null;
        String personGivenName = null;
        String personFamilyName = null;
        String personEmail = null;
        String personId = null;
        Uri personPhoto = null;

        if (acct != null) {
            personName = acct.getDisplayName();
            personGivenName = acct.getGivenName();
            personFamilyName = acct.getFamilyName();
            personEmail = acct.getEmail();
            personId = acct.getId();
            personPhoto = acct.getPhotoUrl();
        }
        Intent intent = new Intent(getApplicationContext(),IssueStatusActivity.class);
        intent.putExtra(PERSON_NAME,personName);
        intent.putExtra(PERSON_EMAIL,personEmail);
        intent.putExtra(PROFILE_PIC,personPhoto.toString());
        startActivity(intent);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        editor.putString(FIRST_TIME_CHECK,"inmate");
        editor.commit();
        finish();
        Toast.makeText(this,getString(R.string.welcome)+personName,Toast.LENGTH_LONG).show();
    }
}
