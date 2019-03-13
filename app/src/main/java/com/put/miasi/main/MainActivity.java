package com.put.miasi.main;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.put.miasi.R;
import com.put.miasi.main.profile.EditProfileActivity;
import com.put.miasi.utils.CurrentUserProfile;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.LocationUtils;
import com.put.miasi.utils.Notification;
import com.put.miasi.utils.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // private ActionBar mToolbar;
    private Toolbar mToolbar;

    private List<Notification> mNotifications;

    private FirebaseAuth mAuth;
    private String mUserUid;
    private DatabaseReference mRootRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mNotificationsRef;

    private BottomNavigationView mNavigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_history:
                    mToolbar.setTitle(getString(R.string.title_history));
                    resetNavIcon();
                    fragment = new HistoryFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_rides:
                    mToolbar.setTitle(getString(R.string.title_rides));
                    resetNavIcon();
                    fragment = new RidesFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_notifications:
                    mToolbar.setTitle(getString(R.string.title_notifications));
                    resetNavIcon();
                    fragment = new NotificationFragment();
                    //((NotificationFragment) fragment).setNotificationList(mNotifications);
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_options:
                    mToolbar.setTitle(getString(R.string.title_options));
                    mToolbar.setNavigationIcon(R.drawable.ic_edit_white_24dp);
                    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                            startActivity(intent);
                        }
                    });
                    fragment = new OptionsFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void resetNavIcon() {
        mToolbar.setNavigationIcon(null);
        mToolbar.setNavigationOnClickListener(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // mToolbar = getSupportActionBar();

        Log.d(TAG, "onCreate: ");
        
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.title_rides));

        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mNavigation.setSelectedItemId(R.id.navigation_rides);

        mAuth = FirebaseAuth.getInstance();
        mUserUid = mAuth.getCurrentUser().getUid();
        CurrentUserProfile.uid = mUserUid;
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mRootRef.child(Database.USERS);
        mNotificationsRef = mRootRef.child(Database.NOTIFICATIONS);

        mNotifications = new ArrayList<>();

        loadFragment(new RidesFragment());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        getUserProfile();
        getNotifications();
        Log.d(TAG, "onStart: ");
    }

    private void getNotifications() {
        // HashMap<String, Boolean> notificationsMap = CurrentUserProfile.notificationsMap;
        DatabaseReference userNotificationsRef = mNotificationsRef.child(mUserUid);

        ValueEventListener userNotificationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Notification notification = ds.getValue(Notification.class);
                    notification.setNotificationUid(ds.getKey());
                    Log.d(TAG, "onDataChange: " + notification.toString());
                    mNotifications.add(notification);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        userNotificationsRef.addListenerForSingleValueEvent(userNotificationListener);
    }

    private void getUserProfile() {
        final DatabaseReference usersRef = mUsersRef.child(mUserUid);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                CurrentUserProfile.loadUserData(mUserUid, user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        usersRef.addListenerForSingleValueEvent(userListener);
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);

        if (mNavigation.getSelectedItemId() == R.id.navigation_history
                || mNavigation.getSelectedItemId() == R.id.navigation_rides
                || mNavigation.getSelectedItemId() == R.id.navigation_notifications) {
            MenuItem item = menu.findItem(R.id.logout_menu_item);
            item.setVisible(false);
        }
        return true;
    }
}
