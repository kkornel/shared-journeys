package com.put.miasi.main.history;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.put.miasi.R;
import com.put.miasi.main.search.RideLocationActivity;
import com.put.miasi.utils.CurrentUserProfile;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.DialogUtils;
import com.put.miasi.utils.GeoUtils;
import com.put.miasi.utils.ListItemClickListener;
import com.put.miasi.utils.Logger;
import com.put.miasi.utils.Notification;
import com.put.miasi.utils.Passenger;
import com.put.miasi.utils.RideOffer;
import com.put.miasi.utils.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.put.miasi.main.history.HistoryTabFragment.RATED_RIDE_INTENT_EXTRA;
import static com.put.miasi.main.history.HistoryTabFragment.RIDE_INTENT_EXTRA;
import static com.put.miasi.main.offer.FromActivity.RIDE_OFFER_INTENT;

public class OfferedRideDetailsActivity extends AppCompatActivity implements ListItemClickListener {
    private static final String TAG = "OfferedRideDetailsActiv";

    private Button btn_action;
    private ImageButton btn_location;
    private TextView tv_distance;
    private TextView tv_car_color;
    private TextView tv_hour_end;
    private TextView tv_to;
    private TextView tv_car;
    private TextView tv_hour_begin;
    private TextView tv_luggage;
    private TextView tv_message;
    private TextView tv_from;
    private TextView tv_endedOrActive;
    private TextView tv_seats;
    private TextView tv_price;
    private TextView tv_noPassengers;
    private RecyclerView passengersRecyclerView;

    private PassengersListAdapter mPassengersListAdapter;

    private RideOffer mRide;
    private boolean mIsAlreadyRated;
    private boolean mIsRated;
    private boolean mIsEnded;
    private float mRating;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mRidesRef;
    private DatabaseReference mNotificationsRef;

    private List<Passenger> mPassengersList;

