package com.put.miasi.main.offer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.put.miasi.R;
import com.put.miasi.main.MainActivity;

public class OfferSummaryActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "OfferSummaryActivity";

    private static final float ZOOM_LEVEL = 17.0f;
    private static final float VERTICAL_BIAS = 0.5f;
    private static final int MARKER_MAP_PADDING = 200;

    private TextView mStartTextView;
    private TextView mDestinationTextView;
    private TextView mDateTextView;
    private TextView mTimeTextView;
    private TextView mCarDetailsTextView;
    private TextView mSeatsTextView;
    private TextView mLuggageTextView;
    private TextView mPriceTextView;
    private TextView mMessageTextView;

    private GoogleMap mMap;

    private Button mPublishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_summary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(getString(R.string.title_activity_offerSummary));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mStartTextView = findViewById(R.id.startTextView);
        mDestinationTextView = findViewById(R.id.destinationTextView);
        mDateTextView = findViewById(R.id.dateTextView);
        mTimeTextView = findViewById(R.id.timeTextView);
        mCarDetailsTextView = findViewById(R.id.carDetailsTextView);
        mSeatsTextView = findViewById(R.id.seatsTextView);
        mLuggageTextView = findViewById(R.id.luggageTextView);
        mPriceTextView = findViewById(R.id.priceTextView);
        mMessageTextView = findViewById(R.id.messageTextView);

        mStartTextView.setText("Poznan");
        mDestinationTextView.setText("Wawa");
        mDateTextView.setText("16/03/19");
        mTimeTextView.setText("16:30");
        mCarDetailsTextView.setText("Opel Corsa Silver");
        mSeatsTextView.setText("3");
        mLuggageTextView.setText("Small");
        mPriceTextView.setText("40");
        mMessageTextView.setText("Ride or die");

        mPublishButton = findViewById(R.id.publishButton);

        mPublishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Add extras to intent
                Toast.makeText(getApplicationContext(), "Published!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OfferSummaryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        // TODO Add extras to bundle
        onBackPressed();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
