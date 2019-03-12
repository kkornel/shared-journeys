package com.put.miasi.main.search;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.put.miasi.R;
import com.put.miasi.utils.RideOffer;

import static com.put.miasi.main.offer.FromActivity.RIDE_OFFER_INTENT;

public class RideLocationActivity extends AppCompatActivity {
    private RideOffer mRideOffer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_location);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_from));

        mRideOffer = (RideOffer) getIntent().getSerializableExtra(RIDE_OFFER_INTENT);
        mRideOffer.getStartPoint().getLatitude();

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
