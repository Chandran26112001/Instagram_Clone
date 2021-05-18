package com.example.instagramclone.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.instagramclone.Home.HomeActivity;
import com.example.instagramclone.Likes.LikesActivity;
import com.example.instagramclone.Profile.ProfileActivity;
import com.example.instagramclone.R;
import com.example.instagramclone.Search.SearchActivity;
import com.example.instagramclone.Share.ShareActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationViewHelper {
    public static void setupBottomNavigaionView(BottomNavigationView bottomNavigationView) {

    }
    public static void enableNavigation(final Context context, final Activity callingActivity, BottomNavigationView view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.ic_house:
                        Intent i = new Intent(context, HomeActivity.class);
                        context.startActivity(i);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                    case R.id.ic_android:
                        Intent i2 = new Intent(context, ProfileActivity.class);
                        context.startActivity(i2);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                    case R.id.ic_search:
                        Intent i3 = new Intent(context, SearchActivity.class);
                        context.startActivity(i3);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        break;
                    case R.id.ic_circle:
                        Intent i4 = new Intent(context, ShareActivity.class);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        context.startActivity(i4);
                        break;
                    case R.id.ic_alert:
                        Intent i5 = new Intent(context, LikesActivity.class);
                        callingActivity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                        context.startActivity(i5);
                        break;
                }
                return false;
            }
        });
        //view.setOnNavigationItemReselectedListener(null);
    }
}
