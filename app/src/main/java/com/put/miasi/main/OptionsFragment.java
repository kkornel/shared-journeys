package com.put.miasi.main;


import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.put.miasi.LoginActivity;
import com.put.miasi.R;
import com.put.miasi.utils.CircleTransform;
import com.put.miasi.utils.CurrentUserProfile;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.RideOffer;
import com.put.miasi.utils.User;
import com.squareup.picasso.Picasso;


public class OptionsFragment extends Fragment {
    private static final String TAG = "OptionsFragment";
    private ImageView iv_avatar;
    private TextView tv_nick;
    private TextView tv_driverRating;
    private TextView tv_numberOfDriverRatings;
    private TextView tv_numberOfDriverOffers;
    private TextView tv_passenger_rating;
    private TextView tv_numberOfPassengerRatings;
    private TextView tv_numberOfParticipatedRides;
    private TextView tv_telephoneNumber;
    private TextView tv_email;
    private User currentUser;


    public OptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options, container, false);
        initializeComponents(view);
        fillComponents();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    private void initializeComponents(View view)
    {
        iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
        tv_nick = (TextView) view.findViewById(R.id.tv_nick);
        tv_driverRating = (TextView) view.findViewById(R.id.tv_driverRating);
        tv_numberOfDriverRatings = (TextView) view.findViewById(R.id.tv_numberOfDriverRatings);
        tv_numberOfDriverOffers = (TextView) view.findViewById(R.id.tv_numberOfDriverOffers);
        tv_passenger_rating = (TextView) view.findViewById(R.id.tv_passenger_rating);
        tv_numberOfPassengerRatings = (TextView) view.findViewById(R.id.tv_numberOfPassengerRatings);
        tv_numberOfParticipatedRides = (TextView) view.findViewById(R.id.tv_numberOfParticipatedRides);
        tv_telephoneNumber = (TextView) view.findViewById(R.id.tv_telephoneNumber);
        tv_email = (TextView) view.findViewById(R.id.tv_email);
    }
    private void fillComponents()
    {
        CurrentUserProfile.getUserProfile();
        Picasso.get().load(CurrentUserProfile.avatarUrl).transform(new CircleTransform()).into(iv_avatar);
        tv_nick.setText(CurrentUserProfile.firstName + " " + CurrentUserProfile.surname);
        tv_driverRating.setText("");
        tv_numberOfDriverRatings.setText("");
        tv_numberOfDriverOffers.setText("");
        tv_passenger_rating.setText("");
        tv_numberOfPassengerRatings.setText("");
        tv_numberOfParticipatedRides.setText("");
        tv_telephoneNumber.setText("");
        tv_email.setText("");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_menu_item:
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
