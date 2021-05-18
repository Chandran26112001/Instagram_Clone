package com.example.instagramclone.Share;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.instagramclone.Profile.AccountSettingsActivity;
import com.example.instagramclone.Profile.EditProfileActivity;
import com.example.instagramclone.Profile.EditProfileFragment;
import com.example.instagramclone.R;
import com.example.instagramclone.Utils.FilePaths;
import com.example.instagramclone.Utils.FileSearch;
import com.example.instagramclone.Utils.GridImageAdapter;
import com.example.instagramclone.Utils.ImageManager;
import com.example.instagramclone.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    //Widgets
    private GridView gridView;
    private ImageView galleryImageView;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;

    private ArrayList<String> directories;
    private String mSelectedImage;
    DatabaseReference reference;
    FirebaseUser fuser;
    StorageReference mStorageReference;
    private double mPhotoUploadProgress=0;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery,container,false);
        gridView = view.findViewById(R.id.gridView);
        galleryImageView = view.findViewById(R.id.galleryImageView);
        mProgressBar = view.findViewById(R.id.progressBar);
        directorySpinner = view.findViewById(R.id.spinnerDirectory);
        mProgressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();
        dialog = new ProgressDialog(getActivity());
        //firebase stuffs
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();



        ImageView shareClose = view.findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        TextView nextScreen = view.findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //logic based on where we from because in edit profile also we need to come here
                if (isRootTask()){
                    Intent intent = new Intent(getActivity(),NextActivity.class);
                    intent.putExtra("selectedImage",mSelectedImage);
                    startActivity(intent);
                } else {
                    //upload the pro_pic here itself
                    uploadProfilePicture(mSelectedImage);
//                    Intent intent = new Intent(getActivity(), EditProfileActivity.class);
//                    intent.putExtra("selectedImage",mSelectedImage);
//                    intent.putExtra("return_to_fragment","Edit Profile");
//                    startActivity(intent);
                }

            }
        });

        init();

        return view;
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
                //setting propic in the ImageView
                //UniversalImageLoader.setImage(mSelectedImage,profile_photo,null,"file:///");
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
//                if(progress-15 > mPhotoUploadProgress) {
//                    Toast.makeText(getActivity(), "Upload Progress :" + String.format("%.0f",progress) + "%", Toast.LENGTH_LONG).show();
//                    mPhotoUploadProgress = progress;
//                }
                dialog.setMessage("Uploading " + String.format("%.0f",progress) + " %");
                dialog.setCancelable(false);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setMax(100);
                dialog.show();

            }
        });
    }
    //to check whether it is ROOT TASK(i.e.from ShareActivity) or not
    private boolean isRootTask() {
        if (((ShareActivity)getActivity()).getTask() == 0) {
            return true;
        }else {
            return false;
        }
    }
    private void init() {

        FilePaths filePaths = new FilePaths();

        //check for other directories in storage/emulated/pictures
        if(FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }
        //want to add the directories inside the ROOT_DIR
        directories.add(filePaths.CAMERA);
        directories.add(filePaths.WHATSAPP);

        ArrayList<String> newDirectories = new ArrayList<>();

        for(int i=0;i<directories.size();i++) {
            String temp =directories.get(i).replace("/storage/emulated/0/","");
            newDirectories.add(temp);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,newDirectories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);
        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setupGridView(directories.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    //for checking whether it is image file
    private boolean isImageFile(String filePath) {
        if(filePath.endsWith(".jpg") || filePath.endsWith(".png")) {
            return true;
        }
        return false;
    }
    public void setupGridView(String selectedDirectory) {
        //my code
        final ArrayList<String> imgURLs = new ArrayList<>();
        File[] files = new File(selectedDirectory).listFiles();
        for (File file: files) {
            if (file.isFile() && isImageFile(file.getPath())) {
                imgURLs.add(file.getPath());
            }
        }
        //final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/3;
        gridView.setColumnWidth(imageWidth);
        try{
            GridImageAdapter adapter = new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,"file:///",imgURLs);
            gridView.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "something", Toast.LENGTH_SHORT).show();
        }
        //initially we should display the first image in any directory
        try {
            UniversalImageLoader.setImage(imgURLs.get(0),galleryImageView,mProgressBar,"file:///");
            mSelectedImage = imgURLs.get(0);
        } catch (Exception e){
            Toast.makeText(getActivity(), "No Images In This Path", Toast.LENGTH_SHORT).show();
        }
        //on CLick
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UniversalImageLoader.setImage(imgURLs.get(i),galleryImageView,mProgressBar,"file:///");
                mSelectedImage = imgURLs.get(i);
            }
        });
    }

}
