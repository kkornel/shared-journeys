package com.put.miasi.main.offer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.put.miasi.R;
import com.put.miasi.utils.Car;
import com.put.miasi.utils.OfferLog;
import com.put.miasi.utils.RideOffer;

import static com.put.miasi.main.offer.FromActivity.RIDE_OFFER_INTENT;

public class CarDetailsActivity extends AppCompatActivity {
    private static final String TAG = "CarDetailsActivity";

    private EditText mBrandEditText;
    private EditText mModelEditText;
    private EditText mColorEditText;
    private Spinner mSpaceSpinner;
    private Spinner mLuggageSpinner;
    private Button mNextButton;

    private RideOffer mRideOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_carDetails));

        mRideOffer = getIntent().getParcelableExtra(RIDE_OFFER_INTENT);
        OfferLog.d("onCreate: " + mRideOffer.toString());

        mBrandEditText = findViewById(R.id.brandEditText);
        mModelEditText = findViewById(R.id.modelEditText);
        mColorEditText = findViewById(R.id.colorEditText);

        mSpaceSpinner = findViewById(R.id.spaceSpinner);
        mSpaceSpinner.setSelection(2);

        mLuggageSpinner = findViewById(R.id.luggageSpinner);
        mLuggageSpinner.setSelection(2);

        mNextButton = findViewById(R.id.nextButton);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!areFormsValid()) {
                    Toast.makeText(getApplicationContext(), "Provide all details!", Toast.LENGTH_LONG).show();
                    return;
                }

                String brand = mBrandEditText.getText().toString();
                String model = mModelEditText.getText().toString();
                String color = mColorEditText.getText().toString();

                Car car = new Car(brand, model, color);
                mRideOffer.setCar(car);
                mRideOffer.setSeats(Integer.valueOf(mSpaceSpinner.getSelectedItem().toString()));
                mRideOffer.setLuggage(mLuggageSpinner.getSelectedItem().toString());

                Intent intent = new Intent(CarDetailsActivity.this, MessagePriceActivity.class);
                intent.putExtra(RIDE_OFFER_INTENT, mRideOffer);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean areFormsValid() {
        boolean areValid = true;

        if (mBrandEditText.getText().toString().equals("")) {
            mBrandEditText.setError(getString(R.string.required));
            areValid = false;
        } else {
            mBrandEditText.setError(null);
        }

        if (mModelEditText.getText().toString().equals("")) {
            mModelEditText.setError(getString(R.string.required));
            areValid = false;
        } else {
            mModelEditText.setError(null);
        }

        if (mColorEditText.getText().toString().equals("")) {
            mColorEditText.setError(getString(R.string.required));
            areValid = false;
        } else {
            mColorEditText.setError(null);
        }

        return areValid;
    }
}
