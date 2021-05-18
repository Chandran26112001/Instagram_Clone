package com.example.instagramclone.Profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instagramclone.Home.HomeActivity;
import com.example.instagramclone.R;
import com.example.instagramclone.Share.NextActivity;
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
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Field;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {
    public static int a=1;
    private ImageView backArrow,saveChanges;
    private TextView changeProfilePhoto;
    private CircleImageView profile_photo;
    private EditText username,display_name,website,description,email,phoneNumber;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    StorageReference mStorageReference;
    private double mPhotoUploadProgress=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile,container,false);

        username = (EditText) view.findViewById(R.id.username);
        display_name = (EditText) view.findViewById(R.id.display_name);
        website = (EditText) view.findViewById(R.id.website);
        description = (EditText) view.findViewById(R.id.description);
        email = (EditText) view.findViewById(R.id.email);
        phoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
        profile_photo = (CircleImageView) view.findViewById(R.id.profile_photo);
        changeProfilePhoto = (TextView) view.findViewById(R.id.changeProfilePhoto);

        //setProfileImage();
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
                //settings.getProfile_photo() change this
                UniversalImageLoader.setImage(null,profile_photo,null,"https://");
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
        final String mSelectedImage = getActivity().getIntent().getStringExtra("selectedImage");
        //changing ProPic
        changeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ShareActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //above flag has a particular number which is catched by the getIntent().getFlags() in ShareActivity
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
        //navigating back to profile activity
        backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        //save changes
        saveChanges = (ImageView) view.findViewById(R.id.saveChanges);
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

                Toast.makeText(getActivity(), "Saved Changes Successfully !", Toast.LENGTH_SHORT).show();
//                if(a==1) {
//                    getActivity().finish();
//                }
                getActivity().finish();
            }
        });
        return view;
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
                    Toast.makeText(getActivity(), "Username Saved !", Toast.LENGTH_SHORT).show();
                }else {
                    a=0;
                    Toast.makeText(getActivity(), "Username Already Exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    /**
     *  private void setProfileImage() {
     *         String imgURL = "www.thenewsminute.com/sites/default/files/styles/news_detail/public/Sangathamizhan_VijaySethupathi_Teaser_screenshot.jpg?itok=79d1YeLz";
     *         UniversalImageLoader.setImage(imgURL,mProfilePhoto,null,"https://");
     *     }
     */


}
