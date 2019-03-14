package com.put.miasi.main;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.put.miasi.R;
import com.put.miasi.main.notifications.NotificationService;
import com.put.miasi.main.profile.EditProfileActivity;
import com.put.miasi.utils.CurrentUserProfile;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.LocationUtils;
import com.put.miasi.utils.NotificationUtils;
import com.put.miasi.utils.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "EntryActivity";

    // private ActionBar mToolbar;
    private Toolbar mToolbar;

    //private List<Notification> mNotifications;

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
        // mNavigation.setSelectedItemId(R.id.navigation_rides);

        mAuth = FirebaseAuth.getInstance();
        mUserUid = mAuth.getCurrentUser().getUid();
        CurrentUserProfile.uid = mUserUid;
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mRootRef.child(Database.USERS);
        mNotificationsRef = mRootRef.child(Database.NOTIFICATIONS);

        String menuFragment = getIntent().getStringExtra("a");
        // Log.d("Halo", "onCreate: " + getIntent());
        // Log.d("Halo", "onCreate: " + getIntent().getExtras().getString("a"));
        // Log.d("Halo", "onCreate: " + getIntent().getExtras().keySet().size());
        // Log.d("Halo", "onCreate: " + menuFragment);
        if (menuFragment != null) {
            Log.d(TAG, "onCreate: != null" + menuFragment);
            mNavigation.setSelectedItemId(R.id.navigation_notifications);
            loadFragment(new NotificationFragment());
        } else {
            Log.d(TAG, "onCreate: == null");
            mNavigation.setSelectedItemId(R.id.navigation_rides);
            loadFragment(new RidesFragment());
        }


        // mNotifications = new ArrayList<>();
        //a();
        // loadFragment(new RidesFragment());

        if (isMyServiceRunning(NotificationService.class)) {
            Log.d(TAG, "onCreate: RUNNING");
        } else {
            Log.d(TAG, "onCreate: NOT RUNNING");
            startService(new Intent(this, NotificationService.class));
        }


        // createNotificationChannel();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.main_notification_channel_name);
            String description = "notification_description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static final int NOTIFICATION_ID = 1138;

    private static final int PENDING_INTENT_ID = 3417;
    private static final String CHANNEL_ID = "moon_runner_notification_channel_01";

    private void noti2() {
        Intent intent = new Intent(this, MainActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("a", "a");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_edit_white_24dp)
                .setContentTitle("title")
                .setContentText("text")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void noti() {
        Intent intent = new Intent(this, MainActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("a", "a");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_edit_white_24dp)
                .setContentTitle("title")
                .setContentText("text")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void a() {
        DatabaseReference userNotificationsRef = mNotificationsRef.child(mUserUid);


        userNotificationsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Toast.makeText(getApplicationContext(), "!!!",Toast.LENGTH_SHORT).show();
                // Notification notification = NotificationUtils.createNotification(getApplication(), MainActivity.class);
                // NotificationUtils.notifyManager();

                if(CurrentUserProfile.notificationsMap.containsKey(dataSnapshot.getKey())) {
                    Log.d("halo", "onChildAdded: mam");
                } else {
                    Log.d("halo", "onChildAdded: nie mam");
                }

                Log.d("halo", "onChildAdded: " + dataSnapshot);
                Log.d("halo", "onChildAdded: " + s);
                noti2();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        // String menuFragment = getIntent().getStringExtra("a");
        // Log.d("Halo", "onCreate: " + getIntent());
        // Log.d("Halo", "onCreate: " + getIntent().getExtras().getString("a"));
        // Log.d("Halo", "onCreate: " + getIntent().getExtras().keySet().size());
        // Log.d("Halo", "onCreate: " + menuFragment);
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
        // getNotifications();
        Log.d(TAG, "onStart: ");

        // Log.d(TAG, "onCreate: *******************" + getIntent().getStringExtra("menuFragment"));
    }

    // private void getNotifications() {
    //     // HashMap<String, Boolean> notificationsMap = CurrentUserProfile.notificationsMap;
    //     DatabaseReference userNotificationsRef = mNotificationsRef.child(mUserUid);
    //
    //     ValueEventListener userNotificationListener = new ValueEventListener() {
    //         @Override
    //         public void onDataChange(DataSnapshot dataSnapshot) {
    //             for (DataSnapshot ds : dataSnapshot.getChildren()) {
    //                 Notification notification = ds.getValue(Notification.class);
    //                 notification.setNotificationUid(ds.getKey());
    //                 Log.d(TAG, "onDataChange: " + notification.toString());
    //                 mNotifications.add(notification);
    //             }
    //         }
    //
    //         @Override
    //         public void onCancelled(DatabaseError databaseError) {
    //             Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
    //         }
    //     };
    //     userNotificationsRef.addListenerForSingleValueEvent(userNotificationListener);
    // }

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
