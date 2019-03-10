package com.put.miasi.main.history;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.put.miasi.R;
import com.put.miasi.utils.OfferLog;
import com.put.miasi.utils.RideOffer;

import static com.put.miasi.main.history.HistoryTabFragment.RATED_RIDE_INTENT_EXTRA;
import static com.put.miasi.main.history.HistoryTabFragment.RIDE_INTENT_EXTRA;

public class OfferedRideDetailsActivity extends AppCompatActivity {

    private RideOffer mRide;
    private boolean mIsAlreadyRated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offered_ride_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_offerSummary));

        mRide = (RideOffer) getIntent().getSerializableExtra(RIDE_INTENT_EXTRA);
        mIsAlreadyRated = getIntent().getBooleanExtra(RATED_RIDE_INTENT_EXTRA, false);

        OfferLog.d("beep", mRide.toString());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
