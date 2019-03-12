package com.put.miasi.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.put.miasi.R;
import com.put.miasi.main.offer.FromActivity;
import com.put.miasi.main.search.RideCalendar;
import com.put.miasi.main.search.SearchActivity;


public class RidesFragment extends Fragment {
    private static final String TAG = "RidesFragment";

    private Button mOfferButton;
    private Button mSearchButton;

    public RidesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rides, container, false);

        mOfferButton = rootView.findViewById(R.id.offerButton);
        mOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FromActivity.class));
            }
        });

        mSearchButton = rootView.findViewById(R.id.searchButton);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RideCalendar.class));
            }
        });

        return rootView;
    }
}
