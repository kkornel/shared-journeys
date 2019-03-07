package com.put.miasi.main.offer;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.put.miasi.R;
import com.put.miasi.utils.LatLon;
import com.put.miasi.utils.OfferLog;
import com.put.miasi.utils.RideOffer;

import java.util.Arrays;

import static com.put.miasi.main.offer.FromActivity.RIDE_OFFER_INTENT;

public class DestinationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "DestinationActivity";

    private static final float ZOOM_LEVEL = 17.0f;
    private static final float VERTICAL_BIAS = 0.5f;
    private static final int MARGIN_TOP = 64;
    private static final int MARGIN_BOTTOM = 32;

    private static String SEARCH_COUNTRY = "PL";
    private static String SEARCH_HINT = "e.g. Warszawa Żoliborz";

    private GoogleMap mMap;
    private Button mNextButton;

    private RideOffer mRideOffer;
    private LatLng mDestinationLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_destination));

        mRideOffer = getIntent().getParcelableExtra(RIDE_OFFER_INTENT);
        OfferLog.d("onCreate: " + mRideOffer.toString());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getView().setVisibility(View.INVISIBLE);

        // Initialize Places.
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

        // Create a new Places client instance.
        final PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));

        autocompleteFragment.setCountry(SEARCH_COUNTRY);
        autocompleteFragment.setHint(SEARCH_HINT);
        autocompleteFragment.getView().setBackgroundColor(getResources().getColor(R.color.colorSearchBackground));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                OfferLog.d("Place: " + place.getName() + ", " + place.getLatLng());

                mDestinationLatLng = place.getLatLng();

                mMap.addMarker(new MarkerOptions().position(mDestinationLatLng)
                        .title(place.getName()));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDestinationLatLng, ZOOM_LEVEL));

                mapFragment.getView().setVisibility(View.VISIBLE);

                ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.map, ConstraintSet.TOP, R.id.cardView, ConstraintSet.BOTTOM, MARGIN_TOP);
                constraintSet.connect(R.id.map, ConstraintSet.BOTTOM, R.id.nextButton, ConstraintSet.TOP, MARGIN_BOTTOM);
                constraintSet.setVerticalBias(R.id.map, VERTICAL_BIAS);
                constraintSet.applyTo(constraintLayout);

                mNextButton.setEnabled(true);
            }

            @Override
            public void onError(Status status) {
                OfferLog.d("An error occurred: " + status);
            }
        });

        mNextButton = findViewById(R.id.nextButton);
        mNextButton.setEnabled(false);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRideOffer.setDestinationPoint(new LatLon(mDestinationLatLng));

                Intent intent = new Intent(DestinationActivity.this, DatePickerActivity.class);
                intent.putExtra(RIDE_OFFER_INTENT, mRideOffer);
                startActivity(intent);
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
    }
}
