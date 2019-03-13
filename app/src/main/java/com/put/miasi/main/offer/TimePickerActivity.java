package com.put.miasi.main.offer;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.put.miasi.R;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.OfferLog;
import com.put.miasi.utils.RideOffer;
import com.put.miasi.utils.TimePickerFragment;

import java.util.Calendar;
import java.util.Date;

import static com.put.miasi.main.offer.FromActivity.RIDE_OFFER_INTENT;

public class TimePickerActivity extends AppCompatActivity implements TimePickerFragment.TimePickedListener {
    private static final String TAG = "TimePickerActivity";

    private TextView mSelectedTimeTextView;
    private Button mSelectTimeButton;
    private Button mNextButton;

    private RideOffer mRideOffer;
    private int mHour;
    private int mMin;

    // TODO remove
    void tests() {
        Calendar cl = Calendar.getInstance();
        OfferLog.d("MyDate", "*************************************************");
        OfferLog.d("MyDate", "TimePicker: " + cl.toString());

        cl.setTimeInMillis(mRideOffer.getDate());
        OfferLog.d("MyDate", "TimePicker: " + cl.toString());

        int year = cl.get(Calendar.YEAR);
        OfferLog.d("MyDate", "TimePicker: year " + year);

        int month = cl.get(Calendar.MONTH);
        OfferLog.d("MyDate", "TimePicker: month " + month);

        int day = cl.get(Calendar.DAY_OF_MONTH);
        OfferLog.d("MyDate", "TimePicker: day " + day);

        int hour = mHour;
        OfferLog.d("MyDate", "TimePicker: mHour " + mHour);
        int min = mMin;
        OfferLog.d("MyDate", "TimePicker: mMin " + mMin);

        cl.set(year, month, day, hour, min);

        mRideOffer.setDate(cl.getTime().getTime());

        // TODO remove
        // ********************************************************************

        OfferLog.d("MyDate", "TimePicker: " + cl.toString());
        OfferLog.d("MyDate", "TimePicker: " + new Date(cl.getTime().getTime()));
        OfferLog.d("MyDate", "*************************************************");
        // ********************************************************************

        Intent intent = new Intent(TimePickerActivity.this, CarDetailsActivity.class);
        intent.putExtra(RIDE_OFFER_INTENT, mRideOffer);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Ride Location");

        mRideOffer = (RideOffer) getIntent().getSerializableExtra(RIDE_OFFER_INTENT);

        mSelectedTimeTextView = findViewById(R.id.selectedTimeTextView);

        mSelectTimeButton = findViewById(R.id.selectTimeButton);
        mSelectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });

        mNextButton = findViewById(R.id.nextButton);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cl = Calendar.getInstance();
                cl.setTimeInMillis(mRideOffer.getDate());

                int year = cl.get(Calendar.YEAR);
                int month = cl.get(Calendar.MONTH);
                int day = cl.get(Calendar.DAY_OF_MONTH);
                int hour = mHour;
                int min = mMin;

                cl.set(year, month, day, hour, min);

                mRideOffer.setDate(cl.getTime().getTime());

                Intent intent = new Intent(TimePickerActivity.this, CarDetailsActivity.class);
                intent.putExtra(RIDE_OFFER_INTENT, mRideOffer);
                startActivity(intent);
            }
        });

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);

        mHour = hour;
        mMin = 0;

        String h = (hour < 10) ? DateUtils.convertSingleDateToDouble(hour) : String.valueOf(hour);

        mSelectedTimeTextView.setText(h + ":00");

        // TODO remove
        // tests();
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimePicked(int hourOfDay, int minute) {
        String h = (hourOfDay < 10) ? DateUtils.convertSingleDateToDouble(hourOfDay) : String.valueOf(hourOfDay);
        String m = (minute < 10) ? DateUtils.convertSingleDateToDouble(minute) : String.valueOf(minute);

        String time = h + ":" + m;
        mHour = hourOfDay;
        mMin = minute;
        mSelectedTimeTextView.setText(time);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
