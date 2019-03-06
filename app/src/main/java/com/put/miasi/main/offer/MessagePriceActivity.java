package com.put.miasi.main.offer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.put.miasi.R;

public class MessagePriceActivity extends AppCompatActivity {

    private EditText mMessageEditText;
    private EditText mPriceEditText;
    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_price);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(getString(R.string.title_activity_messagePrice));

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

                // TODO Add extras to intent
                Intent intent = new Intent(MessagePriceActivity.this, OfferSummaryActivity.class);
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
