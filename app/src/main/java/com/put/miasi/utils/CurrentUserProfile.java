package com.put.miasi.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class CurrentUserProfile {
    private static final String TAG = "CurrentUserProfile";

    public static String uid;
    public static String avatarUrl;
    public static String firstName;
    public static String surname;
    public static String email;
    public static String phone;
    // public static List<String> offeredRides;
    // public static List<String> participatedRides;
    public static HashMap<String, Boolean> offeredRidesMap;
    public static List<String>  offeredRidesList;
    public static HashMap<String, Boolean> participatedRidesMap;
    public static List<String> participatedRidesList;
    public static double driverRating;
    public static int numberOfDriverRatings;
    public static double passengerRating;
    public static int numberOfPassengerRatings;

    public static void loadUserData(String uids, User user) {
        uid = uids;
        avatarUrl = user.getAvatarUrl();
        firstName = user.getFirstName();
        surname = user.getSurname();
        email = user.getEmail();
        phone = user.getPhone();
        offeredRidesMap = user.getOfferedRides();
        offeredRidesList = user.getOfferedRidesList();
        participatedRidesMap = user.getParticipatedRides();
        participatedRidesList = user.getParticipatedRidesList();
        driverRating = user.getDriverRating();
        numberOfDriverRatings = user.getNumberOfDriverRatings();
        passengerRating = user.getPassengerRating();
        numberOfPassengerRatings = user.getNumberOfPassengerRatings();
    }

    public static void getUserProfile() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String userUid = auth.getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference usersRef = database.getReference(Database.USERS).child(userUid);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                OfferLog.d(dataSnapshot.toString());
                User user = dataSnapshot.getValue(User.class);
                OfferLog.d(user.toString());
                CurrentUserProfile.loadUserData(userUid, user);
                OfferLog.d(CurrentUserProfile.toStringy());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        usersRef.addListenerForSingleValueEvent(userListener);
    }

    public static String toStringy() {
        return "CurrentUserProfile{" +
                "\nuid='" + uid + '\'' +
                "\n, avatarUrl='" + avatarUrl + '\'' +
                "\n, firstName='" + firstName + '\'' +
                "\n, surname='" + surname + '\'' +
                "\n, email='" + email + '\'' +
                "\n, phone='" + phone + '\'' +
                "\n, offeredRides=" + offeredRidesMap +
                "\n, participatedRides=" + participatedRidesMap +
                "\n, driverRating=" + driverRating +
                "\n, numberOfDriverRatings=" + numberOfDriverRatings +
                "\n, passengerRating=" + passengerRating +
                "\n, numberOfPassengerRatings=" + numberOfPassengerRatings +
                '}';
    }
}
