package com.put.miasi.main;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.put.miasi.R;
import com.put.miasi.main.history.OfferedRideDetailsActivity;
import com.put.miasi.main.notifications.NotificationAdapter;
import com.put.miasi.utils.CircleTransform;
import com.put.miasi.utils.CurrentUserProfile;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.Notification;
import com.put.miasi.utils.NotificationListItemClickListener;
import com.put.miasi.utils.RideOffer;
import com.put.miasi.utils.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.put.miasi.main.history.HistoryTabFragment.RATED_RIDE_INTENT_EXTRA;
import static com.put.miasi.main.history.HistoryTabFragment.RIDE_INTENT_EXTRA;


public class NotificationFragment extends Fragment implements NotificationListItemClickListener {
    private static final String TAG = "NotificationFragment";

    private SwipeRefreshLayout mSwipeRefresh;
    private TextView mNoDataInfoTextView;
    private NotificationAdapter mNotificationAdapter;
    private RecyclerView mRecyclerView;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mRidesRef;
    private DatabaseReference mNotificationsRef;

    // private HashMap<String, Boolean> mNotificationsFromProfile;
    private List<Notification> mNotifications;
    private HashMap<String, User> mSenders;
    private HashMap<String, RideOffer> mRides;

    private int mIndex;

    private boolean mHasDataChanged;

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

