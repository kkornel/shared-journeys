package com.put.miasi.main.history;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.put.miasi.utils.Notification;
import com.put.miasi.utils.RideOffer;
import com.put.miasi.utils.User;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

import static com.put.miasi.main.history.HistoryTabFragment.RATED_RIDE_INTENT_EXTRA;
import static com.put.miasi.main.history.HistoryTabFragment.RIDE_INTENT_EXTRA;
import static com.put.miasi.main.offer.FromActivity.RIDE_OFFER_INTENT;

public class ParticipatedRideDetailsActivity extends AppCompatActivity {
    private static final String TAG = "ParticipatedRideDetails";

    private Button btn_action;
    private ImageView iv_avatar;
    private ImageButton btn_location;
    private TextView tv_distance;
    private TextView tv_car_color;
    private TextView tv_rating;
    private TextView tv_numRatings;
    private TextView tv_hour_end;
    private TextView tv_to;
    private TextView tv_car;
    private TextView tv_hour_begin;
    private TextView tv_luggage;
    private TextView tv_message;
    private TextView tv_from;
    private TextView tv_endedOrActive;
    private TextView tv_reservedSeats;
    private TextView tv_nick;
    private TextView callDriverButton;
    private TextView tv_seats;
    private TextView tv_price;

    private RideOffer mRide;
    private User mDriver;
    private boolean mIsAlreadyRated;
    private boolean mIsEnded;
    private float mRating;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mRidesRef;
    private DatabaseReference mNotificationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participated_ride_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        initializeComponents();
    }

    private void initializeComponents() {
        iv_avatar = findViewById(R.id.iv_avatar);
        tv_distance = findViewById(R.id.tv_distance);
        tv_car_color = findViewById(R.id.tv_car_color);
        tv_rating = findViewById(R.id.tv_rating);
        tv_numRatings = findViewById(R.id.tv_numRatings);
        tv_hour_end = findViewById(R.id.tv_hour_end);
        tv_to = findViewById(R.id.tv_to);
        tv_car = findViewById(R.id.tv_car);
        tv_seats = findViewById(R.id.tv_seats);
        tv_reservedSeats = findViewById(R.id.tv_reservedSeats);
        tv_hour_begin = findViewById(R.id.tv_hour_begin);
        tv_luggage = findViewById(R.id.tv_luggage);
        tv_message = findViewById(R.id.tv_message);
        tv_from = findViewById(R.id.tv_from);
        tv_nick = findViewById(R.id.tv_nick);
        callDriverButton = findViewById(R.id.callDriverButton);
        tv_price = findViewById(R.id.tv_price);
        tv_endedOrActive = findViewById(R.id.tv_endedOrActive);

        btn_location = findViewById(R.id.btn_location);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locationIntent = new Intent(ParticipatedRideDetailsActivity.this, RideLocationActivity.class);
                locationIntent.putExtra(RIDE_OFFER_INTENT, mRide);
                startActivity(locationIntent);
            }
        });

        btn_action = findViewById(R.id.actionButton);

        if (mIsEnded) {
            tv_endedOrActive.setText("ENDED");
            tv_endedOrActive.setTextColor(getResources().getColor(R.color.colorAccent));

            if (mIsAlreadyRated) {
                btn_action.setText("Already rated");
                btn_action.setEnabled(false);
            } else {
                btn_action.setText("Rate the driver");
                btn_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rateTheDriver();
                    }
                });
            }
        } else {
            tv_endedOrActive.setText("ACTIVE");
            tv_endedOrActive.setTextColor(getResources().getColor(R.color.colorActive));

            btn_action.setText("Cancel reservation");
            btn_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelReservation();
                }
            });
        }

        getUserProfile(mRide.getDriverUid());
    }

    private void rateTheDriver() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final LayoutInflater inflater = getLayoutInflater();

        View vView = inflater.inflate(R.layout.dialog_rate_driver, null);
        final ImageView avatarImageView = vView.findViewById(R.id.avatarImageView);
        final TextView driverNameTextView = vView.findViewById(R.id.driverNameTextView);
        final RatingBar ratingBar = vView.findViewById(R.id.ratingBar);

        Picasso.get()
                .load(mDriver.getAvatarUrl())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_error_red_24dp)
                .into(avatarImageView);

        driverNameTextView.setText(mDriver.getFirstName() + " " + mDriver.getSurname());

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mRating = rating;
            }
        });

        final AlertDialog dialog;

        builder.setView(vView)
                .setTitle("Rate " + mDriver.getFirstName())
                .setPositiveButton("Rate", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Rated:  " + mRating, Toast.LENGTH_SHORT).show();
                        rate();
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

    private void rate() {
        // Driver
        float driverRate = mDriver.getDriverRating();
        int numOfDriverRatings = mDriver.getNumberOfDriverRatings();

        driverRate = driverRate + mRating;
        numOfDriverRatings = numOfDriverRatings + 1;

        mDriver.setDriverRating(driverRate);
        mDriver.setNumberOfDriverRatings(numOfDriverRatings);

        String newNotificationUid = mNotificationsRef.child(mDriver.getUid()).push().getKey();
        Notification notification = new Notification(
                Notification.NotificationType.RATED_AS_DRIVER,
                CurrentUserProfile.uid,
                mRide.getKey(),
                mRating);

        HashMap<String, Boolean> driverNotifications = mDriver.getNotifications();
        if (driverNotifications == null) {
            driverNotifications = new HashMap<>();
        }
        driverNotifications.put(newNotificationUid, false);

        mUsersRef.child(mDriver.getUid()).child(Database.DRIVER_RATING).setValue(driverRate);
        mUsersRef.child(mDriver.getUid()).child(Database.NUMBER_OF_DRIVER_RATING).setValue(numOfDriverRatings);

        mUsersRef.child(mDriver.getUid()).child(Database.NOTIFICATIONS).setValue(driverNotifications);

        mNotificationsRef.child(mDriver.getUid()).child(newNotificationUid).setValue(notification);

        // Passenger
        HashMap<String, Boolean> participatedRidesMap = CurrentUserProfile.participatedRidesMap;
        participatedRidesMap.put(mRide.getKey(), true);

        mUsersRef.child(CurrentUserProfile.uid).child(Database.PARTICIPATED_RIDES).setValue(participatedRidesMap);

        CurrentUserProfile.getUserProfile();

        btn_action.setEnabled(false);
    }

    private void cancelReservation() {
        DialogUtils.createDialog(this,
                "Are you sure to cancel reservation?",
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

        HashMap<String, Boolean> participatedRides = CurrentUserProfile.participatedRidesMap;

        participatedRides.remove(rideUid);

        CurrentUserProfile.offeredRidesMap = participatedRides;

        String newNotificationUid = mNotificationsRef.child(mDriver.getUid()).push().getKey();
        Notification notification = new Notification(
                Notification.NotificationType.PASSENGER_RESIGNED,
                CurrentUserProfile.uid,
                mRide.getKey());

        HashMap<String, Boolean> passengerNotifications = mDriver.getNotifications();
        if (passengerNotifications == null) {
            passengerNotifications = new HashMap<>();
        }
        passengerNotifications.put(newNotificationUid, false);

        mUsersRef.child(mDriver.getUid()).child(Database.NOTIFICATIONS).setValue(passengerNotifications);
        mNotificationsRef.child(mDriver.getUid()).child(newNotificationUid).setValue(notification);

        mUsersRef.child(userUid).child(Database.PARTICIPATED_RIDES).setValue(participatedRides);

        int idx = 0;
        for (String id : mRide.passengers.keySet()) {
            if (id.equals(userUid)) {
                break;
            }
            idx++;
        }

        mRide.passengers.remove(idx);

        int seats = mRide.getSeats();
        seats++;
        mRide.setSeats(seats);

        mRidesRef.child(rideUid).setValue(mRide);

        CurrentUserProfile.getUserProfile();
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

        Picasso.get()
                .load(mDriver.getAvatarUrl())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_error_red_24dp)
                .into(iv_avatar);

        tv_nick.setText(mDriver.getFirstName() + " " + mDriver.getSurname());
        callDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber(mDriver.getPhone());
            }
        });

        tv_rating.setText(String.valueOf(mDriver.getDriverRatingAvg()));
        tv_numRatings.setText(String.valueOf(mDriver.getNumberOfDriverRatings()));

        tv_car.setText(mRide.getCar().getBrand() + " " + mRide.getCar().getModel());
        tv_car_color.setText(mRide.getCar().getColor());
        tv_seats.setText(mRide.getSeats() + "");

        tv_luggage.setText("Luggage: " + mRide.getLuggage());

        tv_message.setText(mRide.getMessage());

        int reservedRests = mRide.passengers.get(CurrentUserProfile.uid);
        tv_reservedSeats.setText(String.valueOf(reservedRests));
    }

    private void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void getUserProfile(String userUid) {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference userRef = database.child(Database.USERS).child(userUid);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.setUid(dataSnapshot.getKey());
                mDriver = user;
                fillRideDetails();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        userRef.addListenerForSingleValueEvent(userListener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
