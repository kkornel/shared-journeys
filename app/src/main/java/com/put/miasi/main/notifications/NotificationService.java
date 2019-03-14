package com.put.miasi.main.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.put.miasi.R;
import com.put.miasi.main.MainActivity;
import com.put.miasi.utils.CurrentUserProfile;
import com.put.miasi.utils.Database;

public class NotificationService extends Service {
    private static final String TAG = "NotificationService";

    public static String NOTIFICATION_INTENT_EXTRA = "open_notifications";

    private static final int NOTIFICATION_ID = 1302;
    private static final String CHANNEL_ID = "miasi_notification_channel_01";

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

        createNotificationChannel();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                Log.d(TAG, "onCreate: Service is still running!");
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

    private ChildEventListener createChildNotificationListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded: dataSnapshot " + dataSnapshot);
                Log.d(TAG, "onChildAdded: s " + s);

                if(CurrentUserProfile.notificationsMap.containsKey(dataSnapshot.getKey())) {
                    Log.d(TAG, "onChildAdded: mam");
                } else {
                    Log.d(TAG, "onChildAdded: nie mam");
                    createNotification();
                }


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

    private void createNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_INTENT_EXTRA, true);
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

    private void addChildEventListener() {
        mUserNotificationsRef.addChildEventListener(mChildNotificationsListener);
    }

    private void removeChildEventListener() {
        mUserNotificationsRef.removeEventListener(mChildNotificationsListener);
    }




}