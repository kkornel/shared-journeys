package com.put.miasi.main;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.put.miasi.R;
import com.put.miasi.main.history.HistoryTabFragment;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.NavLog;
import com.put.miasi.utils.RideOffer;
import com.put.miasi.utils.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryFragment";

    private HistoryPagerAdapter mHistoryPagerAdapter;
    private ViewPager mViewPager;

    private HistoryTabFragment mParticipatedFragment;
    private HistoryTabFragment mOfferedFragment;

    private FirebaseUser mUser;
    private String mUserUid;
    private DatabaseReference mRootRef;
    private DatabaseReference mRidesRef;
    private DatabaseReference mUsersRef;

    private List<String> mParticipatedRidesIds;
    private List<RideOffer> mParticipatedRides;

    private List<String> mOfferedRidesIds;
    private List<RideOffer> mOfferedRidesRides;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        NavLog.d("HisFra: onCreateView");

        mHistoryPagerAdapter = new HistoryPagerAdapter(getChildFragmentManager());

        mViewPager = rootView.findViewById(R.id.historyContainer);
        mViewPager.setAdapter(mHistoryPagerAdapter);

        TabLayout tabLayout = rootView.findViewById(R.id.history_tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        NavLog.d("HisFra: onStart");

        mParticipatedRidesIds = new ArrayList<>();
        mParticipatedRides = new ArrayList<>();
        mOfferedRidesIds = new ArrayList<>();
        mOfferedRidesRides = new ArrayList<>();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mRootRef.child(Database.USERS);
        mRidesRef = mRootRef.child(Database.RIDES);

        mUser = auth.getCurrentUser();
        mUserUid = mUser.getUid();

        getUserProfile();
    }

    private void getUserProfile() {
        final DatabaseReference currentUserRef = mUsersRef.child(mUserUid);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                User user = dataSnapshot.getValue(User.class);
                mParticipatedRidesIds = user.getParticipatedRidesList();
                mOfferedRidesIds = user.getOfferedRidesList();
                getUserRides();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        currentUserRef.addListenerForSingleValueEvent(userListener);
    }

    private void getUserRides() {
        ValueEventListener ridesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d(TAG, "DataSnapshot: " + ds.toString());
                    if (mParticipatedRidesIds.contains(ds.getKey())) {
                        mParticipatedRides.add(ds.getValue(RideOffer.class));
                        Log.d(TAG, "mParticipatedRides: " + ds.getValue(RideOffer.class));
                    } else if (mOfferedRidesIds.contains(ds.getKey())) {
                        mOfferedRidesRides.add(ds.getValue(RideOffer.class));
                        Log.d(TAG, "mOfferedRidesRides: " + ds.getValue(RideOffer.class));
                    }
                }
                NavLog.d("heere");
                mParticipatedFragment.loadNewData(mParticipatedRides);
                mOfferedFragment.loadNewData(mOfferedRidesRides);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mRidesRef.addListenerForSingleValueEvent(ridesListener);
    }

    public class HistoryPagerAdapter extends FragmentPagerAdapter {
        public HistoryPagerAdapter(FragmentManager fm) {
            super(fm);
            NavLog.d("HisFra HistoryPagerAdapter");

        }

        @Override
        public Fragment getItem(int position) {
            NavLog.d("HisFra getItem");
            if (position == 0) {
                NavLog.d("HisFra mParticipatedFragment");
                mParticipatedFragment = new HistoryTabFragment();
                mParticipatedFragment.setFlag(true);
                return mParticipatedFragment;
            } else {
                NavLog.d("HisFra mOfferedFragment");
                mOfferedFragment = new HistoryTabFragment();
                mOfferedFragment.setFlag(false);
                return mOfferedFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
