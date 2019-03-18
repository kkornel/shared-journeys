package com.put.miasi.main;

import android.content.Intent;
import android.media.Image;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
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
        firebaseInit();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void initializeComponents(View view) {
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

    private void fillComponents() {
        Picasso.get().load(currentUser.getAvatarUrl()).transform(new CircleTransform()).into(iv_avatar);
        tv_nick.setText(currentUser.getFirstName() + " " + currentUser.getSurname());
        String driverRating = "Driver rating: " + "<b>" + String.format("%.1f", currentUser.getDriverRatingAvg()) + "</b> ";
        tv_driverRating.setText(Html.fromHtml(driverRating));
        tv_numberOfDriverRatings.setText("Number of ratings: " + currentUser.getNumberOfDriverRatings());
        tv_numberOfDriverOffers.setText("Number of offered rides: " + currentUser.getOfferedRidesList().size());
        String passengerRating = "Passenger rating: " + "<b>" + String.format("%.1f", currentUser.getPassengerRatingAvg()) + "</b> ";
        tv_passenger_rating.setText(Html.fromHtml(passengerRating));
        tv_numberOfPassengerRatings.setText("Number of ratings: " + currentUser.getNumberOfPassengerRatings());
        tv_numberOfParticipatedRides.setText("Number of participated rides: " + currentUser.getParticipatedRidesList().size());
        tv_telephoneNumber.setText(currentUser.getPhone());
        tv_email.setText(currentUser.getEmail());
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

    ///////////////////// FIREBASE /////////////////////////////
    private void firebaseInit() {
        Log.i(TAG, CurrentUserProfile.uid);
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference usersRef = database.child(Database.USERS);
        final ValueEventListener usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    user.setUid(ds.getKey());
                    if (CurrentUserProfile.uid.equals(user.getUid())) {
                        currentUser = user;
                        fillComponents();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        usersRef.addListenerForSingleValueEvent(usersListener);
    }
}




