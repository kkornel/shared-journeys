package com.put.miasi.main.offer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.put.miasi.R;
import com.put.miasi.main.MainActivity;
import com.put.miasi.utils.Car;
import com.put.miasi.utils.CurrentUserProfile;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.FetchURL;
import com.put.miasi.utils.GeoUtils;
import com.put.miasi.utils.LatLon;
import com.put.miasi.utils.OfferLog;
import com.put.miasi.utils.RideOffer;
import com.put.miasi.utils.TaskLoadedCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.put.miasi.main.offer.FromActivity.RIDE_OFFER_INTENT;
import static com.put.miasi.utils.Database.OFFERED_RIDES;

public class OfferSummaryActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private static final String TAG = "OfferSummaryActivity";

    private static final int MARKER_MAP_PADDING = 200;

    private TextView mStartTextView;
    private TextView mDestinationTextView;
    private TextView mDistanceTextView;
    private TextView mDurationTextView;
    private TextView mDateTextView;
    private TextView mTimeTextView;
    private TextView mCarDetailsTextView;
    private TextView mSeatsTextView;
    private TextView mLuggageTextView;
    private TextView mPriceTextView;
    private TextView mMessageTextView;

    private GoogleMap mMap;
    private Button mPublishButton;

    private RideOffer mRideOffer;
    private LatLng mStartLatLng;
    private LatLng mDestLatLng;
    private Polyline currentPolyline;

    // public static String DISTANCE;
    public static long DISTANCE;
    public static long DURATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_summary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_offerSummary));

        // mRideOffer = getIntent().getParcelableExtra(RIDE_OFFER_INTENT);
        mRideOffer = (RideOffer) getIntent().getSerializableExtra(RIDE_OFFER_INTENT);
        OfferLog.d("onCreate: " + mRideOffer.toString());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mStartTextView = findViewById(R.id.startTextView);
        mDestinationTextView = findViewById(R.id.destinationTextView);
        mDistanceTextView = findViewById(R.id.seatsTextViewLabel);
        mDurationTextView = findViewById(R.id.durationTextView);
        mDateTextView = findViewById(R.id.dateTextView);
        mTimeTextView = findViewById(R.id.timeTextView);
        mCarDetailsTextView = findViewById(R.id.carDetailsTextView);
        mSeatsTextView = findViewById(R.id.seatsTextView);
        mLuggageTextView = findViewById(R.id.luggageTextView);
        mPriceTextView = findViewById(R.id.priceTextView);
        mMessageTextView = findViewById(R.id.messageTextView);

        unpackIntentSummary();

        mPublishButton = findViewById(R.id.publishButton);
        mPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Marker startMarker = mMap.addMarker(new MarkerOptions()
                .position(mStartLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Start"));

        Marker destMarker = mMap.addMarker(new MarkerOptions()
                .position(mDestLatLng)
                .title("Destination"));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(startMarker.getPosition());
        builder.include(destMarker.getPosition());

        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, MARKER_MAP_PADDING));

        new FetchURL(OfferSummaryActivity.this).execute(getUrl(mStartLatLng, mDestLatLng, "driving"), "driving");
    }

    private void publish() {
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        final String key = database.child(Database.RIDES).push().getKey();

        HashMap<String, Boolean> offeredRides = CurrentUserProfile.offeredRidesMap;

        if (offeredRides == null) {
            offeredRides = new HashMap<>();
        }

        offeredRides.put(key, false);

        mRideOffer.setDriverUid(CurrentUserProfile.uid);
        mRideOffer.setDistance(DISTANCE);
        mRideOffer.setDuration(DURATION);


        final DatabaseReference userRef = database.child(Database.USERS).child(CurrentUserProfile.uid);

        // userRef.child(OFFERED_RIDES).setValue(offeredRides).addOnSuccessListener(new OnSuccessListener<Void>() {
        //     @Override
        //     public void onSuccess(Void aVoid) {
        //         database.child(Database.RIDES).child(key).setValue(mRideOffer).addOnSuccessListener(new OnSuccessListener<Void>() {
        //             @Override
        //             public void onSuccess(Void aVoid) {
        //                 Toast.makeText(getApplicationContext(), "Published!", Toast.LENGTH_SHORT).show();
        //                 Intent intent = new Intent(OfferSummaryActivity.this, MainActivity.class);
        //                 startActivity(intent);
        //             }
        //         });
        //     }
        // });

        userRef.child(OFFERED_RIDES).setValue(offeredRides).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    database.child(Database.RIDES).child(key).setValue(mRideOffer).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Published!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(OfferSummaryActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }

    private void unpackIntentSummary() {
        LatLon startPoint = mRideOffer.getStartPoint();
        mStartLatLng = startPoint.toLatLng();
        LatLon destinationPoint = mRideOffer.getDestinationPoint();
        mDestLatLng = destinationPoint.toLatLng();
        long date = mRideOffer.getDate();
        Calendar calendar = DateUtils.getCalendarFromMilliSecs(date);
        String year = DateUtils.getYearFromCalendar(calendar);
        String month = DateUtils.getMonthFromCalendar(calendar);
        String day = DateUtils.getDayFromCalendar(calendar);
        String hour = DateUtils.getHourFromCalendar(calendar);
        String min = DateUtils.getMinFromCalendar(calendar);

        // TODO remove
        // ********************************************************************
        OfferLog.d("MyDate", "*************************************************");
        OfferLog.d("MyDate", "Summary: " + date);
        OfferLog.d("MyDate", "Summary: " + calendar.toString());
        OfferLog.d("MyDate", "Summary: " + new Date(date));
        OfferLog.d("MyDate", "Summary: year " + year);
        OfferLog.d("MyDate", "Summary: month " + month);
        OfferLog.d("MyDate", "Summary: day " + day);
        OfferLog.d("MyDate", "Summary: hour " + hour);
        OfferLog.d("MyDate", "Summary: min " + min);
        OfferLog.d("MyDate", "*************************************************");
        // ********************************************************************

        Car car = mRideOffer.getCar();
        String carString = car.getBrand() + " " + car.getModel() + " " + car.getColor();

        int seats = mRideOffer.getSeats();
        String luggage = mRideOffer.getLuggage();
        int price = mRideOffer.getPrice();
        String message = mRideOffer.getMessage();


        // List<Address> startAddressList = null;
        // List<Address> destAddressList = null;
        //
        // Geocoder startGeocoder = new Geocoder(OfferSummaryActivity.this);
        // Geocoder destGeocoder = new Geocoder(OfferSummaryActivity.this);
        // try {
        //     startAddressList = startGeocoder.getFromLocation(startPoint.getLatitude(), startPoint.getLongitude(), 1);
        //     destAddressList = destGeocoder.getFromLocation(destinationPoint.getLatitude(), destinationPoint.getLongitude(), 1);
        //
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        // Address startAddress = startAddressList.get(0);
        // Address destAddress = destAddressList.get(0);
        //
        // String startCity = startAddress.getLocality();
        // String destCity = destAddress.getLocality();

        // OfferLog.d("onPlaceSelected: " + startAddress.toString());

        String startCity = GeoUtils.getCityFromLatLng(OfferSummaryActivity.this, mStartLatLng);
        String destCity = GeoUtils.getCityFromLatLng(OfferSummaryActivity.this, mDestLatLng);

        mStartTextView.setText(startCity);
        mDestinationTextView.setText(destCity);
        mDateTextView.setText(day + "/" + month + "/" + year);
        mTimeTextView.setText(hour + ":" + min);
        mCarDetailsTextView.setText(carString);
        mSeatsTextView.setText(String.valueOf(seats));
        mLuggageTextView.setText(luggage);
        mPriceTextView.setText(String.valueOf(price));

        if (message == null || message.equals("")) {
            mMessageTextView.setText("No message for passengers.");
        } else {
            mMessageTextView.setText(message);
        }
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        OfferLog.d(url);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        String distance = DateUtils.getStringDistanceFromLongMeters(DISTANCE);
        String duration = DateUtils.getStringDurationFromLongSeconds(DURATION);

        mDistanceTextView.setText(distance);
        mDurationTextView.setText(duration);

        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
