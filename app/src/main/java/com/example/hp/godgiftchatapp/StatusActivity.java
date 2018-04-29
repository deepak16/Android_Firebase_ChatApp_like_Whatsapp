package com.example.hp.godgiftchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mStatus;
    private Button mSaveChanges;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mStatus = (EditText)findViewById(R.id.status_input);
        mSaveChanges = (Button)findViewById(R.id.status_save);
        mProgressDialog = new ProgressDialog(this);

        String status_value = getIntent().getStringExtra("status_string");
        mStatus.setText(status_value);

        mToolbar = (Toolbar) findViewById(R.id.status_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProgressDialog.setTitle("Saving Changes");
                mProgressDialog.setMessage("Please wait while we save the changes");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = current_user.getUid();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("status");



                mDatabase.setValue(mStatus.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            mProgressDialog.dismiss();

                            Toast.makeText(StatusActivity.this, "Status Updated", Toast.LENGTH_SHORT).show();

                        }
                        else {
                            mProgressDialog.hide();
                        }
                    }
                });



            }
        });


    }
}
