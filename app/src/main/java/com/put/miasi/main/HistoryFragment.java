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
import com.google.firebase.database.connection.ListenHashProvider;
import com.put.miasi.R;
import com.put.miasi.main.history.HistoryTabFragment;
import com.put.miasi.utils.Database;
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
    private HashMap<String, Boolean> mParticipatedRidesMap;

    private List<String> mOfferedRidesIds;
    private List<RideOffer> mOfferedRidesRides;
    private HashMap<String, Boolean> mOfferedRidesRidesMap;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

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

        mParticipatedRidesIds = new ArrayList<>();
        mParticipatedRides = new ArrayList<>();
        mParticipatedRidesMap = new HashMap<>();
        mOfferedRidesIds = new ArrayList<>();
        mOfferedRidesRides = new ArrayList<>();
        mOfferedRidesRidesMap = new HashMap<>();

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
                    if (mParticipatedRidesIds.contains(ds.getKey())) {
                        RideOffer rideOffer = ds.getValue(RideOffer.class);
                        rideOffer.setKey(ds.getKey());
                        mParticipatedRides.add(rideOffer);
                    } else if (mOfferedRidesIds.contains(ds.getKey())) {
                        RideOffer rideOffer = ds.getValue(RideOffer.class);
                        rideOffer.setKey(ds.getKey());
                        mOfferedRidesRides.add(rideOffer);
                    }
                }
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
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                mParticipatedFragment = new HistoryTabFragment();
                mParticipatedFragment.setIsParticipatedFragmentFlag(true);
                mParticipatedFragment.setRidesMap(mParticipatedRidesMap);
                return mParticipatedFragment;
            } else {
                mOfferedFragment = new HistoryTabFragment();
                mOfferedFragment.setIsParticipatedFragmentFlag(false);
                mOfferedFragment.setRidesMap(mOfferedRidesRidesMap);
                return mOfferedFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
