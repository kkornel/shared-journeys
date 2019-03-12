package com.put.miasi.main.search;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.put.miasi.R;
import com.put.miasi.main.MainActivity;
import com.put.miasi.utils.CurrentUserProfile;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.RideOffer;
import com.put.miasi.utils.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SeatsReservationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    private static final String TAG = "SeatsReservationActivit";

    private Spinner spinner = null;
    private ArrayAdapter<String> adapter = null;
    private List<String> numbersList = null;

    private Button mBookButton;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mRidesRef;

    private int mNumOfSeatsPicked;
    private User mDriver;
    private RideOffer mOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seats_reservation);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mDatabaseRef.child(Database.USERS);
        mRidesRef = mDatabaseRef.child(Database.RIDES);

        mBookButton = findViewById(R.id.btn_reservation);
        mBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookSeats();
            }
        });

        mNumOfSeatsPicked = 1;

        setPopUpSize();
        Intent intent = getIntent();
        String text = intent.getStringExtra("size");

        int size = Integer.valueOf(text);
        initalizeSpinner(size);

        mDriver = (User) getIntent().getSerializableExtra("driver");
        mOffer = (RideOffer) getIntent().getSerializableExtra("ride");

        Log.d(TAG, "onCreate: " + mDriver);
        Log.d(TAG, "onCreate: " + mOffer);
    }

    private void bookSeats() {
        HashMap<String, Boolean> participatedRidesMap = CurrentUserProfile.participatedRidesMap;
        participatedRidesMap.put(mOffer.getKey(), false);

        CurrentUserProfile.participatedRidesMap = participatedRidesMap;

        mUsersRef.child(CurrentUserProfile.uid).child(Database.PARTICIPATED_RIDES).setValue(participatedRidesMap);

        int availableSeats = mOffer.getSeats();
        availableSeats -= mNumOfSeatsPicked;

        mOffer.setSeats(availableSeats);

        HashMap<String, Integer> passengers = mOffer.getPassengers();
        if (passengers == null) {
            passengers = new HashMap<>();
        }
        passengers.put(CurrentUserProfile.uid, mNumOfSeatsPicked);
        mOffer.setPassengers(passengers);

        mRidesRef.child(mOffer.getKey()).child(Database.PASSENGERS).setValue(passengers);
        mRidesRef.child(mOffer.getKey()).child(Database.SEATS).setValue(availableSeats);

        Log.d(TAG, "bookSeats: " + mOffer);

        startActivity(new Intent(SeatsReservationActivity.this, MainActivity.class));
    }

    private void setPopUpSize()
    {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*0.8), (int) (height *0.6));
    }

    private void initalizeSpinner(int size)
    {
        fillListWithNumbers(size);

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item,numbersList);
        adapter.setDropDownViewResource(R.layout.spinner_item);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }
    private void fillListWithNumbers(int size)
    {
        numbersList = new ArrayList<String>();
        for (int i=0; i<size; i++)
        {
            switch (i)
            {
                case 0:
                    numbersList.add("1");
                    break;
                case 1:
                    numbersList.add("2");
                    break;
                case 2:
                    numbersList.add("3");
                    break;
                case 3:
                    numbersList.add("4");
                    break;
                case 4:
                    numbersList.add("5");
                    break;
                case 5:
                    numbersList.add("6");
                    break;
                case 6:
                    numbersList.add("7");
                    break;

            }

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mNumOfSeatsPicked = position + 1;

        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(SeatsReservationActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
