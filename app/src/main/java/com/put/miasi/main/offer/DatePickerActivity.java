package com.put.miasi.main.offer;

import android.content.Intent;
import java.util.Calendar;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.google.android.gms.maps.model.LatLng;
import com.put.miasi.R;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.LatLon;
import com.put.miasi.utils.OfferLog;
import com.put.miasi.utils.RideOffer;

import java.util.Date;
import java.util.Locale;

import static com.put.miasi.main.offer.FromActivity.RIDE_OFFER_INTENT;
import static com.put.miasi.utils.DateUtils.STANDARD_DATE_TIME_FORMAT;

public class DatePickerActivity extends AppCompatActivity {
    private static final String TAG = "DatePickerActivity";

    private CalendarView mCalendarView;
    private Button mNextButton;

    private RideOffer mRideOffer;
    private long mTimePickedMilliSecs;

    // TODO remove
    void tests() {
        if (mTimePickedMilliSecs == 0) {
            mTimePickedMilliSecs = mCalendarView.getDate();
        }
        mRideOffer.setDate(mTimePickedMilliSecs);

        Calendar cl = Calendar.getInstance();

        // TODO remove
        // ********************************************************************
        OfferLog.d("MyDate", "*************************************************");
        OfferLog.d("MyDate", "DatePicker: " + mCalendarView.getDate());
        OfferLog.d("MyDate", "DatePicker: " + cl.toString());
        OfferLog.d("MyDate", "DatePicker: " + new Date(mCalendarView.getDate()));
        OfferLog.d("MyDate", "*************************************************");
        // ********************************************************************

        Intent intent = new Intent(DatePickerActivity.this, TimePickerActivity.class);
        intent.putExtra(RIDE_OFFER_INTENT, mRideOffer);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_datePicker));

        // mRideOffer = getIntent().getParcelableExtra(RIDE_OFFER_INTENT);
        mRideOffer = (RideOffer) getIntent().getSerializableExtra(RIDE_OFFER_INTENT);
        OfferLog.d( "onCreate: " + mRideOffer.toString());

        Calendar currentCalendar = Calendar.getInstance();

        mCalendarView = findViewById(R.id.calendarView);
        mCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        mCalendarView.setMinDate(currentCalendar.getTimeInMillis());
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                OfferLog.d("onSelectedDayChange: " + dayOfMonth + "/" + month + "/" + year);
                long timePickedMilliSecs = DateUtils.getMilliSecondsFromDate(dayOfMonth, month, year);
                OfferLog.d( "onSelectedDayChange: " + DateUtils.getDate(timePickedMilliSecs, STANDARD_DATE_TIME_FORMAT));
                mTimePickedMilliSecs = timePickedMilliSecs;
            }
        });

        mNextButton = findViewById(R.id.nextButton);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimePickedMilliSecs == 0) {
                    mTimePickedMilliSecs = mCalendarView.getDate();
                }
                mRideOffer.setDate(mTimePickedMilliSecs);

                Intent intent = new Intent(DatePickerActivity.this, TimePickerActivity.class);
                intent.putExtra(RIDE_OFFER_INTENT, mRideOffer);
                startActivity(intent);
            }
        });

        // TODO remove
        // tests();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
