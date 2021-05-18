package com.example.instagramclone.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.instagramclone.Login.LoginActivity;
import com.example.instagramclone.R;
import com.example.instagramclone.Utils.BottomNavigationViewHelper;
import com.example.instagramclone.Utils.SectionsPagerAdapter;
import com.example.instagramclone.Utils.UniversalImageLoader;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeActivity extends AppCompatActivity {

    private Context mContext = HomeActivity.this;
    private static final int ACTIVITY_NUM = 0;
    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    //private FirebaseAuth.AuthStateListener mAuthListener;

    /**
     * every time app starts checks whether user is signed in .....if yes continues...else takes to Login Page
     * @Override
     *     protected void onStart() {
     *         super.onStart();
     *         firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
     *         if(firebaseUser == null) {
     *             startActivity(new Intent(mContext,LoginActivity.class));
     *         }
     *     }
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //FireBase Contents


        //normal contents
        initImageLoader();
        setupBottomNavigationView();
        setupViewPager();
    }
    //the bottom navigation view
    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigaionView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
    //for three fragments in Home Activity
    private void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment()); //index 0
        adapter.addFragment(new HomeFragment()); //index 1
        adapter.addFragment(new MessagesFragment()); //index 2
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(adapter);
        //for setting with the tab layout in the top
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_action_name);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_arrow);
    }
    //initializing the image loader
    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    /*
    * --------------------------------------FIREBASE STUFFS----------------------------------------*/


}