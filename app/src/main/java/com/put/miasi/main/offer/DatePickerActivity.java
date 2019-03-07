package com.put.miasi.main.offer;

import android.content.Intent;
import java.util.Calendar;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.put.miasi.R;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.OfferLog;
import com.put.miasi.utils.RideOffer;

import java.util.Locale;

import static com.put.miasi.main.offer.FromActivity.RIDE_OFFER_INTENT;

public class DatePickerActivity extends AppCompatActivity {
    private static final String TAG = "DatePickerActivity";

    private CalendarView mCalendarView;
    private Button mNextButton;

    private long mTimePickedMilliSecs;

    private RideOffer mRideOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(getString(R.string.title_activity_datePicker));

        mCalendarView = findViewById(R.id.calendarView);

        mRideOffer = getIntent().getParcelableExtra(RIDE_OFFER_INTENT);
        OfferLog.d( "onCreate: " + mRideOffer.toString());

        //Initialize calendar with date
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());

        //Show monday as first date of week
        mCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        mCalendarView.setMinDate(currentCalendar.getTimeInMillis());

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                OfferLog.d("onSelectedDayChange: " + dayOfMonth + "/" + month + "/" + year);

                String d = DateUtils.getWithLeadingZero(dayOfMonth);
                String m = DateUtils.getWithLeadingZero(month);
                String y = String.valueOf(year);

                long timePickedMilliSecs = DateUtils.getMilliSecondsFromDate(dayOfMonth, month, year);

                OfferLog.d( "onSelectedDayChange: " + DateUtils.getDate(timePickedMilliSecs, "dd/MM/yyyy hh:mm:ss.SSS"));

                mTimePickedMilliSecs = timePickedMilliSecs;

            }
        });

        mNextButton = findViewById(R.id.nextButton);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Add extras to intent

                if (mTimePickedMilliSecs == 0) {
                    mTimePickedMilliSecs = mCalendarView.getDate();
                }

                mRideOffer.setDate(mTimePickedMilliSecs);


                Intent intent = new Intent(DatePickerActivity.this, TimePickerActivity.class);
                intent.putExtra(RIDE_OFFER_INTENT, mRideOffer);
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
}
