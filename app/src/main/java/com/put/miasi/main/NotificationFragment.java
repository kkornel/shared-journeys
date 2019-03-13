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

import java.util.ArrayList;
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

    private List<Notification> mNotifications;

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

        // TODO remove
        mNotifications = new ArrayList<>();
        mNotifications.add(new Notification());
        mNotifications.add(new Notification());
        mNotifications.add(new Notification());
        mNotifications.add(new Notification());
        mNotifications.add(new Notification());
        mNotifications.add(new Notification());
        mNotifications.add(new Notification());
        mNotifications.add(new Notification());
        mNotifications.add(new Notification());
        mNotifications.add(new Notification());

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

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mDatabaseRef.child(Database.USERS);
        mRidesRef = mDatabaseRef.child(Database.RIDES);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        mRidesRef.addListenerForSingleValueEvent(listener);
    }

    @Override
    public void onListItemClick(Notification notification) {

    }

    public void setNotificationList(List<Notification> notifications) {
        mNotifications = notifications;
    }

    public void loadNewData(List<Notification> notifications) {
        // DateUtils.sortListByDate(ridesList);
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
}
