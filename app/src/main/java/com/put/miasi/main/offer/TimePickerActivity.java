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
import com.put.miasi.utils.TimePickerFragment;

import java.util.Calendar;

public class TimePickerActivity extends AppCompatActivity implements TimePickerFragment.TimePickedListener {

    private TextView mSelectedTimeTextView;
    private Button mSelectTimeButton;
    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(getString(R.string.title_activity_timePicker));



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
                // TODO Add extras to intent
                Intent intent = new Intent(TimePickerActivity.this, CarDetailsActivity.class);
                startActivity(intent);
            }
        });

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY) + 1;

        String h = (hour < 10) ? DateUtils.convertSingleDateToDouble(hour) : String.valueOf(hour);

        mSelectedTimeTextView.setText(h + ":00");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimePicked(String time) {
        mSelectedTimeTextView.setText(time);

    }

    @Override
    public boolean onSupportNavigateUp() {
        // TODO Add extras to bundle
        onBackPressed();
        return true;
    }
}
