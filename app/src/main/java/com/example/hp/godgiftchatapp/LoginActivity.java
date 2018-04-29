package com.example.hp.godgiftchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private EditText mDisplayName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mLgnBtn;
    private FirebaseAuth mAuth;

    private Toolbar mToolbar;

    private DatabaseReference mUserDatabase;

    private ProgressDialog mLogProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        mEmail = (EditText)findViewById(R.id.login_email);
        mLogProgress = new ProgressDialog(this);
        mPassword = (EditText)findViewById(R.id.login_password);
        mLgnBtn = (Button) findViewById(R.id.lgn_btn);
        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mLgnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
                {
                    mLogProgress.setTitle("Logging you in");
                    mLogProgress.setMessage("Please wait while we connect to your account..");
                    mLogProgress.setCanceledOnTouchOutside(false);
                    mLogProgress.show();
                    login_user(email,password);
                }
                else {
                    Toast.makeText(LoginActivity.this, "Please fill the form carefully!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void login_user(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       // Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {

                        String current_userId = mAuth.getCurrentUser().getUid();
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            mUserDatabase.child(current_userId).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mLogProgress.dismiss();
                                    Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();

                                }
                            });



                        }
                        else {
                            mLogProgress.hide();
                            Toast.makeText(LoginActivity.this, "Authentication Failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });


    }
}
