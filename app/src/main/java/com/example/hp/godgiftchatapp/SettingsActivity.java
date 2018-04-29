package com.example.hp.godgiftchatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.R.attr.bitmap;
import static com.theartofdev.edmodo.cropper.R.styleable.CropImageView;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    private CircleImageView mImage;
    private TextView mName;
    private TextView mStatus;
    private Button mChangeStatus;
    private Button mChangeImage;
    private final static int GALLERY_PICK = 1;
    private StorageReference mStorageReference;
    public byte[] thumb_byte;

    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mImage = (CircleImageView) findViewById(R.id.settings_image);
        mName = (TextView)findViewById(R.id.setting_display_name);
        mStatus = (TextView) findViewById(R.id.Settings_status);
        mChangeStatus = (Button)findViewById(R.id.Settings_change_status);
        mChangeImage = (Button)findViewById(R.id.Settings_change_image);
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        //activating firebase offline capabilities for the database object below
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
               final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumbnail = dataSnapshot.child("thumbnail").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
                if(!image.equals("default"))
                {
                   // Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.profile1).into(mImage);

                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.profile1).into(mImage, new Callback() {
                    @Override
                    public void onSuccess() {


                        Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.profile1).into(mImage);
                    }

                    @Override
                    public void onError() {

                    }
                });
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status_string = mStatus.getText().toString();

                Intent changestatusintent = new Intent(SettingsActivity.this,StatusActivity.class);
                changestatusintent.putExtra("status_string",status_string);
                startActivity(changestatusintent);
                finish();
            }
        });


        mChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // start picker to get image for cropping and then use the image in cropping activity

                CropImage.activity()
                        .setGuidelines(com.theartofdev.edmodo.cropper.CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);


                /*Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(galleryIntent.ACTION_GET_CONTENT);
                startActivityForResult(galleryIntent.createChooser(galleryIntent,"Select Image"),GALLERY_PICK);*/




            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgress = new ProgressDialog(SettingsActivity.this);
                mProgress.setTitle("Uploading Image..");
                mProgress.setMessage("Please wait while we upload and process your Image");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();


                Uri resultUri = result.getUri();//we get the result as Uri
                File thumb_file_path = new File(resultUri.getPath());



               /* CropImage.activity(resultUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);*/
               //Image name to be the userId for easy retrieval
                String currentUserId = mCurrentUser.getUid();

                try {
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(40)
                            .compressToBitmap(thumb_file_path);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                      thumb_byte = baos.toByteArray();




                } catch (IOException e) {
                    e.printStackTrace();
                }


                StorageReference filepath = mStorageReference.child("profile_images").child(currentUserId+".jpg");
                final StorageReference thumb_filepath = mStorageReference.child("profile_images").child("thumbs").child(currentUserId+".jpg");




                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            final String download_url = task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_download_url = thumb_task.getResult().getDownloadUrl().toString();
                                    
                                    if(thumb_task.isSuccessful()){

                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put("image",thumb_download_url);
                                        update_hashMap.put("thumbnail",thumb_download_url);

                                        mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                mProgress.dismiss();
                                                Toast.makeText(SettingsActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();

                                            }
                                        });


                                        
                                    }
                                    else{
                                        Toast.makeText(SettingsActivity.this, "Error in uploading thumbnail", Toast.LENGTH_SHORT).show();
                                        mProgress.dismiss();
                                    }
                                }
                            });





                        }

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
