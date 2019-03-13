package com.put.miasi.main;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.put.miasi.BuildConfig;
import com.put.miasi.R;
import com.put.miasi.main.offer.FromActivity;
import com.put.miasi.main.search.RideCalendar;
import com.put.miasi.main.search.SearchActivity;
import com.put.miasi.utils.LocationUtils;

import static com.put.miasi.utils.LocationUtils.REQUEST_CODE_FINE_LOCATION_PERMISSIONS;


public class RidesFragment extends Fragment {
    private static final String TAG = "RidesFragment";

    private Button mOfferButton;
    private Button mSearchButton;

    public RidesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rides, container, false);

        mOfferButton = rootView.findViewById(R.id.offerButton);
        mOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FromActivity.class));
            }
        });



        mSearchButton = rootView.findViewById(R.id.searchButton);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RideCalendar.class));
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // if (!LocationUtils.hasLocationPermissions(getContext())) {
        //     LocationUtils.requestLocationPermissions(getActivity(), getContext(), mSearchButton);
        // }
        // if (!LocationUtils.isGpsEnabled(getContext())) {
        //     showNoGpsSnackBar();
        // } else if (LocationUtils.lastKnowLocation == null) {
        //     LocationUtils.findUserLocation(getActivity(), getContext());
        // }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_FINE_LOCATION_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted, yay! Do the
                        // location-related task you need to do.
                        LocationUtils.findUserLocation(getActivity(), getContext());
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(
                            mOfferButton,
                            R.string.permission_denied_explanation,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.settings, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    boolean shouldProvideRationale =
                                            shouldShowRequestPermissionRationale(
                                                    Manifest.permission.ACCESS_FINE_LOCATION);

                                    // Provide an additional rationale to the user. This would happen if the user denied the
                                    // request previously, but didn't check the "Don't ask again" checkbox.
                                    if (shouldProvideRationale) {
                                        // Show an explanation to the user *asynchronously* -- don't block
                                        // this thread waiting for the user's response! After the user
                                        // sees the explanation, try again to request the permission.
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                REQUEST_CODE_FINE_LOCATION_PERMISSIONS);
                                    } else {
                                        // Build intent that displays the App settings screen.
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package",
                                                BuildConfig.APPLICATION_ID, null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                }
                            })
                            .show();
                }
        }
    }

    private void showNoGpsSnackBar() {
        Snackbar.make(
                mOfferButton,
                R.string.enable_gps_to_check_weather,
                Snackbar.LENGTH_LONG)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openLocationSettings();
                    }
                })
                .show();
    }

    private void openLocationSettings() {
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }
}
