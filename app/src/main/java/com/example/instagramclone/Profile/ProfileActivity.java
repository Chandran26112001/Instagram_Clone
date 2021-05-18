package com.example.instagramclone.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagramclone.R;
import com.example.instagramclone.Utils.BottomNavigationViewHelper;
import com.example.instagramclone.Utils.GridImageAdapter;
import com.example.instagramclone.Utils.UniversalImageLoader;
import com.example.instagramclone.models.Photo;
import com.example.instagramclone.models.UserAccountSettings;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Context mContext = ProfileActivity.this;
    private static final int ACTIVITY_NUM = 4;
    private ProgressBar mProgressBar;
    private CircleImageView profilePhoto;
    private TextView profileName,display_name,description,website,tvPosts,tvFollowers,tvFollowing,textEditProfile;


    private FirebaseUser fuser;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        //initializing the widgets
        profileName = findViewById(R.id.profileName);
        display_name = findViewById(R.id.display_name);
        description = findViewById(R.id.description);
        website = findViewById(R.id.website);
        tvPosts = findViewById(R.id.tvPosts);
        tvFollowers = findViewById(R.id.tvFollowers);
        tvFollowing = findViewById(R.id.tvFollowing);
        profilePhoto = findViewById(R.id.profile_photo);

        setupBottomNavigationView();
        setupToolbar();
        setupActivityWidgets();
        tempGridSetup();

        //navigating to edit profile fragment
        textEditProfile = findViewById(R.id.textEditProfile);
        textEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
                //dialog.show(getSupportFragmentManager(),"Just Checking !");
                startActivity(new Intent(ProfileActivity.this,EditProfileActivity.class));
            }
        });

        //firebase

        reference.child("user_account_settings").child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccountSettings settings = snapshot.getValue(UserAccountSettings.class);
                assert settings != null;
                setProfileWidgets(settings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //setting up the profile widgets from database
    private void setProfileWidgets(UserAccountSettings userAccountSettings) {
        profileName.setText(userAccountSettings.getUsername());
        display_name.setText(userAccountSettings.getDisplay_name());
        description.setText(userAccountSettings.getDescription());
        website.setText(userAccountSettings.getWebsite());
        tvPosts.setText(String.valueOf(userAccountSettings.getPosts()));
        tvFollowers.setText(String.valueOf(userAccountSettings.getFollowers()));
        tvFollowing.setText(String.valueOf(userAccountSettings.getFollowing()));
        UniversalImageLoader.setImage(userAccountSettings.getProfile_photo(),profilePhoto,mProgressBar,"");
    }
    //this is temporary
    private void tempGridSetup() {
        final ArrayList<Photo> photos = new ArrayList<>();
        final ArrayList<String> imgURLs = new ArrayList<>();
        Query query = reference.child("user_photos").child(fuser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    photos.add(dataSnapshot.getValue(Photo.class));
                }
                for (int i=0;i<photos.size();i++) {
                    imgURLs.add(photos.get(i).getImage_path());
                }
                //setting up the grid view
                GridView gridView = findViewById(R.id.gridView);
                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWidth/3;
                gridView.setColumnWidth(imageWidth);
                GridImageAdapter adapter =  new GridImageAdapter(mContext,R.layout.layout_grid_imageview,"",imgURLs);
                gridView.setAdapter(adapter);
                //enabling the navigation from here to Photo Activity
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Bundle args = new Bundle();
                        args.putParcelable("photoDetails",photos.get(i));
                        args.putInt("activityNumber",ACTIVITY_NUM);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Error Loading Images", Toast.LENGTH_SHORT).show();
            }
        });

    }
    //set Grid View
    private void setupImageGrid(ArrayList<String> imgURLs) {
        GridView gridView = (GridView) findViewById(R.id.gridView);
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/3;
        gridView.setColumnWidth(imageWidth);
        GridImageAdapter adapter =  new GridImageAdapter(mContext,R.layout.layout_grid_imageview,"",imgURLs);
        gridView.setAdapter(adapter);
    }

    //setting up the widgets
    private void setupActivityWidgets(){
        mProgressBar = findViewById(R.id.profileProgressBar);
        mProgressBar.setVisibility(View.GONE);
        profilePhoto = findViewById(R.id.profile_photo);
    }
    //setting up the toolbar with the menu using toolbar.setOnMenuItemClickListener
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolBar);
        setSupportActionBar(toolbar);
        ImageView profileMenu = (ImageView) findViewById(R.id.profileMenu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,AccountSettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    //no need of menu in toolbar as we have image button

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigaionView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}