    private HashMap<User, Boolean> mWasRated; /* So you cant rate him twice */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offered_ride_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_offerSummary));

        mRide = (RideOffer) getIntent().getSerializableExtra(RIDE_INTENT_EXTRA);
        mIsAlreadyRated = getIntent().getBooleanExtra(RATED_RIDE_INTENT_EXTRA, false);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mDatabaseRef.child(Database.USERS);
        mRidesRef = mDatabaseRef.child(Database.RIDES);
        mNotificationsRef = mDatabaseRef.child(Database.NOTIFICATIONS);

        Calendar cal = DateUtils.getCalendarFromMilliSecs(mRide.getDate());
        int durationHours = DateUtils.getDurationHoursFromLongSeconds(mRide.getDuration());
        int durationMins = DateUtils.getDurationMinsFromLongSeconds(mRide.getDuration());
        cal.add(Calendar.HOUR_OF_DAY, durationHours);
        cal.add(Calendar.MINUTE, durationMins);

        // TODO I changed it so now is ended while being in progress
        cal = DateUtils.getCalendarFromMilliSecs(mRide.getDate());
        mIsEnded = !DateUtils.isNowBeforeDate(cal.getTime());

        mPassengersList = new ArrayList<>();
        mWasRated = new HashMap<>();

        initializeComponents();
        getPassengersProfiles();
    }

    private void initializeComponents() {
        tv_distance = findViewById(R.id.tv_distance);
        tv_car_color = findViewById(R.id.tv_car_color);
        tv_hour_end = findViewById(R.id.tv_hour_end);
        tv_to = findViewById(R.id.tv_to);
        tv_car = findViewById(R.id.tv_car);
        tv_seats = findViewById(R.id.tv_seats);
        tv_hour_begin = findViewById(R.id.tv_hour_begin);
        tv_luggage = findViewById(R.id.tv_luggage);
        tv_message = findViewById(R.id.tv_message);
        tv_from = findViewById(R.id.tv_from);
        tv_price = findViewById(R.id.tv_price);
        tv_endedOrActive = findViewById(R.id.tv_endedOrActive);

        tv_noPassengers = findViewById(R.id.tv_noPassengers);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        passengersRecyclerView = findViewById(R.id.passengersRecyclerView);
        passengersRecyclerView.setLayoutManager(linearLayoutManager);
        passengersRecyclerView.setHasFixedSize(true);

        mPassengersListAdapter = new PassengersListAdapter(this, mPassengersList, this);
        passengersRecyclerView.setAdapter(mPassengersListAdapter);

        tv_noPassengers.setVisibility(View.INVISIBLE);

        btn_location = findViewById(R.id.btn_location);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locationIntent = new Intent(OfferedRideDetailsActivity.this, RideLocationActivity.class);
                locationIntent.putExtra(RIDE_OFFER_INTENT, mRide);
                startActivity(locationIntent);
            }
        });


        btn_action = findViewById(R.id.actionButton);
        btn_action.setText("Cancel offer");

        if (mIsEnded) {
            tv_endedOrActive.setText("ENDED");
            tv_endedOrActive.setTextColor(getResources().getColor(R.color.colorAccent));

            btn_action.setEnabled(false);
        } else {
            tv_endedOrActive.setText("ACTIVE");
            tv_endedOrActive.setTextColor(getResources().getColor(R.color.colorActive));

            btn_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelRide();
                }
            });
        }

        fillRideDetails();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        if (mIsEnded) {
            Logger.d("rated", mIsAlreadyRated + "");
            if(mIsAlreadyRated) {
                Toast.makeText(getApplicationContext(), "You've already rated passengers", Toast.LENGTH_SHORT).show();
            } else {
                if (mWasRated.containsKey(mPassengersList.get(clickedItemIndex).getUser())) {
                    Toast.makeText(getApplicationContext(), "You've already rated that passenger", Toast.LENGTH_SHORT).show();
                } else {
                    ratePassengersDialog(mPassengersList.get(clickedItemIndex));
                }
            }
        } else {
            rejectCallDialog(mPassengersList.get(clickedItemIndex));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsRated) {
            makeRated();
        }
    }

    private void getPassengersProfiles() {
        final List<User> passengerProfiles = new ArrayList<>();

        if (mRide.getPassengers() == null) {
            gotAllPassengers(passengerProfiles);
            return;
        }

        final int howManyPassengers = mRide.getPassengers().size();

        for (final String passengerId : mRide.getPassengers().keySet()) {
            mUsersRef.child(passengerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User passenger = dataSnapshot.getValue(User.class);
                    passenger.setUid(passengerId);
                    passengerProfiles.add(passenger);

                    if (passengerProfiles.size() >= howManyPassengers) {
                        gotAllPassengers(passengerProfiles);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                }
            });
        }
    }

    private void gotAllPassengers(List<User> passengersProfiles) {
        List<Passenger> passengers = new ArrayList<>();
        for (User pass : passengersProfiles) {
            int howManySeats = mRide.getPassengers().get(pass.getUid());
            Passenger passenger = new Passenger(pass, howManySeats);
            passengers.add(passenger);
        }
        mPassengersList = passengers;
        loadNewData(passengers);
    }

    private void ratePassengersDialog(Passenger passenger) {
        final User user = passenger.getUser();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final LayoutInflater inflater = getLayoutInflater();

        View vView = inflater.inflate(R.layout.dialog_rate_driver, null);
        final ImageView avatarImageView = vView.findViewById(R.id.avatarImageView);
        final TextView driverNameTextView = vView.findViewById(R.id.driverNameTextView);
        final RatingBar ratingBar = vView.findViewById(R.id.ratingBar);

        Picasso.get()
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_error_red_24dp)
                .into(avatarImageView);

        driverNameTextView.setText(user.getFirstName() + " " + user.getSurname());

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mRating = rating;
            }
        });

        final AlertDialog dialog;

        builder.setView(vView)
                .setTitle("Rate " + user.getFirstName())
                .setPositiveButton("Rate", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Rated:  " + mRating, Toast.LENGTH_SHORT).show();
                        mIsRated = true;
                        rate(user);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Canceled ", Toast.LENGTH_SHORT).show();
                    }
                });

        dialog = builder.create();
        dialog.show();
    }

    private void rate(User user) {
        float passengerRate = user.getPassengerRating();
        int numOfPassRatings = user.getNumberOfPassengerRatings();

        passengerRate = passengerRate + mRating;
        numOfPassRatings = numOfPassRatings + 1;

        user.setPassengerRating(passengerRate);
        user.setNumberOfPassengerRatings(numOfPassRatings);

        String newNotificationUid = mNotificationsRef.child(user.getUid()).push().getKey();
        Notification notification = new Notification(
                Notification.NotificationType.RATED_AS_PASSENGER,
                CurrentUserProfile.uid,
                mRide.getKey(),
                mRating);

        HashMap<String, Boolean> passengerNotifications = user.getNotifications();
        if (passengerNotifications == null) {
            passengerNotifications = new HashMap<>();
        }
        passengerNotifications.put(newNotificationUid, false);

        mUsersRef.child(user.getUid()).child(Database.PASSENGER_RATING).setValue(passengerRate);
        mUsersRef.child(user.getUid()).child(Database.NUMBER_OF_PASSENGER_RATING).setValue(numOfPassRatings);
        mUsersRef.child(user.getUid()).child(Database.NOTIFICATIONS).setValue(passengerNotifications);

        mNotificationsRef.child(user.getUid()).child(newNotificationUid).setValue(notification);

        mWasRated.put(user, true);

        loadNewData(mPassengersList);
    }

    private void makeRated() {
        HashMap<String, Boolean> offeredRides = CurrentUserProfile.offeredRidesMap;
        offeredRides.put(mRide.getKey(), true);

        mUsersRef.child(CurrentUserProfile.uid).child(Database.OFFERED_RIDES).setValue(offeredRides);

        CurrentUserProfile.getUserProfile();
    }

    private void rejectCallDialog(Passenger passenger) {
        final User user = passenger.getUser();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final LayoutInflater inflater = getLayoutInflater();

        View vView = inflater.inflate(R.layout.dialog_passenger, null);

        final ImageView avatarImageView = vView.findViewById(R.id.avatarImageView);
        final TextView passengerNameTextView = vView.findViewById(R.id.driverNameTextView);
        final TextView avgPassRateTextView = vView.findViewById(R.id.avgPassRateTextView);
        final TextView numPassRateTextView = vView.findViewById(R.id.numPassRateTextView);
        final Button rejectButton = vView.findViewById(R.id.declineButton);
        final Button callButton = vView.findViewById(R.id.callButton);

        Picasso.get()
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_error_red_24dp)
                .into(avatarImageView);

        passengerNameTextView.setText(user.getFirstName() + " " + user.getSurname());
        avgPassRateTextView.setText(user.getPassengerRatingAvg() + "");
        numPassRateTextView.setText(user.getNumberOfPassengerRatings() + "");

        final AlertDialog dialog;


        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Reject:  ", Toast.LENGTH_SHORT).show();
                rejectRide(user);
                rejectButton.setEnabled(false);
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Call:  ", Toast.LENGTH_SHORT).show();
                dialPhoneNumber(user.getPhone());
            }
        });


        builder.setView(vView)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Canceled ", Toast.LENGTH_SHORT).show();
                    }
                });

        dialog = builder.create();
        dialog.show();
    }

    private void rejectRide(User user) {
        int seatsReserved = mRide.getPassengers().get(user.getUid());

        int availableSeats = mRide.getSeats();
        availableSeats += seatsReserved;
        mRide.setSeats(availableSeats);

        mRide.getPassengers().remove(user.getUid());

        String newNotificationUid = mNotificationsRef.child(user.getUid()).push().getKey();
        Notification notification = new Notification(
                Notification.NotificationType.RIDE_DECLINED,
                CurrentUserProfile.uid,
                mRide.getKey());

        HashMap<String, Boolean> passengerNotifications = user.getNotifications();
        if (passengerNotifications == null) {
            passengerNotifications = new HashMap<>();
        }
        passengerNotifications.put(newNotificationUid, false);

        mUsersRef.child(user.getUid()).child(Database.NOTIFICATIONS).setValue(passengerNotifications);

        mNotificationsRef.child(user.getUid()).child(newNotificationUid).setValue(notification);

        mRidesRef.child(mRide.getKey()).child(Database.PASSENGERS).setValue(mRide.getPassengers());
        mRidesRef.child(mRide.getKey()).child(Database.SEATS).setValue(availableSeats);
        // mRidesRef.child(mRide.getKey()).setValue(mRide);

        user.getParticipatedRides().remove(mRide.getKey());
        mUsersRef.child(user.getUid()).child(Database.PARTICIPATED_RIDES).setValue(user.getParticipatedRides());

        int declinedId = findPassenger(user.getUid());
        mPassengersList.remove(declinedId);
        loadNewData(mPassengersList);
    }

    private int findPassenger(String passengerUid) {
        int idx = -1;
        for (Passenger passenger : mPassengersList) {
            idx++;
            if (passenger.getUser().getUid().equals(passengerUid)) {
                return idx;
            }
        }
        return idx;
    }

    private void cancelRide() {
        DialogUtils.createDialog(this,
                "Are you sure to cancel ride?",
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cancel();
                    }
                },
                "No",
                null);
    }

    private void cancel() {
        String rideUid = mRide.getKey();
        String userUid = CurrentUserProfile.uid;

        for (Passenger passenger : mPassengersList) {
            User user = passenger.getUser();

            String newNotificationUid = mNotificationsRef.child(user.getUid()).push().getKey();
            Notification notification = new Notification(
                    Notification.NotificationType.RIDE_CANCELED,
                    CurrentUserProfile.uid,
                    mRide.getKey());

            HashMap<String, Boolean> passengerNotifications = user.getNotifications();
            if (passengerNotifications == null) {
                passengerNotifications = new HashMap<>();
            }
            passengerNotifications.put(newNotificationUid, false);

            HashMap<String, Boolean> participatedRides = passenger.getUser().getParticipatedRides();
            participatedRides.remove(rideUid);
            mUsersRef.child(passenger.getUser().getUid()).child(Database.PARTICIPATED_RIDES).setValue(participatedRides);
            mUsersRef.child(passenger.getUser().getUid()).child(Database.NOTIFICATIONS).setValue(passengerNotifications);
            mNotificationsRef.child(user.getUid()).child(newNotificationUid).setValue(notification);
        }

        HashMap<String, Boolean> offeredRides = CurrentUserProfile.offeredRidesMap;

        offeredRides.remove(rideUid);

        CurrentUserProfile.offeredRidesMap = offeredRides;

        mUsersRef.child(userUid).child(Database.OFFERED_RIDES).setValue(offeredRides);

        CurrentUserProfile.getUserProfile();

        mRidesRef.child(mRide.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        });
    }

    private void fillRideDetails() {
        getSupportActionBar().setTitle(DateUtils.getDate(mRide.getDate(), DateUtils.TIME_FORMAT_1));

        Calendar cal = DateUtils.getCalendarFromMilliSecs(mRide.getDate());
        String startHour = DateUtils.getHourFromCalendar(cal);
        String startMin = DateUtils.getMinFromCalendar(cal);

        int durationHours = DateUtils.getDurationHoursFromLongSeconds(mRide.getDuration());
        int durationMins = DateUtils.getDurationMinsFromLongSeconds(mRide.getDuration());
        cal.add(Calendar.HOUR_OF_DAY, durationHours);
        cal.add(Calendar.MINUTE, durationMins);
        String arrivalHour = DateUtils.getHourFromCalendar(cal);
        String arrivalMin = DateUtils.getMinFromCalendar(cal);

        tv_hour_begin.setText(startHour + ":" + startMin);
        tv_hour_end.setText(arrivalHour + ":" + arrivalMin);

        tv_from.setText(GeoUtils.getCityFromLatLng(this, mRide.getStartPoint().toLatLng()));
        tv_to.setText(GeoUtils.getCityFromLatLng(this, mRide.getDestinationPoint().toLatLng()));

        tv_price.setText(mRide.getPrice() + " zł");

        int distance = (int) mRide.getDistance() / 1000;
        tv_distance.setText("Distance: " + distance + " km");

        tv_car.setText(mRide.getCar().getBrand() + " " + mRide.getCar().getModel());
        tv_car_color.setText(mRide.getCar().getColor());
        tv_seats.setText(mRide.getSeats() + "");

        tv_luggage.setText("Luggage: " + mRide.getLuggage());

        tv_message.setText(mRide.getMessage());
    }

    private void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void checkIfListIsEmpty() {
        if (mPassengersList == null || mPassengersList.size() == 0) {
            tv_noPassengers.setVisibility(View.VISIBLE);
            tv_noPassengers.setText("No passengers yet");
        } else {
            tv_noPassengers.setVisibility(View.GONE);
        }
    }

    public void loadNewData(List<Passenger> passengers) {
        mPassengersList = passengers;
        checkIfListIsEmpty();
        mPassengersListAdapter.loadNewData(mPassengersList);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