        mSwipeRefresh = rootView.findViewById(R.id.swipeRefresh);
        mSwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mNoDataInfoTextView.setText(getString(R.string.loading));
                        mNoDataInfoTextView.setVisibility(View.VISIBLE);
                        getNotifications();
                    }
                }
        );

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

        // mNotificationsFromProfile = new HashMap<>();
        mNotifications = new ArrayList<>();
        mSenders = new HashMap<>();
        mRides = new HashMap<>();

        mHasDataChanged = false;

        // getNotificationsFromProfile();
        getNotifications();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onListItemClick(Notification notification, String title) {
        Notification.NotificationType notificationType = notification.getNotificationType();

        switch (notificationType) {
            case NEW_PASSENGER:
            case PASSENGER_RESIGNED:
                showNotificationDialogToActivity(notification, title);
                break;
            case RIDE_CANCELED:
            case RIDE_DECLINED:
            case RATED_AS_DRIVER:
            case RATED_AS_PASSENGER:
                showNotificationDialog(notification, title);
                break;
        }
    }

    // private void getNotificationsFromProfile() {
    //     // mNotificationsFromProfile = new HashMap<>();
    //
    //     DatabaseReference userProfileNotificationsRef = mUsersRef.child(CurrentUserProfile.uid).child(Database.NOTIFICATIONS);
    //
    //     ValueEventListener userProfileNotificationListener = new ValueEventListener() {
    //         @Override
    //         public void onDataChange(DataSnapshot dataSnapshot) {
    //             HashMap<String, Boolean> notificationsFromProfile = (HashMap<String, Boolean>) dataSnapshot.getValue();
    //
    //             if (notificationsFromProfile == null || notificationsFromProfile.size() == 0) {
    //                 mNotificationsFromProfile = new HashMap<>();
    //                 mNotifications = new ArrayList<>();
    //                 mSenders = new HashMap<>();
    //                 mRides = new HashMap<>();
    //                 mSwipeRefresh.setRefreshing(false);
    //                 Toast.makeText(getActivity(), "No new data", Toast.LENGTH_SHORT).show();
    //                 noNewNotifications();
    //                 loadNewData();
    //                 return;
    //             }
    //
    //             // Log.d(TAG, "onDataChange: notificationsFromProfile " + notificationsFromProfile);
    //             // Log.d(TAG, "onDataChange: notificationsFromProfile " + notificationsFromProfile.size());
    //             // Log.d(TAG, "onDataChange: notificationsFromProfile " + mNotificationsFromProfile);
    //             // Log.d(TAG, "onDataChange: mNotificationsFromProfile " + mNotificationsFromProfile.size());
    //
    //             if (mNotificationsFromProfile.size() == notificationsFromProfile.size()) {
    //                 // Log.d(TAG, "onDataChange: if");
    //                 mHasDataChanged = false;
    //                 mSwipeRefresh.setRefreshing(false);
    //                 Toast.makeText(getActivity(), "No new data", Toast.LENGTH_SHORT).show();
    //                 checkIfListIsEmpty();
    //             } else {
    //                 mNotificationsFromProfile = new HashMap<>();
    //                 // Log.d(TAG, "onDataChange: else");
    //                 mHasDataChanged = true;
    //                 mNotificationsFromProfile = notificationsFromProfile;
    //                 if (mNotificationsFromProfile == null || mNotificationsFromProfile.size() == 0) {
    //                     noNewNotifications();
    //                 }
    //                 getNotifications();
    //             }
    //
    //             // Log.d(TAG, "getNotificationsFromProfile: " + mNotifications);
    //         }
    //
    //         @Override
    //         public void onCancelled(DatabaseError databaseError) {
    //             Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
    //         }
    //     };
    //     userProfileNotificationsRef.addListenerForSingleValueEvent(userProfileNotificationListener);
    // }

    private void getNotifications() {
        // Log.d(TAG, "getNotifications: ");

        mNotifications = new ArrayList<>();
        mSenders = new HashMap<>();
        mRides = new HashMap<>();

        DatabaseReference userNotificationsRef = mNotificationsRef.child(CurrentUserProfile.uid);

        ValueEventListener userNotificationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot);
                if (dataSnapshot.getValue() == null) {
                    noNewNotifications();
                    loadNewData();
                    return;
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Notification notification = ds.getValue(Notification.class);

                    String notificationUid = ds.getKey();
                    notification.setNotificationUid(notificationUid);

                    String senderUid = notification.getSenderUid();
                    mSenders.put(senderUid, null);

                    String rideUid = notification.getRideUid();
                    mRides.put(rideUid, null);

                    mNotifications.add(notification);
                    // Log.d(TAG, "onDataChange: " + notification.toString());
                }
                // Log.d(TAG, "onDataChange: DONE NOTIFICATIONS: " + mNotifications);
                getAllSenders();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        userNotificationsRef.addListenerForSingleValueEvent(userNotificationListener);
    }

    private void getAllSenders() {
        mIndex = mSenders.size();
        // Log.d(TAG, "getAllSenders: mIndex = " + mIndex + " mSenders.size() = " + mSenders.size());

        for (final String senderUid : mSenders.keySet()) {
            ValueEventListener userListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User sender = dataSnapshot.getValue(User.class);
                    sender.setUid(senderUid);
                    mSenders.put(senderUid, sender);
                    mIndex--;

                    // Log.d(TAG, "onDataChange: mIndex = " + mIndex);
                    if (mIndex <= 0) {
                        // Log.d(TAG, "onDataChange: DONE SENDERS ");
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
        // Log.d(TAG, "getAllSenders: mIndex = " + mIndex + " mRides.size() = " + mRides.size());

        for (final String rideUid : mRides.keySet()) {

            if (rideUid == null && mRides.size() == 1) {
                mSwipeRefresh.setRefreshing(false);
                loadNewData();
                continue;
            }

            if (rideUid == null) {
                loadNewData();
                continue;
            }

            ValueEventListener rideListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Log.d(TAG, "asd-> " + dataSnapshot.toString());

                    RideOffer ride = dataSnapshot.getValue(RideOffer.class);

                    ride.setKey(rideUid);
                    mRides.put(rideUid, ride);
                    mIndex--;

                    // Log.d(TAG, "onDataChange: mIndex = " + mIndex);
                    if (mIndex <= 0) {
                        // Log.d(TAG, "onDataChange: DONE RIDES ");
                        mSwipeRefresh.setRefreshing(false);
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
        // Log.d(TAG, "setNotificationList: ");
        mNotifications = notifications;
    }

    public void loadNewData() {
        // Log.d(TAG, "loadNewData: ");
        sortListByDate(mNotifications);
        setNotificationList(mNotifications);
        checkIfListIsEmpty();
        // mNotificationAdapter.loadNewData(mNotificationsFromProfile, mNotifications, mSenders, mRides);
        mNotificationAdapter.loadNewData(mNotifications, mSenders, mRides);
    }

    private void checkIfListIsEmpty() {
        if (mNotifications.size() == 0) {
            noNewNotifications();
        } else {
            mNoDataInfoTextView.setVisibility(View.GONE);
        }
    }

    private void noNewNotifications() {
        mNoDataInfoTextView.setVisibility(View.VISIBLE);
        mNoDataInfoTextView.setText("No new notifications");
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

    private void showNotificationDialogToActivity(final Notification notification, String title) {
        final User user = mSenders.get(notification.getSenderUid());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final LayoutInflater inflater = getLayoutInflater();

        View vView = inflater.inflate(R.layout.dialog_notification_to_activity, null);
        final ImageView avatarImageView = vView.findViewById(R.id.avatarImageView);
        final TextView notificationTitleTextView = vView.findViewById(R.id.notificationTitle);
        final Button seeRideButton = vView.findViewById(R.id.seeRideButton);

        // Picasso.get()
        //         .load(user.getAvatarUrl())
        //         .placeholder(R.drawable.ic_account_circle_black_24dp)
        //         .error(R.drawable.ic_error_red_24dp)
        //         .into(avatarImageView);

        Picasso.get()
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_error_red_24dp)
                .transform(new CircleTransform()).into(avatarImageView);

        notificationTitleTextView.setText(title);

        seeRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OfferedRideDetailsActivity.class);

                intent.putExtra(RIDE_INTENT_EXTRA, mRides.get(notification.getRideUid()));
                intent.putExtra(RATED_RIDE_INTENT_EXTRA, false);

                startActivity(intent);
            }
        });

        final AlertDialog dialog;

        builder.setView(vView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mNotifications.remove(getIndex(notification.getNotificationUid()));
                        mNotificationsRef.child(CurrentUserProfile.uid).child(notification.getNotificationUid()).removeValue();
                        loadNewData();
                    }
                });
        // .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
        //     public void onClick(DialogInterface dialog, int id) {
        //         mNotificationsFromProfile.remove(notification.getNotificationUid());
        //         mNotifications.remove(getIndex(notification.getNotificationUid()));
        //         CurrentUserProfile.notificationsMap = mNotificationsFromProfile;
        //         mUsersRef.child(CurrentUserProfile.uid).child(Database.NOTIFICATIONS).setValue(mNotificationsFromProfile);
        //         mNotificationsRef.child(CurrentUserProfile.uid).child(notification.getNotificationUid()).removeValue();
        //         loadNewData();
        //     }
        // });

        // builder.setView(vView)
        //         .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        //             public void onClick(DialogInterface dialog, int id) {
        //                 mNotificationsFromProfile.put(notification.getNotificationUid(), true);
        //                 CurrentUserProfile.notificationsMap = mNotificationsFromProfile;
        //                 mUsersRef.child(CurrentUserProfile.uid).child(Database.NOTIFICATIONS).setValue(mNotificationsFromProfile);
        //                 loadNewData();
        //             }
        //         })
        //         .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
        //             public void onClick(DialogInterface dialog, int id) {
        //                 mNotificationsFromProfile.remove(notification.getNotificationUid());
        //                 mNotifications.remove(getIndex(notification.getNotificationUid()));
        //                 CurrentUserProfile.notificationsMap = mNotificationsFromProfile;
        //                 mUsersRef.child(CurrentUserProfile.uid).child(Database.NOTIFICATIONS).setValue(mNotificationsFromProfile);
        //                 mNotificationsRef.child(CurrentUserProfile.uid).child(notification.getNotificationUid()).removeValue();
        //                 loadNewData();
        //             }
        //         });

        dialog = builder.create();
        dialog.show();
    }

    private void showNotificationDialog(final Notification notification, String title) {
        final User user = mSenders.get(notification.getSenderUid());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final LayoutInflater inflater = getLayoutInflater();

        View vView = inflater.inflate(R.layout.dialog_notification, null);
        final ImageView avatarImageView = vView.findViewById(R.id.avatarImageView);
        final TextView notificationTitleTextView = vView.findViewById(R.id.notificationTitle);

        // Picasso.get()
        //         .load(user.getAvatarUrl())
        //         .placeholder(R.drawable.ic_account_circle_black_24dp)
        //         .error(R.drawable.ic_error_red_24dp)
        //         .into(avatarImageView);

        Picasso.get()
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_error_red_24dp)
                .transform(new CircleTransform()).into(avatarImageView);

        notificationTitleTextView.setText(title);

        final AlertDialog dialog;

        builder.setView(vView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mNotifications.remove(getIndex(notification.getNotificationUid()));
                        mNotificationsRef.child(CurrentUserProfile.uid).child(notification.getNotificationUid()).removeValue();
                        loadNewData();
                    }
                });
        // .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
        //     public void onClick(DialogInterface dialog, int id) {
        //         mNotificationsFromProfile.remove(notification.getNotificationUid());
        //         mNotifications.remove(getIndex(notification.getNotificationUid()));
        //         CurrentUserProfile.notificationsMap = mNotificationsFromProfile;
        //         mUsersRef.child(CurrentUserProfile.uid).child(Database.NOTIFICATIONS).setValue(mNotificationsFromProfile);
        //         mNotificationsRef.child(CurrentUserProfile.uid).child(notification.getNotificationUid()).removeValue();
        //         loadNewData();
        //     }
        // });

        // builder.setView(vView)
        //         .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        //             public void onClick(DialogInterface dialog, int id) {
        //                 mNotificationsFromProfile.put(notification.getNotificationUid(), true);
        //                 CurrentUserProfile.notificationsMap = mNotificationsFromProfile;
        //                 mUsersRef.child(CurrentUserProfile.uid).child(Database.NOTIFICATIONS).setValue(mNotificationsFromProfile);
        //                 loadNewData();
        //             }
        //         })
        //         .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
        //             public void onClick(DialogInterface dialog, int id) {
        //                 mNotificationsFromProfile.remove(notification.getNotificationUid());
        //                 mNotifications.remove(getIndex(notification.getNotificationUid()));
        //                 CurrentUserProfile.notificationsMap = mNotificationsFromProfile;
        //                 mUsersRef.child(CurrentUserProfile.uid).child(Database.NOTIFICATIONS).setValue(mNotificationsFromProfile);
        //                 mNotificationsRef.child(CurrentUserProfile.uid).child(notification.getNotificationUid()).removeValue();
        //                 loadNewData();
        //             }
        //         });

        dialog = builder.create();
        dialog.show();
    }

    private int getIndex(String uid) {
        int idx = 0;
        for (Notification notification : mNotifications) {
            if (notification.getNotificationUid().equals(uid)) {
                break;
            }
        }
        return idx;
    }
}
