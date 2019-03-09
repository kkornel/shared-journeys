package com.put.miasi.main.history;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.put.miasi.R;
import com.put.miasi.utils.OfferLog;
import com.put.miasi.utils.RideOffer;

import static com.put.miasi.main.history.HistoryTabFragment.RIDE_INTENT_EXTRA;

public class ParticipatedRideDetailsActivity extends AppCompatActivity {

    private RideOffer mRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participated_ride_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_offerSummary));

        mRide = getIntent().getParcelableExtra(RIDE_INTENT_EXTRA);

        OfferLog.d("beep", mRide.toString());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
