package com.example.hp.godgiftchatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUserList;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = (Toolbar) findViewById(R.id.all_user_bar);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUserList = (RecyclerView) findViewById(R.id.Users_List);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(Users.class,R.layout.users_single_layout,UsersViewHolder.class,mUserDatabase) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {

               final String user_id = getRef(position).getKey();

                //Toast.makeText(UsersActivity.this, model.toString(), Toast.LENGTH_SHORT).show();
               /* Log.d("model","1" + model.getImage()
                + "2" + model.getStatus() + "3" + model.getName() + "4" + model.getThumb_image() );*/
                viewHolder.setname(model.getName());
                viewHolder.setstatus(model.getStatus());
                viewHolder.set_image_for_thumb(model.getImage(),getApplicationContext());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    Intent profileIntent = new Intent(UsersActivity.this,ProfileActivity.class);
                        profileIntent.putExtra("user_id",user_id);
                        startActivity(profileIntent);

                    }
                });


            }
        };
        mUserList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class UsersViewHolder extends  RecyclerView.ViewHolder{

        View mView;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;



        }
        public void set_image_for_thumb(String thumb, Context ctx){

            CircleImageView userthumbnail = (CircleImageView)mView.findViewById(R.id.users_single_image);
            // Toast.makeText(ctx, thumb, Toast.LENGTH_SHORT).show(); //coming null
            Picasso.with(ctx).load(thumb).placeholder(R.drawable.profile1).into(userthumbnail);

        }


       /* public void sethumb_image(String thumb, Context ctx){

            CircleImageView userthumbnail = (CircleImageView)mView.findViewById(R.id.users_single_image);
           // Toast.makeText(ctx, thumb, Toast.LENGTH_SHORT).show(); //coming null
            Picasso.with(ctx).load(thumb).placeholder(R.drawable.profile1).into(userthumbnail);

        }*/

        public void setname(String name){

            TextView musersingledisplayname = (TextView)mView.findViewById(R.id.users_single_name);
            musersingledisplayname.setText(name);

        }
        public void setstatus(String status){

            TextView musersinglestatus = (TextView)mView.findViewById(R.id.users_single_status);
            musersinglestatus.setText(status);

        }

    }
}
