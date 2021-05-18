package com.example.instagramclone.Share;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instagramclone.Profile.EditProfileActivity;
import com.example.instagramclone.R;
import com.example.instagramclone.Utils.FilePaths;
import com.example.instagramclone.Utils.ImageManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.app.Activity.RESULT_OK;

public class PhotoFragment extends Fragment {

    private static final int GALLERY_FRAGMENT_NUM = 0;
    private static final int PHOTO_FRAGMENT_NUM = 1;
    private static final int CAMERA_REQUEST_CODE = 5;
    private ProgressDialog dialog;

    //firebase stuffs
    DatabaseReference reference;
    StorageReference mStorageReference;
    FirebaseUser fuser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo,container,false);
        Button btnLaunchCamera = view.findViewById(R.id.btnLaunchCamera);
        //new
        dialog = new ProgressDialog(getActivity());
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((ShareActivity)getActivity()).getCurrentTabNumber() == PHOTO_FRAGMENT_NUM ) {
                    if (((ShareActivity)getActivity()).checkPermissions(Manifest.permission.CAMERA)) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE);
                    }
                }
            }
        });
        return view;
    }

    //to check whether it is ROOT TASK(i.e.from ShareActivity) or not
    private boolean isRootTask() {
        if (((ShareActivity)getActivity()).getTask() == 0) {
            return true;
        }else {
            return false;
        }
    }
    private void uploadProfilePicture(final Bitmap bitmap_photo) {
        FilePaths filePaths = new FilePaths();
        final StorageReference storageReference = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE+"/"+fuser.getUid()+"/profile_photo");
        byte[] bytes = ImageManager.getBytesFromBitmap(bitmap_photo,100);
        UploadTask uploadTask = null;
        uploadTask = storageReference.putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                /**
                 * After successfully uploading the profile_photo
                 *      1) Add data to Firebase Database (in User Account Settings)
                 */
                dialog.dismiss();
                Toast.makeText(getActivity(), "ProPic Uploaded Successfully !", Toast.LENGTH_SHORT).show();
                //add to the nodes in database
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri URI = uri;
                        String downloadURL = URI.toString();
                        //addPhotoToDatabase
                        reference.child("user_account_settings").child(fuser.getUid())
                                .child("profile_photo").setValue(downloadURL);
                        //navigating to edit profile
                        startActivity(new Intent(getActivity(), EditProfileActivity.class));
                        getActivity().finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Task Failed", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100 * snapshot.getBytesTransferred()) /snapshot.getTotalByteCount();
                dialog.setMessage("Uploading " + String.format("%.0f",progress) + " %");
                dialog.setCancelable(false);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setMax(100);
                dialog.show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK ) {

            Bitmap bitmap_photo = (Bitmap) data.getExtras().get("data");

            if (isRootTask()){
                Intent intent = new Intent(getActivity(),NextActivity.class);
                intent.putExtra("selectedBitmap",bitmap_photo);
                startActivity(intent);
            } else {
                //upload the pro_pic here itself
                try {
                    uploadProfilePicture(bitmap_photo);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
