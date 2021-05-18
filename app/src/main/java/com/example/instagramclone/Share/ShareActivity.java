package com.example.instagramclone.Share;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.instagramclone.R;
import com.example.instagramclone.Utils.BottomNavigationViewHelper;
import com.example.instagramclone.Utils.Permissions;
import com.example.instagramclone.Utils.SectionsPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class ShareActivity extends AppCompatActivity {

    private Context mContext = ShareActivity.this;
    //constants
    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        //checking necessary permissions
        if(checkPermissionsArray(Permissions.PERMISSIONS)) {
            setupViewPager();
        } else {
            verifyPermissions(Permissions.PERMISSIONS);
            if(checkPermissionsArray(Permissions.PERMISSIONS)) {
                setupViewPager();
//                Toast.makeText(mContext, "GRANT ACCESS !", Toast.LENGTH_SHORT).show();
//                finish();
            }
        }

        //setupBottomNavigationView();
    }

    //get Task Number
    public int getTask() {
        return getIntent().getFlags();
    }

    //get current tab number
    public int getCurrentTabNumber() {
        return mViewPager.getCurrentItem();
    }

    //setting up the view pager
    public void setupViewPager() {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tab_share);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setText("GALLERY");
        tabLayout.getTabAt(1).setText("PHOTO");
    }
    //check for all permissions
    public boolean checkPermissionsArray(String[] permissions) {
        for(int i=0;i<permissions.length;i++) {
            if(!checkPermissions(permissions[i])) {
                return false;
            }
        }
        return true;
    }

    //check for individual permissions
    public boolean checkPermissions(String permission) {
        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this,permission);
        if(permissionRequest != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    //verify the permissions
    public void verifyPermissions(String[] permissions) {
        ActivityCompat.requestPermissions(ShareActivity.this,permissions,VERIFY_PERMISSIONS_REQUEST);
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigaionView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}