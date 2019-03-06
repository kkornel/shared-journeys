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

/**
 * A simple {@link Fragment} subclass.
 */
public class RidesFragment extends Fragment {

    private Button mOfferButton;
    private Button mSearchButton;


    public RidesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rides, container, false);

        mOfferButton = rootView.findViewById(R.id.offerButton);
        mOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Offer", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getActivity(), FromActivity.class));
            }
        });

        mSearchButton = rootView.findViewById(R.id.searchButton);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Search", Toast.LENGTH_SHORT).show();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

}
