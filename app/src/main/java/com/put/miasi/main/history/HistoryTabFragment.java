package com.put.miasi.main.history;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.put.miasi.R;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.ListItemClickListener;
import com.put.miasi.utils.RideOffer;

import java.util.ArrayList;
import java.util.List;


public class HistoryTabFragment extends Fragment implements ListItemClickListener {
    private static final String TAG = "HistoryTabFragment";

    private SwipeRefreshLayout mSwipeRefresh;
    private TextView mNoDataInfoTextView;
    private HistoryAdapter mHistoryAdapter;
    private RecyclerView mRecyclerView;

    private FirebaseUser mUser;
    private String mUserUid;
    private DatabaseReference mRootRef;
    private DatabaseReference mWorkoutsRef;
    private DatabaseReference mUsersRef;

    private List<String> mFriendsIds;
    private List<RideOffer> mRides;

    // I do it, because I want to call loadNewData() only when I get all the data from ALL friends
    // not every for every friend separately
    private long mNumberOfFriendsAlreadyIterated = 0;
    private long mFriendsCount;

    private boolean mFragmentJustStarted;
    private boolean mNewData;
    private boolean mDataChanged;

    public HistoryTabFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view_swipe_refresh, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mSwipeRefresh = rootView.findViewById(R.id.swipeRefresh);
        mSwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Toast.makeText(getActivity(), getString(R.string.refresh), Toast.LENGTH_SHORT).show();

                        if (mDataChanged) {
                            readFriendsWorkouts(mFriendsIds);
                            mDataChanged = false;
                        } else {
                            if (mNewData) {
                                mFragmentJustStarted = false;
                                // loadNewData(mFeedFriendsList);
                                mNewData = false;
                            }
                        }
                        mSwipeRefresh.setRefreshing(false);
                    }
                }
        );

        mNoDataInfoTextView = rootView.findViewById(R.id.noDataInfoTextView);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mHistoryAdapter = new HistoryAdapter(getContext(), this, mRides);
        mRecyclerView.setAdapter(mHistoryAdapter);

        mNoDataInfoTextView.setText(getString(R.string.loading));
        mNoDataInfoTextView.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        mUser = auth.getCurrentUser();
        mUserUid = mUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRootRef = database.getReference();
        mUsersRef = mRootRef.child(Database.USERS);

        mFragmentJustStarted = true;
        mNewData = false;
        mDataChanged = false;

        mFriendsIds = new ArrayList<>();


    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

    }

    public void setFeedFriendsList(List<RideOffer> feedFriendsList) {
        // mFeedFriendsList = feedFriendsList;
    }

    public void loadNewData(List<RideOffer> feedFriendsList) {
        // sortListByDate(feedFriendsList);
        // setFeedFriendsList(feedFriendsList);
        // checkIfListIsEmpty();
        // mHistoryAdapter.loadNewData(mFeedFriendsList);
    }

    private void readFriendsWorkouts(List<String> friendsIds) {

    }

    // private void sortListByDate(List<FriendWorkout> list) {
    //     Collections.sort(list, new Comparator<FriendWorkout>() {
    //         public int compare(FriendWorkout o1, FriendWorkout o2) {
    //             if (o1.getWorkout().getWorkoutDate() == null || o2.getWorkout().getWorkoutDate() == null)
    //                 return 0;
    //             return o2.getWorkout().getWorkoutDate().compareTo(o1.getWorkout().getWorkoutDate());
    //         }
    //     });
    // }

    private void checkIfListIsEmpty() {
        if (mRides.size() == 0) {
            mNoDataInfoTextView.setVisibility(View.VISIBLE);
            mNoDataInfoTextView.setText("LUL");
        } else {
            mNoDataInfoTextView.setVisibility(View.GONE);
        }
    }

    private void showNoFriendsMsg() {
        mNoDataInfoTextView.setVisibility(View.VISIBLE);
        mNoDataInfoTextView.setText("No friends");
    }

}
