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
import android.widget.DatePicker;

import com.put.miasi.R;

import java.util.Locale;

public class DatePickerActivity extends AppCompatActivity {
    private static final String TAG = "DatePickerActivity";

    private CalendarView mCalendarView;
    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(getString(R.string.title_activity_datePicker));

        mCalendarView = findViewById(R.id.calendarView);

        //Initialize calendar with date
        Calendar currentCalendar = Calendar.getInstance(Locale.getDefault());

        //Show monday as first date of week
        mCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        mCalendarView.setMinDate(currentCalendar.getTimeInMillis());

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Log.d(TAG, "onSelectedDayChange: " + dayOfMonth + "/" + month + "/" + year);
                Log.d(TAG, "onSelectedDayChange: " + view.getDate());
                Log.d(TAG, "onSelectedDayChange: " + view.getDateTextAppearance());
            }
        });

        mNextButton = findViewById(R.id.nextButton);

        mNextButton.setEnabled(false);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Add extras to intent
                Intent intent = new Intent(DatePickerActivity.this, DatePicker.class);
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
