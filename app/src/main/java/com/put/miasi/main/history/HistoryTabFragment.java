package com.put.miasi.main.history;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.put.miasi.R;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.ListItemClickListener;
import com.put.miasi.utils.NavLog;
import com.put.miasi.utils.RideOffer;

import java.util.ArrayList;
import java.util.List;


public class HistoryTabFragment extends Fragment implements ListItemClickListener {
    private static final String TAG = "HistoryTabFragment";

    private TextView mNoDataInfoTextView;
    private HistoryAdapter mHistoryAdapter;
    private RecyclerView mRecyclerView;

    private boolean mIsParticipatedFragment;

    private List<RideOffer> mRides;

    public HistoryTabFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view_empty, container, false);

        NavLog.d("HisTabFra: onCreateView");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

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

    public void setFlag(boolean isParticipated) {
        NavLog.d("HisTabFra: setFlag");
        mIsParticipatedFragment = isParticipated;
    }

    @Override
    public void onStart() {
        super.onStart();

        NavLog.d("HisTabFra: onStart");


        if (mIsParticipatedFragment) {

        }









        // TODO download all data
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        // TODO start new activity for details

        // Intent i = new Intent(getActivity(), WorkoutGpsDetailsFriend.class);
        // i.putExtra(FRIEND_WORKOUT_INTENT_EXTRA, mFeedFriendsList.get(clickedItemIndex));
        // startActivity(i);
    }

    public void setRidesList(List<RideOffer> rides) {
        mRides = rides;
    }

    public void loadNewData(List<RideOffer> ridesList) {
        DateUtils.sortListByDate(ridesList);
        setRidesList(ridesList);
        checkIfListIsEmpty();
        mHistoryAdapter.loadNewData(mRides);
        NavLog.d(mRides.toString());
    }

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
