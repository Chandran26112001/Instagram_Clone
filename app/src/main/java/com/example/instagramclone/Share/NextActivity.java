package com.example.instagramclone.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagramclone.Home.HomeActivity;
import com.example.instagramclone.R;
import com.example.instagramclone.Utils.FilePaths;
import com.example.instagramclone.Utils.ImageManager;
import com.example.instagramclone.Utils.StringManipulation;
import com.example.instagramclone.Utils.UniversalImageLoader;
import com.example.instagramclone.models.Photo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NextActivity extends AppCompatActivity {

    public ImageView image;
    public EditText description;
    private Intent intent;
    private String mSelectedImage;

    //Firebase Stuffs
    public int imageCount = 0;
    FirebaseUser fuser;
    DatabaseReference reference;
    private String imgURL;
    StorageReference mStorageReference;
    private double mPhotoUploadProgress=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        //firebase
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //getting the count of photos of this user
                for (DataSnapshot dataSnapshot : snapshot.child("user_photos").child(fuser.getUid()).getChildren()) {
                    imageCount++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //widgets
        image = findViewById(R.id.imageShare);
        description = findViewById(R.id.description);
        //setting small image
        setSmallImage();

        ImageView backArrow = findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView share = findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = getIntent();
                //upload the image to firebase
                Toast.makeText(NextActivity.this,"Uploading the Post ...",Toast.LENGTH_SHORT).show();
                if (intent.hasExtra("selectedImage")){
                    mSelectedImage = intent.getStringExtra("selectedImage");
                    uploadNewPhoto(description.getText().toString(),imageCount,mSelectedImage,null);
                }else if (intent.hasExtra("selectedBitmap")) {
                    Bitmap bitmap = intent.getParcelableExtra("selectedBitmap");
                    uploadNewPhoto(description.getText().toString(),imageCount,null,bitmap);
                }


            }
        });
    }

    private void setSmallImage() {
        intent = getIntent();
        if (intent.hasExtra("selectedImage")){
            mSelectedImage = intent.getStringExtra("selectedImage");
            UniversalImageLoader.setImage(mSelectedImage,image,null,"file:///");
        }else if (intent.hasExtra("selectedBitmap")) {
            Bitmap bitmap = intent.getParcelableExtra("selectedBitmap");
            image.setImageBitmap(bitmap);
        }
    }

    private void uploadNewPhoto(final String caption, int imageCount, String imgURL,Bitmap bm) {
        FilePaths filePaths = new FilePaths();
        final StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE+"/"+fuser.getUid()+"/photo"+(imageCount+1));
        if(bm == null){
            bm = ImageManager.getBitmap(imgURL);
        }
        byte[] bytes = ImageManager.getBytesFromBitmap(bm,100);
        UploadTask uploadTask = null;
        uploadTask = storageReference.putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                /**
                 * After successfully uploading the photo
                 *      1) Add data to Firebase Database
                 *      2) Navigate to Home Activity
                 */
                Toast.makeText(NextActivity.this, "Pic Uploaded Successfully !", Toast.LENGTH_SHORT).show();
                //add to the nodes in database
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri URI = uri;
                        String downloadURL = URI.toString();
                        addPhotoToDatabase(caption,downloadURL);
                        //Toast.makeText(NextActivity.this,downloadURL, Toast.LENGTH_SHORT).show();
                    }
                });
                //navigate to home activity
                startActivity(new Intent(NextActivity.this, HomeActivity.class));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NextActivity.this, "Task Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100 * snapshot.getBytesTransferred()) /snapshot.getTotalByteCount();
                if(progress-15 > mPhotoUploadProgress) {
                    Toast.makeText(NextActivity.this, "Upload Progress :" + String.format("%.0f",progress) + "%", Toast.LENGTH_LONG).show();
                    mPhotoUploadProgress = progress;
                }
            }
        });

    }

    private String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }

    private void addPhotoToDatabase(String caption,String url) {
        String newPhotoKey = reference.child("photos").push().getKey();

        String tags = StringManipulation.getTags(caption);
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimeStamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(fuser.getUid());
        photo.setPhoto_id(newPhotoKey);

        reference.child("photos").child(newPhotoKey).setValue(photo);
        reference.child("user_photos").child(fuser.getUid()).child(newPhotoKey).setValue(photo);

    }

    private void someMethod() {
        /***
         *      Step 1)
         *      create a data model for photos
         *      Step 2)
         *      add properties to the photos(captions,date,imageURL,photo_id,tags,user_id)
         *      Step 3)
         *      Count the number of photos that the user already has
         *      Step 4)
         *      Upload the data to Firebase Storage and Insert into the nodes in Firebase Database
         *          a) 'photos' node
         *          b) 'user_photos' node
         */
    }

}
