package com.put.miasi.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.NavLog;
import com.put.miasi.utils.OfferLog;
import com.put.miasi.utils.RideOffer;
import com.put.miasi.utils.User;
import com.put.miasi.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

        NavLog.d("MainAct onStart");

        tests();
    }

    private void tests() {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        // Rides

        final DatabaseReference offeredRidesRef = database.child(Database.RIDES);

        final List<RideOffer> rideOffers = new ArrayList<>();

        ValueEventListener ridesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                OfferLog.d(dataSnapshot.toString());
                for (DataSnapshot ds :dataSnapshot.getChildren()) {
                    RideOffer rideOffer = ds.getValue(RideOffer.class);
                    rideOffer.setKey(ds.getKey());
                    OfferLog.d(rideOffer.toString());
                    rideOffers.add(rideOffer);
                    OfferLog.d(String.valueOf(rideOffers.size()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        offeredRidesRef.addListenerForSingleValueEvent(ridesListener);

        // Users

        final DatabaseReference usersRef = database.child(Database.USERS);

        // Konkretny uzytkownik o userUid
        // final DatabaseReference usersRef = database.child(Database.USERS).child(userUid);

        final List<User> users = new ArrayList<>();

        ValueEventListener usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                OfferLog.d(dataSnapshot.toString());
                for (DataSnapshot ds :dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    user.setUid(ds.getKey());
                    OfferLog.d(user.toString());
                    users.add(user);
                    OfferLog.d(String.valueOf(users.size()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        usersRef.addListenerForSingleValueEvent(usersListener);

        DateUtils.getStringDurationFromLongSeconds(10993);

        String s = DateUtils.getStringDistanceFromLongMeters(310291);
        Log.d("qwerty", "getStringDistanceFromLongMeters=" + s);

        Calendar cl = Calendar.getInstance();

        cl.add(Calendar.DAY_OF_MONTH, 4);
        OfferLog.d("Main", String.valueOf(cl.getTime().getTime()));

        cl.add(Calendar.DAY_OF_MONTH, 5);
        OfferLog.d("Main", String.valueOf(cl.getTime().getTime()));

        cl.add(Calendar.MONTH, 1);
        cl.add(Calendar.DAY_OF_MONTH, 10);
        OfferLog.d("Main", String.valueOf(cl.getTime().getTime()));

        cl.add(Calendar.MONTH, 1);
        cl.add(Calendar.DAY_OF_MONTH, 2);
        OfferLog.d("Main", String.valueOf(cl.getTime().getTime()));


    }

    private void getUserProfile() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String userUid = auth.getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference usersRef = database.getReference(Database.USERS).child(userUid);

        ValueEventListener userListener = new ValueEventListener() {
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
