package com.example.instagramclone.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagramclone.R;
import com.example.instagramclone.Share.ShareActivity;
import com.example.instagramclone.Utils.FilePaths;
import com.example.instagramclone.Utils.ImageManager;
import com.example.instagramclone.Utils.UniversalImageLoader;
import com.example.instagramclone.models.User;
import com.example.instagramclone.models.UserAccountSettings;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    public static int a=1;
    private ImageView backArrow,saveChanges;
    private TextView changeProfilePhoto;
    private CircleImageView profile_photo;
    private EditText username,display_name,website,description,email,phoneNumber;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    StorageReference mStorageReference;
    private double mPhotoUploadProgress=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //getting imgURL from previous activity
        final String mSelectedImage = getIntent().getStringExtra("selectedImage");
        //UniversalImageLoader.setImage(mSelectedImage,profile_photo,null,"file:///");

        username = findViewById(R.id.username);
        display_name = findViewById(R.id.display_name);
        website = findViewById(R.id.website);
        description = findViewById(R.id.description);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);
        profile_photo = findViewById(R.id.profile_photo);
        changeProfilePhoto = findViewById(R.id.changeProfilePhoto);

        Intent intent = getIntent();
        if (intent.hasExtra("return_to_fragment")) {
            UniversalImageLoader.setImage(mSelectedImage,profile_photo,null,"file:///");
        }

        //firebase
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        //setting up the exixting data
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("user_account_settings").child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccountSettings settings = snapshot.getValue(UserAccountSettings.class);
                username.setText(settings.getUsername());
                display_name.setText(settings.getDisplay_name());
                website.setText(settings.getWebsite());
                description.setText(settings.getDescription());
                String pro_pic = settings.getProfile_photo();
                UniversalImageLoader.setImage(pro_pic,profile_photo,null,"");
                //Toast.makeText(EditProfileActivity.this, pro_pic, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.child("users").child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User mUser = snapshot.getValue(User.class);
                email.setText(mUser.getEmail());
                phoneNumber.setText(mUser.getPhone_number());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //changing ProPic
        changeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProfileActivity.this, ShareActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //above flag has a particular number which is catched by the getIntent().getFlags() in ShareActivity
                startActivity(intent);
                finish();
            }
        });
        //navigating back to profile activity
        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //save changes
        saveChanges = findViewById(R.id.saveChanges);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Object> hashMap1 = new HashMap<>();
                //hashMap1.put("username",username.getText().toString());
                hashMap1.put("display_name",display_name.getText().toString());
                hashMap1.put("website",website.getText().toString());
                hashMap1.put("description",description.getText().toString());
                reference.child("user_account_settings").child(fuser.getUid()).updateChildren(hashMap1);

                //now check whether the username already exists,,,
                checkIfUsernameExists(username.getText().toString());

                HashMap<String,Object> hashMap2 = new HashMap<>();
                hashMap2.put("email",email.getText().toString());
                hashMap2.put("phone_number",phoneNumber.getText().toString());
                reference.child("users").child(fuser.getUid()).updateChildren(hashMap2);

                //uploading the pro pic
                //uploadProfilePicture(mSelectedImage);

                Toast.makeText(EditProfileActivity.this, "Saved Changes Successfully !", Toast.LENGTH_SHORT).show();
//                if(a==1) {
//                    getActivity().finish();
//                }
                finish();
            }
        });

    }

    private void uploadProfilePicture(final String mSelectedImage) {
        FilePaths filePaths = new FilePaths();
        final StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE+"/"+fuser.getUid()+"/profile_photo");
        Bitmap bm = ImageManager.getBitmap(mSelectedImage);
        byte[] bytes = ImageManager.getBytesFromBitmap(bm,100);
        UploadTask uploadTask = null;
        uploadTask = storageReference.putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                /**
                 * After successfully uploading the profile_photo
                 *      1) Add data to Firebase Database (in User Account Settings)
                 */
                Toast.makeText(EditProfileActivity.this, "ProPic Uploaded Successfully !", Toast.LENGTH_SHORT).show();
                //add to the nodes in database
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri URI = uri;
                        String downloadURL = URI.toString();
                        //addPhotoToDatabase
                        reference.child("user_account_settings").child(fuser.getUid())
                                .child("profile_photo").setValue(downloadURL);
                    }
                });
                //setting propic in the ImageView
                UniversalImageLoader.setImage(mSelectedImage,profile_photo,null,"file:///");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Task Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100 * snapshot.getBytesTransferred()) /snapshot.getTotalByteCount();
                if(progress-15 > mPhotoUploadProgress) {
                    Toast.makeText(EditProfileActivity.this, "Upload Progress :" + String.format("%.0f",progress) + "%", Toast.LENGTH_LONG).show();
                    mPhotoUploadProgress = progress;
                }
            }
        });
    }

    private void checkIfUsernameExists(final String username) {
        reference = FirebaseDatabase.getInstance().getReference();
        Query mQuery = reference.child("user_account_settings")
                .orderByChild("username")
                .equalTo(username);
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    a=1;
                    reference.child("user_account_settings")
                            .child(fuser.getUid())
                            .child("username")
                            .setValue(username);
                    Toast.makeText(EditProfileActivity.this, "Username Saved !", Toast.LENGTH_SHORT).show();
                }else {
                    a=0;
                    Toast.makeText(EditProfileActivity.this, "Username Already Exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}