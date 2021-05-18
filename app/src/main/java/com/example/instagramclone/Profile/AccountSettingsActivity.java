package com.example.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.instagramclone.R;
import com.example.instagramclone.Utils.BottomNavigationViewHelper;
import com.example.instagramclone.Utils.SectionsStatePagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class AccountSettingsActivity extends AppCompatActivity {

    private Context mContext;
    private SectionsStatePagerAdapter pagerAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;
    private static final int ACTIVITY_NUM = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);
        //for navigation
        Intent intent = getIntent();

//        if(intent.hasExtra("return_to_fragment")) {
//            //setViewPager(pagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
//            Toast.makeText(mContext, "haiiiiiiiiiiiiii", Toast.LENGTH_SHORT).show();
//        }
        getIncomingIntent();
        mContext = AccountSettingsActivity.this;
        mViewPager = (ViewPager) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayout_1);
        //for bottom navigation view
        setupBottomNavigationView();
        //setting up the fragments
        setupFragments();
        //setting up the list of settings
        setupSettingsList();
        //setting up back action
        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    //navigating to edit profile from ProfileActivity.java directly to EditProfileFragment.java


      private void getIncomingIntent() {

     }


    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigaionView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
    //for setting up the fragments for edit and sign out
    private void setupFragments() {
        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new EditProfileFragment(),getString(R.string.edit_profile_fragment)); //fragment 0
        pagerAdapter.addFragment(new SignOutFragment(),getString(R.string.sign_out_fragment)); //fragment 1
    }
    //setting the view pager specifically when clicked on that item
    private void setViewPager(int fragmentNumber) {
        mRelativeLayout.setVisibility(View.GONE);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(fragmentNumber);
    }
    private void setupSettingsList() {
        ListView listView = findViewById(R.id.lvAccountSettings);

        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile_fragment)); //fragment 0
        options.add(getString(R.string.sign_out_fragment)); //fragment 1

        ArrayAdapter adapter = new ArrayAdapter(mContext,android.R.layout.simple_list_item_1,options);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //setViewPager(i);
                if (i==0) {
                    startActivity(new Intent(AccountSettingsActivity.this,EditProfileActivity.class));
                }
            }
        });
    }
}
