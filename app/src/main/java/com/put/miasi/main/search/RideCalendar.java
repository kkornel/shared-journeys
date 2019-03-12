package com.put.miasi.main.search;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.put.miasi.R;
import com.put.miasi.utils.DateUtils;

import java.util.Calendar;


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
                date = mTimePickedMilliSecs;
                Intent intent = new Intent(RideCalendar.this, SearchActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });
    }


}
