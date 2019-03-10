package com.put.miasi.main.history;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.put.miasi.utils.CurrentUserProfile;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.DialogUtils;
import com.put.miasi.utils.GeoUtils;
import com.put.miasi.utils.ListItemClickListener;
import com.put.miasi.utils.OfferLog;
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

public class OfferedRideDetailsActivity extends AppCompatActivity implements ListItemClickListener {
    private static final String TAG = "OfferedRideDetailsActiv";

    private Button btn_action;
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
    private TextView noPassengersTextView;
    private RecyclerView passengersRecyclerView;

    private PassengersListAdapter mPassengersListAdapter;

    private RideOffer mRide;
    private boolean mIsAlreadyRated;
    private boolean mIsEnded;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mRidesRef;

    private List<Passenger> mPassengersList;

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

        Calendar cal = DateUtils.getCalendarFromMilliSecs(mRide.getDate());
        int durationHours = DateUtils.getDurationHoursFromLongSeconds(mRide.getDuration());
        int durationMins = DateUtils.getDurationMinsFromLongSeconds(mRide.getDuration());
        cal.add(Calendar.HOUR_OF_DAY, durationHours);
        cal.add(Calendar.MINUTE, durationMins);

        // TODO I changed it so now is ended while being in progress
        cal = DateUtils.getCalendarFromMilliSecs(mRide.getDate());
        mIsEnded = !DateUtils.isNowBeforeDate(cal.getTime());

        // TODO
        mPassengersList = new ArrayList<>();

        getPassengersProfiles();

        initializeComponents();
    }

    private void getPassengersProfiles() {
        final List<User> passengerProfiles = new ArrayList<>();
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
        loadNewData();
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

        noPassengersTextView = findViewById(R.id.noPassengersTextView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        passengersRecyclerView = findViewById(R.id.passengersRecyclerView);
        passengersRecyclerView.setLayoutManager(linearLayoutManager);
        passengersRecyclerView.setHasFixedSize(true);
        // passengersRecyclerView.addItemDecoration(new DividerItemDecoration(passengersRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        mPassengersListAdapter = new PassengersListAdapter(this, mPassengersList, this);
        passengersRecyclerView.setAdapter(mPassengersListAdapter);

        noPassengersTextView.setText("");
        noPassengersTextView.setVisibility(View.VISIBLE);

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
                        //
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
                    cancelRide();
                }
            });
        }

        fillRideDetails();
    }

    private void checkIfListIsEmpty() {
        if (mPassengersList.size() == 0) {
            noPassengersTextView.setVisibility(View.VISIBLE);
            noPassengersTextView.setText(getString(R.string.no_results));
        } else {
            noPassengersTextView.setVisibility(View.GONE);
        }
    }

    public void loadNewData() {
        checkIfListIsEmpty();
        mPassengersListAdapter.loadNewData(mPassengersList);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        rateTheDriver(mPassengersList.get(clickedItemIndex));
    }

    private void rateTheDriver(Passenger passenger) {
        User user = passenger.getUser();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final LayoutInflater inflater = getLayoutInflater();

        View vView = inflater.inflate(R.layout.dialog_passenger, null);

        final ImageView avatarImageView = vView.findViewById(R.id.avatarImageView);
        final TextView passengerNameTextView = vView.findViewById(R.id.driverNameTextView);
        final TextView avgPassRateTextView = vView.findViewById(R.id.avgPassRateTextView);
        final TextView numPassRateTextView = vView.findViewById(R.id.numPassRateTextView);
        final Button rejectButton = vView.findViewById(R.id.rejectButton);
        final Button rateButton = vView.findViewById(R.id.rateButton);

        Picasso.get()
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_error_red_24dp)
                .into(avatarImageView);

        passengerNameTextView.setText(user.getFirstName() + " " + user.getSurname());
        avgPassRateTextView.setText(user.getPassengerRaingAvg() + "");
        numPassRateTextView.setText(user.getNumberOfPassengerRatings() + "");

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Reject:  ", Toast.LENGTH_SHORT).show();
            }
        });

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Rate:  ", Toast.LENGTH_SHORT).show();
            }
        });

        final AlertDialog dialog;

        builder.setView(vView)
                .setTitle("Rate " + user.getFirstName())
                .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "Call:  ", Toast.LENGTH_SHORT).show();

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
        // String rideUid = mRide.getKey();
        // String userUid = CurrentUserProfile.uid;
        //
        // HashMap<String, Boolean> participatedRides = CurrentUserProfile.participatedRidesMap;
        //
        // participatedRides.remove(rideUid);
        //
        // CurrentUserProfile.offeredRidesMap = participatedRides;
        //
        // mUsersRef.child(userUid).child(Database.PARTICIPATED_RIDES).setValue(participatedRides);
        //
        // int idx = 0;
        // for (String id : mRide.passengers.keySet()) {
        //     if (id.equals(userUid)) {
        //         break;
        //     }
        //     idx++;
        // }
        //
        // mRide.passengers.remove(idx);
        //
        // int seats = mRide.getSeats();
        // seats++;
        // mRide.setSeats(seats);
        //
        // mRidesRef.child(rideUid).setValue(mRide);
        //
        // CurrentUserProfile.getUserProfile();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
