package com.put.miasi.main.offer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.put.miasi.R;
import com.put.miasi.utils.RideOffer;

import static com.put.miasi.main.offer.FromActivity.RIDE_OFFER_INTENT;

public class MessagePriceActivity extends AppCompatActivity {
    private static final String TAG = "MessagePriceActivity";

    private EditText mMessageEditText;
    private EditText mPriceEditText;
    private Button mNextButton;

    private RideOffer mRideOffer;

    // TODO remove
    void tests() {
        String msg = "Ride or die";
        String price = "39";
        int pricePerSeat = Integer.valueOf(price);

        mRideOffer.setMessage(msg);
        mRideOffer.setPrice(pricePerSeat);

        Intent intent = new Intent(MessagePriceActivity.this, OfferSummaryActivity.class);
        intent.putExtra(RIDE_OFFER_INTENT, mRideOffer);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_price);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_messagePrice));

        mRideOffer = (RideOffer) getIntent().getSerializableExtra(RIDE_OFFER_INTENT);

        mMessageEditText = findViewById(R.id.messageEditText);
        mPriceEditText = findViewById(R.id.priceEditText);

        mNextButton = findViewById(R.id.nextButton);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!areFormsValid()) {
                    Toast.makeText(getApplicationContext(), "Provide all details!", Toast.LENGTH_LONG).show();
                    return;
                }

                String msg = mMessageEditText.getText().toString();

                if (msg.equals("")) {
                    msg = null;
                }

                String price = mPriceEditText.getText().toString();
                int pricePerSeat = Integer.valueOf(price);

                mRideOffer.setMessage(msg);
                mRideOffer.setPrice(pricePerSeat);

                Intent intent = new Intent(MessagePriceActivity.this, OfferSummaryActivity.class);
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

    private boolean areFormsValid() {
        boolean areValid = true;

        if (mPriceEditText.getText().toString().equals("")) {
            mPriceEditText.setError(getString(R.string.required));
            areValid = false;
        } else {
            mPriceEditText.setError(null);
        }

        return areValid;
    }
}
