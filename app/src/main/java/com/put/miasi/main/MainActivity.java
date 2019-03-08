package com.put.miasi.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.put.miasi.R;
import com.put.miasi.utils.CurrentUserProfile;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.OfferLog;
import com.put.miasi.utils.User;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // private ActionBar mToolbar;
    private Toolbar mToolbar;

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
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_options:
                    mToolbar.setTitle(getString(R.string.title_options));
                    mToolbar.setNavigationIcon(R.drawable.ic_edit_white_24dp);
                    mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "dasdas", Toast.LENGTH_SHORT).show();
                            // Intent intent = new Intent(MainActivity.this, ProfileDetailsActivity.class);
                            // startActivity(intent);
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

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.title_rides));

        mNavigation = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mNavigation.setSelectedItemId(R.id.navigation_rides);

        loadFragment(new RidesFragment());
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserProfile();
    }

    private void getUserProfile() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String userUid = auth.getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference usersRef = database.getReference(Database.USERS).child(userUid);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                OfferLog.d(dataSnapshot.toString());
                User user = dataSnapshot.getValue(User.class);
                OfferLog.d(user.toString());
                CurrentUserProfile.loadUserData(userUid, user);
                OfferLog.d(CurrentUserProfile.toStringy());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        usersRef.addListenerForSingleValueEvent(postListener);
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
            MenuItem item = menu.findItem(R.id.settings_menu_item);
            item.setVisible(false);
        }
        return true;
    }
}
