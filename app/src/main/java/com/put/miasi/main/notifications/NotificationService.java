package com.put.miasi.main.notifications;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.put.miasi.utils.CurrentUserProfile;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.Notification;
import com.put.miasi.utils.User;

public class NotificationService extends Service {
    private static final String TAG = "NotificationService";

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

    private FirebaseAuth mAuth;
    private String mUserUid;
    private DatabaseReference mRootRef;
    private DatabaseReference mNotificationsRef;
    private DatabaseReference mUserNotificationsRef;

    private ChildEventListener mChildNotificationsListener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: Service created!");

        mAuth = FirebaseAuth.getInstance();
        mUserUid = mAuth.getCurrentUser().getUid();
        CurrentUserProfile.uid = mUserUid;
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mNotificationsRef = mRootRef.child(Database.NOTIFICATIONS);
        mUserNotificationsRef = mNotificationsRef.child(mUserUid);

        mChildNotificationsListener = createChildNotificationListener();

        NotificationUtils.createNotification(getApplicationContext());

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                // Log.d(TAG, "onCreate: Service is still running!");
                handler.postDelayed(runnable, 10000);
            }
        };

        handler.postDelayed(runnable, 15000);
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Log.d(TAG, "onStart: Service started by user.");
        addChildEventListener();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Service stopped!");
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        handler.removeCallbacks(runnable);
        removeChildEventListener();
    }

    public void getUserProfile(final DataSnapshot ds) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String userUid = auth.getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference usersRef = database.getReference(Database.USERS).child(userUid);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                CurrentUserProfile.loadUserData(userUid, user);
                a(ds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        usersRef.addListenerForSingleValueEvent(userListener);
    }

    private void a(DataSnapshot dataSnapshot) {
        String notificationUid = dataSnapshot.getKey();

        // Log.d(TAG, "onChildAdded: dataSnapshot " + dataSnapshot);

        boolean hasBeenSeenByUser = false;

        // Log.d(TAG, "onChildAdded: " + CurrentUserProfile.toStringy());

        if (CurrentUserProfile.notificationsMap != null && CurrentUserProfile.notificationsMap.size() != 0) {
            Log.d(TAG, "onChildAdded: " + CurrentUserProfile.notificationsMap);
            hasBeenSeenByUser = CurrentUserProfile.notificationsMap.get(notificationUid);
        }
        // Log.d(TAG, "onChildAdded: hasBeenSeenByUser = " + hasBeenSeenByUser);

        if (hasBeenSeenByUser) {
            // Log.d(TAG, "onChildAdded: not new");
        } else {
            // Log.d(TAG, "onChildAdded: new");

            Notification notification = dataSnapshot.getValue(Notification.class);
            Notification.NotificationType notificationType = notification.getNotificationType();
            NotificationUtils.updateNotification(createTitle(notificationType));
        }
    }

    private ChildEventListener createChildNotificationListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                getUserProfile(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        };
    }

    private void addChildEventListener() {
        mUserNotificationsRef.addChildEventListener(mChildNotificationsListener);
    }

    private void removeChildEventListener() {
        mUserNotificationsRef.removeEventListener(mChildNotificationsListener);
    }

    private String createTitle(Notification.NotificationType notificationType) {
        String title = "";

        switch (notificationType) {
            case NEW_PASSENGER:
                title = "New passenger signed up!";
                break;
            case PASSENGER_RESIGNED:
                title = "Passenger resigned";
                break;
            case RIDE_CANCELED:
                title = "Ride was canceled";
                break;
            case RIDE_DECLINED:
                title = "Driver declined your ride";
                break;
            case RATED_AS_DRIVER:
                title = "New rating as driver";
                break;
            case RATED_AS_PASSENGER:
                title = "New rating as passenger";
                break;
        }
        return title;
    }
}