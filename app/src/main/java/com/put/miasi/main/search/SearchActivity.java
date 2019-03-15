package com.put.miasi.main.search;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.put.miasi.R;
import com.put.miasi.utils.CircleTransform;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.DateUtils;
import com.put.miasi.utils.GeoUtils;
import com.put.miasi.utils.RideOffer;
import com.put.miasi.utils.TimePickerFragment;
import com.put.miasi.utils.User;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements TimePickerFragment.TimePickedListener
{

    private String TAG = "SearchActivity";
    private String date;
    long dateInMs;
    private Button btn_search;

    private String startCity;
    private String destinationCity;
    boolean startTextFilled = false;
    boolean destinationTextFilled = false;

    private TextView mSelectedTimeTextView;
    private Button mSelectTimeButton;
    private int mHour;
    private int mMin;

    private static String SEARCH_COUNTRY = "PL";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        dateInMs = intent.getLongExtra("date",0);
        date = DateUtils.getDate(dateInMs, DateUtils.STANDARD_DATE_FORMAT);



        initializeSearchButton();
        initializeAutocompleteFragment("start");
        initializeAutocompleteFragment("destination");
        initializeTimer();




    }

    public void initializeSearchButton()
    {
        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setVisibility(View.INVISIBLE);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent offerListIntent = new Intent(SearchActivity.this, OffersListActivity.class);
                offerListIntent.putExtra("date", date);
                offerListIntent.putExtra("startCity", startCity);
                offerListIntent.putExtra("destinationCity", destinationCity);
                offerListIntent.putExtra("hour", mHour);
                offerListIntent.putExtra("min", mMin);
                startActivity(offerListIntent);


            }
        });
    }



    private void initializeAutocompleteFragment(final String whichOne)
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Search for available rides");
        // Initialize Places.
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        // Create a new Places client instance.
        final PlacesClient placesClient = Places.createClient(this);
        AutocompleteSupportFragment autocompleteFragment = null;
        // Initialize the AutocompleteSupportFragment.
        if (whichOne.equals("start"))
        {
            autocompleteFragment = (AutocompleteSupportFragment)
                    getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
            autocompleteFragment.setHint("e.g. Poznań");
        }
        if (whichOne.equals("destination"))
        {
            autocompleteFragment = (AutocompleteSupportFragment)
                    getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment2);
            autocompleteFragment.setHint("e.g. Bydgoszcz");
        }

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setCountry(SEARCH_COUNTRY);
        autocompleteFragment.getView().setBackgroundColor(getResources().getColor(R.color.colorSearchBackground));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place)
            {
                if (whichOne.equals("start"))
                {
                    startTextFilled = true;
                    startCity = place.getName();
                }
                if (whichOne.equals("destination"))
                {
                    destinationTextFilled = true;
                    destinationCity = place.getName();
                }
                if (destinationTextFilled  && startTextFilled)
                {
                    btn_search.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Status status) {
                Log.d(TAG,"An error occurred: " + status);
            }
        });
    }



    private void initializeTimer()
    {
        mSelectedTimeTextView = findViewById(R.id.selectedTimeTextView);
        mSelectTimeButton = findViewById(R.id.selectTimeButton);
        mSelectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        mHour = hour;
        mMin = min;
        String h = (hour < 10) ? DateUtils.convertSingleDateToDouble(hour) : String.valueOf(hour);
        String m = (min < 10) ? DateUtils.convertSingleDateToDouble(min) : String.valueOf(min);
        mSelectedTimeTextView.setText("Time: " + h + ":" + m);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onTimePicked(int hourOfDay, int minute) {
        String h = (hourOfDay < 10) ? DateUtils.convertSingleDateToDouble(hourOfDay) : String.valueOf(hourOfDay);
        String m = (minute < 10) ? DateUtils.convertSingleDateToDouble(minute) : String.valueOf(minute);

        final Calendar c = Calendar.getInstance();

        //data
        long currentDateInMs = DateUtils.getMilliSecondsFromDate(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR));
        String currentDate = DateUtils.getDate(currentDateInMs, DateUtils.STANDARD_DATE_FORMAT);

        //czas
        long currentTimeInMs = c.getTimeInMillis();

        String godzinki = String.valueOf(DateUtils.getDate(currentTimeInMs,DateUtils.STANDARD_TIME_FORMAT)).substring(0,2);
        String minutki = String.valueOf(DateUtils.getDate(currentTimeInMs,DateUtils.STANDARD_TIME_FORMAT)).substring(3);


        Log.i(TAG,godzinki+minutki);
        Log.i(TAG,String.valueOf(hourOfDay)+String.valueOf(minute));





        if (!currentDate.equals(date))
        {
            String time = h + ":" + m;
            mHour = hourOfDay;
            mMin = minute;
            mSelectedTimeTextView.setText("Time: " + time);
        }
        else
        {
            if (hourOfDay > Integer.valueOf(godzinki))
            {
                String time = h + ":" + m;
                mHour = hourOfDay;
                mMin = minute;
                mSelectedTimeTextView.setText("Time: " + time);
            }
            else if (hourOfDay == Integer.valueOf(godzinki))
            {
                if (minute >= Integer.valueOf(minutki))
                {
                    String time = h + ":" + m;
                    mHour = hourOfDay;
                    mMin = minute;
                    mSelectedTimeTextView.setText("Time: " + time);
                }
                else
                {
                    Toast.makeText(SearchActivity.this, "Those rides passed! Choose correct time.", Toast.LENGTH_LONG).show();
                }

            }
            else
            {
                Toast.makeText(SearchActivity.this, "Those rides passed! Choose correct time.", Toast.LENGTH_LONG).show();
            }

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



}
