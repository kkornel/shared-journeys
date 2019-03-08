package com.put.miasi.main.search;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.put.miasi.R;

public class RideDetailsActivity extends AppCompatActivity
{
    private Button btn_reservation;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details);
        initializeComponents();
    }

    private void initializeComponents()
    {
        btn_reservation = (Button) findViewById(R.id.btn_reservation);
        btn_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RideDetailsActivity.this, SeatsReservationActivity.class));
            }
        });
    }
}
