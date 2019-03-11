package com.put.miasi.main.search;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.google.android.gms.common.util.DataUtils;
import com.put.miasi.R;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.OfferLog;

import java.util.Calendar;
import java.util.Locale;

import static com.put.miasi.utils.DateUtils.STANDARD_DATE_TIME_FORMAT;

public class RideCalendar extends AppCompatActivity {

    private CalendarView mCalendarView;
    private Button mNextButton;

    private long date;
    private long mTimePickedMilliSecs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_calendar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_datePicker));


        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());

        mCalendarView = findViewById(R.id.calendarView);
        mCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        //mCalendarView.setMinDate(currentCalendar.getTimeInMillis());
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
                date = mTimePickedMilliSecs;
                Intent intent = new Intent(RideCalendar.this, SearchActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
                //Toast.makeText(RideCalendar.this, "Date: "+ DateUtils.getDate(date,DateUtils.STANDARD_DATE_FORMAT), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
