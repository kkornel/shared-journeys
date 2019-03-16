package com.put.miasi.main.offer;

import android.content.Intent;
import java.util.Calendar;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.put.miasi.R;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.Logger;
import com.put.miasi.utils.RideOffer;

import java.util.Date;

import static com.put.miasi.main.offer.FromActivity.RIDE_OFFER_INTENT;

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
        Logger.d("MyDate", "*************************************************");
        Logger.d("MyDate", "DatePicker: " + mCalendarView.getDate());
        Logger.d("MyDate", "DatePicker: " + cl.toString());
        Logger.d("MyDate", "DatePicker: " + new Date(mCalendarView.getDate()));
        Logger.d("MyDate", "*************************************************");
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

        mRideOffer = (RideOffer) getIntent().getSerializableExtra(RIDE_OFFER_INTENT);

        Calendar currentCalendar = Calendar.getInstance();

        mCalendarView = findViewById(R.id.calendarView);
        mCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        mCalendarView.setMinDate(currentCalendar.getTimeInMillis());

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                long timePickedMilliSecs = DateUtils.getMilliSecondsFromDate(dayOfMonth, month, year);
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
