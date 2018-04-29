package com.example.hp.godgiftchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText mDisplayName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mCrtBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Toolbar mToolbar;

    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDisplayName = (EditText)findViewById(R.id.reg_display_name);
        mEmail = (EditText)findViewById(R.id.reg_email);
        mPassword = (EditText)findViewById(R.id.reg_password);
        mCrtBtn = (Button) findViewById(R.id.reg_create_btn);
        mRegProgress = new ProgressDialog(this);
        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mCrtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_name = mDisplayName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(!TextUtils.isEmpty(display_name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
                {
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your account..");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    register_user(display_name,email,password);
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Please fill the form carefully!", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

    private void register_user(final String display_name, String email, String password) {



        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Tag1", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {

                            String current_userId = mAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = current_user.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String,String> newhashmap = new HashMap<String, String>();
                            newhashmap.put("name",display_name);
                            newhashmap.put("status","Hey there I am using Godgift chat app");
                            newhashmap.put("image","default");
                            newhashmap.put("thumbnail","thumbnail");
                            newhashmap.put("device_token",deviceToken);

                            mDatabase.setValue(newhashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        mRegProgress.dismiss();
                                        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();

                                    }
                                }
                            });






                        }
                        else {
                            mRegProgress.hide();
                            Toast.makeText(RegisterActivity.this,"You got some error. ",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
