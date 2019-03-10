package com.put.miasi.main.history;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.put.miasi.R;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.ListItemClickListener;
import com.put.miasi.utils.NavLog;
import com.put.miasi.utils.OfferLog;
import com.put.miasi.utils.RideListItemClickListener;
import com.put.miasi.utils.RideOffer;

import java.util.HashMap;
import java.util.List;


public class HistoryTabFragment extends Fragment implements RideListItemClickListener {
    private static final String TAG = "HistoryTabFragment";

    public static final String RIDE_INTENT_EXTRA = "ride-intent-extra";
    public static final String RATED_RIDE_INTENT_EXTRA = "rated-ride-intent-extra";

    private TextView mNoDataInfoTextView;
    private HistoryAdapter mHistoryAdapter;
    private RecyclerView mRecyclerView;

    private boolean mIsParticipatedFragment;

    private List<RideOffer> mRides;
    private HashMap<String, Boolean> mRidesMap;

    public HistoryTabFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view_empty, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        mNoDataInfoTextView = rootView.findViewById(R.id.noDataInfoTextView);

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mHistoryAdapter = new HistoryAdapter(getContext(), this, mRides);
        mRecyclerView.setAdapter(mHistoryAdapter);

        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mNoDataInfoTextView.setText(getString(R.string.loading));
        mNoDataInfoTextView.setVisibility(View.VISIBLE);

        return rootView;
    }

    public void setIsParticipatedFragmentFlag(boolean isParticipated) {
        mIsParticipatedFragment = isParticipated;
    }

    public void setRidesMap(HashMap<String, Boolean> ridesMap) {
        this.mRidesMap = ridesMap;
    }

    @Override
    public void onListItemClick(RideOffer clickedItem) {
        if (mIsParticipatedFragment) {
            Intent intent = new Intent(getActivity(), ParticipatedRideDetailsActivity.class);
            String clickedItemKey = clickedItem.getKey();
            boolean isAlreadyRated = (boolean) mRidesMap.get(clickedItemKey);

            intent.putExtra(RIDE_INTENT_EXTRA, clickedItem);
            intent.putExtra(RATED_RIDE_INTENT_EXTRA, isAlreadyRated);


            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), OfferedRideDetailsActivity.class);
            intent.putExtra(RIDE_INTENT_EXTRA, clickedItem);
            startActivity(intent);
        }
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
