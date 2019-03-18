package com.put.miasi.main.search;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.put.miasi.R;
import com.put.miasi.main.MainActivity;
import com.put.miasi.utils.CircleTransform;
import com.put.miasi.utils.CurrentUserProfile;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.GeoUtils;
import com.put.miasi.utils.Notification;
import com.put.miasi.utils.RideOffer;
import com.put.miasi.utils.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.put.miasi.main.offer.FromActivity.RIDE_OFFER_INTENT;

public class RideDetailsActivity extends AppCompatActivity  {
    private static final String TAG = "RideDetailsActivity";

    private Button btn_reservation;
    private ImageButton ib_see_on_the_map;
    private ImageView iv_avatar;
    private TextView tv_available_seats;
    private TextView tv_distance;
    private TextView tv_car_color;
    private TextView tv_rating;
    private TextView tv_hour_end;
    private TextView tv_to;
    private TextView tv_car;
    private TextView tv_hour_begin;
    private TextView tv_luggage;
    private TextView tv_message;
    private TextView tv_from;
    private TextView tv_date;
    private TextView tv_nick;
    private TextView tv_price;
    private TextView tv_rating_quantity;
    private TextView tv_telephone;
    private String rideKey = null;
    private String uid = null;
    private RideOffer offer;
    private User rider;

    private List<String> numbersList = null;
    private Spinner spinner = null;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mRidesRef;
    private DatabaseReference mNotificationsRef;

