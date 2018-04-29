package com.example.hp.godgiftchatapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private TextView mdisplayName,mCurrentStatus,mTotalFriends;
    private ImageView mimageView;
    private Button sentReq;
    private Button mDeclineBtn;
    private FirebaseUser mCurrent_User;


    private String currentFriendshipState;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");


        mCurrent_User = FirebaseAuth.getInstance().getCurrentUser();

        mDeclineBtn = (Button)findViewById(R.id.profile_decline_frnd_req);

        mdisplayName = (TextView) findViewById(R.id.profile_display_name);
        mCurrentStatus = (TextView) findViewById(R.id.profile_status);
        mTotalFriends = (TextView) findViewById(R.id.profile_total_froends);
        mimageView = (ImageView) findViewById(R.id.profile_image);
        sentReq = (Button)findViewById(R.id.profile_sendfrndreq_btn);

        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setEnabled(false);

        currentFriendshipState = "not_friends";

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String dispalyname = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mdisplayName.setText(dispalyname);
                mCurrentStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.profile1).into(mimageView);

                //---------Friends List or the Request Feature----------------//

                mFriendReqDatabase.child(mCurrent_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type.equalsIgnoreCase("received")){


                                currentFriendshipState = "req_received";
                                sentReq.setText("Accept friend Request");

                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);



                            }
                            else if(req_type.equalsIgnoreCase("sent")){

                                currentFriendshipState = "req_sent";
                                sentReq.setText("Cancel friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);


                            }
                            //maybe the person is already a friend
                            //this is not working at the moment

                            else{

                                mFriendDatabase.child(mCurrent_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.hasChild(user_id)){

                                            currentFriendshipState = "friends";
                                            sentReq.setText("Unfriend This Person");

                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }




                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sentReq.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                sentReq.setEnabled(false);

            if(currentFriendshipState.equalsIgnoreCase("not_friends")){

                mFriendReqDatabase.child(mCurrent_User.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            mFriendReqDatabase.child(user_id).child(mCurrent_User.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    HashMap<String,String> notificationData = new HashMap();
                                    notificationData.put("from",mCurrent_User.getUid());
                                    notificationData.put("type","request");


                                    mNotificationDatabase.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            sentReq.setEnabled(true);
                                            currentFriendshipState = "req_sent";
                                            sentReq.setText("Cancel friend Request");

                                            mDeclineBtn.setVisibility(View.INVISIBLE);
                                            mDeclineBtn.setEnabled(false);

                                            Toast.makeText(ProfileActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();

                                        }
                                    });



                                }
                            });

                        }
                        else {
                            Toast.makeText(ProfileActivity.this, "Failed Sending Request", Toast.LENGTH_SHORT).show();
                        }

                    }
                });



            }


            //for cancelling the sent friend request
                if(currentFriendshipState.equalsIgnoreCase("req_sent")){

                    mFriendReqDatabase.child(mCurrent_User.getUid()).child(user_id).removeValue().addOnSuccessListener
                            (new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendReqDatabase.child(user_id).child(mCurrent_User.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    sentReq.setEnabled(true);
                                    currentFriendshipState = "not_friends";
                                    sentReq.setText("Send friend Request");

                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);

                                }
                            });



                        }
                    });

                }

                // Request Receive State
                if(currentFriendshipState.equalsIgnoreCase("req_received")){
                    final String current_Date = java.text.DateFormat.getDateTimeInstance().format(new Date());
                    mFriendDatabase.child(mCurrent_User.getUid()).child(user_id).child("date").setValue(current_Date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendDatabase.child(user_id).child(mCurrent_User.getUid()).setValue(current_Date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    //copied from cancel friend request feature
                                    mFriendReqDatabase.child(mCurrent_User.getUid()).child(user_id).removeValue().addOnSuccessListener
                                            (new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mFriendReqDatabase.child(user_id).child(mCurrent_User.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            sentReq.setEnabled(true);
                                                            currentFriendshipState = "friends";
                                                            sentReq.setText("Unfriend This Person");

                                                            mDeclineBtn.setVisibility(View.INVISIBLE);
                                                            mDeclineBtn.setEnabled(false);

                                                        }
                                                    });



                                                }
                                            });




                                }
                            });

                        }
                    });





                }



            }
        });

     }
}
