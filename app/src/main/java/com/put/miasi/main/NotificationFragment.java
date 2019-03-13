package com.put.miasi.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.put.miasi.R;
import com.put.miasi.main.history.HistoryAdapter;
import com.put.miasi.main.notifications.NotificationAdapter;
import com.put.miasi.utils.CurrentUserProfile;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.Notification;
import com.put.miasi.utils.NotificationListItemClickListener;
import com.put.miasi.utils.RideOffer;
import com.put.miasi.utils.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class NotificationFragment extends Fragment implements NotificationListItemClickListener {
    private static final String TAG = "NotificationFragment";

    private TextView mNoDataInfoTextView;
    private NotificationAdapter mNotificationAdapter;
    private RecyclerView mRecyclerView;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mRidesRef;
    private DatabaseReference mNotificationsRef;

    private List<Notification> mNotifications;
    private HashMap<String, User> mSenders;
    private HashMap<String,RideOffer> mRides;

    public NotificationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mNoDataInfoTextView = rootView.findViewById(R.id.noDataInfoTextView);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mNotificationAdapter = new NotificationAdapter(getContext(), this, mNotifications);
        mRecyclerView.setAdapter(mNotificationAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mNoDataInfoTextView.setText(getString(R.string.loading));
        mNoDataInfoTextView.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: ");
        
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mDatabaseRef.child(Database.USERS);
        mRidesRef = mDatabaseRef.child(Database.RIDES);
        mNotificationsRef = mDatabaseRef.child(Database.NOTIFICATIONS);

        mNotifications = new ArrayList<>();
        mSenders = new HashMap<>();
        mRides = new HashMap<>();

        getNotifications();
    }

    @Override
    public void onListItemClick(Notification notification) {

    }

    private void getNotificationsFromProfile() {
        DatabaseReference userProfileNotificationsRef = mUsersRef.child(CurrentUserProfile.uid).child(Database.NOTIFICATIONS);

        ValueEventListener userProfileNotificationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<Notification, Boolean> not = (HashMap<Notification, Boolean>) dataSnapshot.getValue();
                Log.d(TAG, "!!!!: " + not);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        userProfileNotificationsRef.addListenerForSingleValueEvent(userProfileNotificationListener);
    }

    private void getNotifications() {
        getNotificationsFromProfile();

        Log.d(TAG, "getNotifications: ");
        
        mNotifications = new ArrayList<>();
        mSenders = new HashMap<>();
        mRides = new HashMap<>();

        DatabaseReference userNotificationsRef = mNotificationsRef.child(CurrentUserProfile.uid);

        ValueEventListener userNotificationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Notification notification = ds.getValue(Notification.class);
                    notification.setNotificationUid(ds.getKey());

                    String senderUid = notification.getSenderUid();
                    mSenders.put(senderUid, null);

                    final String rideUid = notification.getRideUid();
                    mRides.put(rideUid, null);
                    
                    mNotifications.add(notification);
                    Log.d(TAG, "onDataChange: " + notification.toString());
                }
                Log.d(TAG, "onDataChange: DONE NOTIFICATIONS: " + mNotifications);
                getAllSenders();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        userNotificationsRef.addListenerForSingleValueEvent(userNotificationListener);
    }
    
    private int mIndex;
    
    private void getAllSenders() {
        mIndex = mSenders.size();
        Log.d(TAG, "getAllSenders: mIndex = " + mIndex + " mSenders.size() = " + mSenders.size());
        
        for (final String senderUid : mSenders.keySet()) {
            ValueEventListener userListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User sender = dataSnapshot.getValue(User.class);
                    sender.setUid(senderUid);
                    mSenders.put(senderUid, sender);
                    mIndex--;

                    Log.d(TAG, "onDataChange: mIndex = " + mIndex);
                    if (mIndex <= 0) {
                        Log.d(TAG, "onDataChange: DONE SENDERS ");
                        getAllRides();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            mUsersRef.child(senderUid).addListenerForSingleValueEvent(userListener);
        }
    }

    private void getAllRides() {
        mIndex = mRides.size();
        Log.d(TAG, "getAllSenders: mIndex = " + mIndex + " mRides.size() = " + mRides.size());
        
        for (final String rideUid : mRides.keySet()) {
            ValueEventListener rideListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    RideOffer ride = dataSnapshot.getValue(RideOffer.class);
                    ride.setKey(rideUid);
                    mRides.put(rideUid, ride);
                    mIndex--;

                    Log.d(TAG, "onDataChange: mIndex = " + mIndex);
                    if (mIndex <= 0) {
                        Log.d(TAG, "onDataChange: DONE RIDES ");
                        loadNewData();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            };
            mRidesRef.child(rideUid).addListenerForSingleValueEvent(rideListener);
        }
    }
    
    

    public void setNotificationList(List<Notification> notifications) {
        Log.d(TAG, "setNotificationList: ");
        mNotifications = notifications;
    }

    public void loadNewData() {
        Log.d(TAG, "loadNewData: ");
        sortListByDate(mNotifications);
        setNotificationList(mNotifications);
        checkIfListIsEmpty();
        mNotificationAdapter.loadNewData(mNotifications, mSenders, mRides);
    }

    public void loadNewData(List<Notification> notifications) {
        sortListByDate(notifications);
        setNotificationList(notifications);
        checkIfListIsEmpty();
        mNotificationAdapter.loadNewData(notifications);
    }

    private void checkIfListIsEmpty() {
        if (mNotifications.size() == 0) {
            mNoDataInfoTextView.setVisibility(View.VISIBLE);
            mNoDataInfoTextView.setText("No new notifications");
        } else {
            mNoDataInfoTextView.setVisibility(View.GONE);
        }
    }

    public void sortListByDate(List<Notification> list) {
        Collections.sort(list, new Comparator<Notification>() {
            public int compare(Notification o1, Notification o2) {
                if (DateUtils.getDateFromMilli(o1.getTimeStamp()) == null || DateUtils.getDateFromMilli(o2.getTimeStamp()) == null)
                    return 0;
                return DateUtils.getDateFromMilli(o2.getTimeStamp()).compareTo(DateUtils.getDateFromMilli(o1.getTimeStamp()));
            }
        });
    }
}