    private int mNumOfSeatsPicked;
    private User mDriver;
    private RideOffer mOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details);
        initializeComponents();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Ride details");

        Intent intent = getIntent();
        rideKey = intent.getStringExtra("Uid");
        firebaseInit();
    }

    private void initializeComponents()
    {
        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
        tv_available_seats = (TextView) findViewById(R.id.tv_available_seats);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_car_color = (TextView) findViewById(R.id.tv_car_color);
        tv_rating = (TextView) findViewById(R.id.tv_rating);
        tv_hour_end = (TextView) findViewById(R.id.tv_hour_end);
        tv_to = (TextView) findViewById(R.id.tv_to);
        tv_car = (TextView) findViewById(R.id.tv_car);
        tv_hour_begin = (TextView) findViewById(R.id.tv_hour_begin);
        tv_luggage = (TextView) findViewById(R.id.tv_luggage);
        tv_message = (TextView) findViewById(R.id.tv_message);
        tv_from = (TextView) findViewById(R.id.tv_from);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_nick = (TextView) findViewById(R.id.tv_nick);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_telephone = (TextView) findViewById(R.id.tv_telephone);
        tv_rating_quantity = (TextView) findViewById(R.id.tv_rating_quantity);

        btn_reservation = (Button) findViewById(R.id.btn_reservation);
        btn_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpinnerDialog();
            }
        });

        ib_see_on_the_map = (ImageButton) findViewById(R.id.ib_see_on_the_map);
        ib_see_on_the_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent locationIntent = new Intent(RideDetailsActivity.this, RideLocationActivity.class);
                locationIntent.putExtra(RIDE_OFFER_INTENT, offer);
                startActivity(locationIntent);
            }
        });

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mDatabaseRef.child(Database.USERS);
        mRidesRef = mDatabaseRef.child(Database.RIDES);
        mNotificationsRef = mDatabaseRef.child(Database.NOTIFICATIONS);

        mNumOfSeatsPicked = 1;
    }

    private void showSpinnerDialog () {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final LayoutInflater inflater = getLayoutInflater();

        View vView = inflater.inflate(R.layout.dialog_seats_reservation, null);
        final Spinner spinner = vView.findViewById(R.id.spinner);

        int size = Integer.valueOf(offer.getSeats());
        fillListWithNumbers(size);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item,numbersList);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        spinner.setAdapter(adapter);

        final AlertDialog dialog;

        builder.setView(vView)
                .setPositiveButton("Book", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mNumOfSeatsPicked = spinner.getSelectedItemPosition() + 1;
                        bookSeats();
                        Toast.makeText(getApplicationContext(), "Booked: " + mNumOfSeatsPicked, Toast.LENGTH_SHORT).show();

                        // TODO startActivity or finish?
                        startActivity(new Intent(RideDetailsActivity.this, MainActivity.class));
                        // finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        dialog = builder.create();
        dialog.show();
    }

    private void fillListWithNumbers(int size)
    {
        numbersList = new ArrayList<String>();
        for (int i=0; i<size; i++)
        {
            switch (i)
            {
                case 0:
                    numbersList.add("1");
                    break;
                case 1:
                    numbersList.add("2");
                    break;
                case 2:
                    numbersList.add("3");
                    break;
                case 3:
                    numbersList.add("4");
                    break;
                case 4:
                    numbersList.add("5");
                    break;
                case 5:
                    numbersList.add("6");
                    break;
                case 6:
                    numbersList.add("7");
                    break;
            }
        }
    }

    private void fillRideDetails()
    {
        Picasso.get()
                .load(rider.getAvatarUrl())
                .placeholder(R.drawable.ic_account_circle_black_24dp)
                .error(R.drawable.ic_error_red_24dp)
                .transform(new CircleTransform()).into(iv_avatar);

        tv_nick.setText(rider.getFirstName() + " " + rider.getSurname());
        tv_available_seats.setText("Available seats: " + offer.getSeats());
        tv_distance.setText("Distance: " + offer.getDistance()/1000 + " km");
        tv_car_color.setText(offer.getCar().getColor());
        String rating = "<b>" + String.format("%.1f", rider.getDriverRatingAvg()) + "</b> ";
        tv_rating.setText(Html.fromHtml(rating));
        // kalendarz i data
        Calendar cal = DateUtils.getCalendarFromMilliSecs(offer.getDate());
        String startHour = DateUtils.getHourFromCalendar(cal);
        String startMin = DateUtils.getMinFromCalendar(cal);

        int durationHours = DateUtils.getDurationHoursFromLongSeconds(offer.getDuration());
        int durationMins = DateUtils.getDurationMinsFromLongSeconds(offer.getDuration());
        cal.add(Calendar.HOUR_OF_DAY, durationHours);
        cal.add(Calendar.MINUTE, durationMins);
        String arrivalHour = DateUtils.getHourFromCalendar(cal);
        String arrivalMin = DateUtils.getMinFromCalendar(cal);

        tv_hour_begin.setText(startHour + ":" + startMin);
        tv_hour_end.setText(arrivalHour + ":" + arrivalMin);
        tv_date.setText(DateUtils.getDate(offer.getDate(), DateUtils.STANDARD_DATE_FORMAT));
        tv_to.setText(GeoUtils.getCityFromLatLng(this, offer.getDestinationPoint().toLatLng()));
        tv_from.setText(GeoUtils.getCityFromLatLng(this, offer.getStartPoint().toLatLng()));

        tv_car.setText(offer.getCar().getBrand() + " " + offer.getCar().getModel());
        tv_luggage.setText("Luggage: " + offer.getLuggage());
        tv_message.setText(offer.getMessage());
        tv_price.setText(offer.getPrice() + " zł");

        tv_rating_quantity.setText(rider.getNumberOfDriverRatings() + " reviews");
        tv_telephone.setText("Tel: "+ rider.getPhone());
    }

    ////////////////////// FIREBASE ///////////////////

    private void bookSeats() {
        HashMap<String, Boolean> participatedRidesMap = CurrentUserProfile.participatedRidesMap;
        participatedRidesMap.put(mOffer.getKey(), false);

        CurrentUserProfile.participatedRidesMap = participatedRidesMap;

        mUsersRef.child(CurrentUserProfile.uid).child(Database.PARTICIPATED_RIDES).setValue(participatedRidesMap);

        int availableSeats = mOffer.getSeats();
        availableSeats -= mNumOfSeatsPicked;

        mOffer.setSeats(availableSeats);

        HashMap<String, Integer> passengers = mOffer.getPassengers();
        if (passengers == null) {
            passengers = new HashMap<>();
        }

        if (passengers.containsKey(CurrentUserProfile.uid)) {
            int alreadyReserved = passengers.get(CurrentUserProfile.uid);
            alreadyReserved += mNumOfSeatsPicked;
            passengers.put(CurrentUserProfile.uid, alreadyReserved);
        } else {
            passengers.put(CurrentUserProfile.uid, mNumOfSeatsPicked);
        }

        mOffer.setPassengers(passengers);

        mRidesRef.child(mOffer.getKey()).child(Database.PASSENGERS).setValue(passengers);
        mRidesRef.child(mOffer.getKey()).child(Database.SEATS).setValue(availableSeats);

        String newNotificationUid = mNotificationsRef.child(mDriver.getUid()).push().getKey();
        Notification notification = new Notification(
                Notification.NotificationType.NEW_PASSENGER,
                CurrentUserProfile.uid,
                mOffer.getKey(),
                mNumOfSeatsPicked);

        // HashMap<String, Boolean> driverNotifications = mDriver.getNotifications();
        // if (driverNotifications == null) {
        //     driverNotifications = new HashMap<>();
        // }
        // driverNotifications.put(newNotificationUid, false);

        mNotificationsRef.child(mDriver.getUid()).child(newNotificationUid).setValue(notification);
        // mUsersRef.child(mDriver.getUid()).child(Database.NOTIFICATIONS).setValue(driverNotifications);
    }

    private void firebaseInit() {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference usersRef = database.child(Database.USERS);

        final ValueEventListener usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds :dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    user.setUid(ds.getKey());
                    if (uid.equals(user.getUid()))
                    {
                        rider = user;
                        mDriver = rider;
                    }
                }
                fillRideDetails();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        final DatabaseReference offeredRidesRef = database.child(Database.RIDES);
        ValueEventListener ridesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds :dataSnapshot.getChildren()) {
                    RideOffer rideOffer = ds.getValue(RideOffer.class);
                    rideOffer.setKey(ds.getKey());
                    if (rideKey.equals(rideOffer.getKey()))
                    {
                        offer = rideOffer;
                        mOffer = offer;

                        uid = rideOffer.getDriverUid();
                        usersRef.addListenerForSingleValueEvent(usersListener);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        offeredRidesRef.addListenerForSingleValueEvent(ridesListener);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
