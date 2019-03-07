package com.put.miasi.main.search;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.put.miasi.R;

import java.util.ArrayList;
import java.util.List;

public class SeatsReservationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    private Spinner spinner = null;
    private ArrayAdapter<String> adapter = null;
    private List<String> numbersList = null;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seats_reservation);
        setPopUpSize();
        initalizeSpinner();


    }
    private void setPopUpSize()
    {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*0.8), (int) (height *0.6));
    }

    private void initalizeSpinner()
    {
        fillListWithNumbers(5);

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
                    numbersList.add("One");
                    break;
                case 1:
                    numbersList.add("Two");
                    break;
                case 2:
                    numbersList.add("Three");
                    break;
                case 3:
                    numbersList.add("Four");
                    break;
                case 4:
                    numbersList.add("Five");
                    break;
                case 5:
                    numbersList.add("Six");
                    break;
                case 6:
                    numbersList.add("Seven");
                    break;

            }

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(SeatsReservationActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
