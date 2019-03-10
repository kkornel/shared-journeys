package com.put.miasi.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompatSideChannelService;
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
import com.put.miasi.main.offer.FromActivity;
import com.put.miasi.main.search.SearchActivity;
import com.put.miasi.main.search.SeatsReservationActivity;
import com.put.miasi.utils.CurrentUserProfile;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.OfferLog;
import com.put.miasi.utils.RideOffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class NotificationFragment extends Fragment {
    private static final String TAG = "NotificationFragment";

    private TextView mRideIdTv;
    private TextView mSeatsTv;
    private Button mButton;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mRidesRef;

    public NotificationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        mRideIdTv = rootView.findViewById(R.id.rideIdTv);
        mSeatsTv = rootView.findViewById(R.id.seatsTv);
        mButton = rootView.findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Offer", Toast.LENGTH_SHORT).show();

                cancel();
            }
        });

        // Inflate the layout for this fragment
        return rootView;

    }

    private List<RideOffer> mRideOffers;

    @Override
    public void onStart() {
        super.onStart();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mDatabaseRef.child(Database.USERS);
        mRidesRef = mDatabaseRef.child(Database.RIDES);

        mRideOffers = new ArrayList<>();

        ValueEventListener ridesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                OfferLog.d(dataSnapshot.toString());
                for (DataSnapshot ds :dataSnapshot.getChildren()) {
                    RideOffer rideOffer = ds.getValue(RideOffer.class);
                    rideOffer.setKey(ds.getKey());
                    OfferLog.d(rideOffer.toString());
                    mRideOffers.add(rideOffer);
                    OfferLog.d(String.valueOf(mRideOffers.size()));
                }
                Toast.makeText(getContext(), "juz", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        mRidesRef.addListenerForSingleValueEvent(ridesListener);

    }

    private void cancel() {
        String key = mRideIdTv.getText().toString();
        int seats = Integer.valueOf(mSeatsTv.getText().toString());

        int idx = 0;
        for (RideOffer r : mRideOffers) {
            if (r.getKey().equals(key)) {
                break;
            }
            idx++;
        }

        RideOffer rideOffer = mRideOffers.get(idx);
        int avaSeats = rideOffer.getSeats();
        OfferLog.d("hakuna", avaSeats + "");
        rideOffer.setSeats(avaSeats - seats);
        avaSeats = rideOffer.getSeats();
        OfferLog.d("hakuna", avaSeats + "");

        HashMap<String, Integer> passengers = rideOffer.getPassengers();
        if (passengers == null) {
            passengers = new HashMap<>();
        }
        passengers.put(CurrentUserProfile.uid, seats);

        HashMap<String, Boolean> participatedRides = CurrentUserProfile.participatedRidesMap;
        if (participatedRides == null) {
            participatedRides = new HashMap<>();
        }
        participatedRides.put(key, false);

        mUsersRef.child(CurrentUserProfile.uid).child(Database.PARTICIPATED_RIDES).setValue(participatedRides);
        mRidesRef.child(key).child("passengers").setValue(passengers);
        mRidesRef.child(key).child("seats").setValue(avaSeats);

    }

}
