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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.put.miasi.BuildConfig;
import com.put.miasi.R;
import com.put.miasi.main.offer.FromActivity;
import com.put.miasi.main.search.RideCalendar;
import com.put.miasi.utils.CurrentUserProfile;
import com.put.miasi.utils.Database;
import com.put.miasi.utils.LocationUtils;
import com.put.miasi.utils.Notification;

import static com.put.miasi.utils.LocationUtils.REQUEST_CODE_FINE_LOCATION_PERMISSIONS;


public class RidesFragment extends Fragment {
    private static final String TAG = "RidesFragment";

    private Button mTestbtn;
    private Button mOfferButton;
    private Button mSearchButton;
    
    private Snackbar mSnackbar;

    private DatabaseReference mRootRef;
    private DatabaseReference mRidesRef;
    private DatabaseReference mUsersRef;
    private DatabaseReference mNotificationsRef;

    public RidesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rides, container, false);

        Log.d(TAG, "onCreateView: ");

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = mRootRef.child(Database.USERS);
        mRidesRef = mRootRef.child(Database.RIDES);
        mNotificationsRef = mRootRef.child(Database.NOTIFICATIONS);

        mTestbtn = rootView.findViewById(R.id.testButton);
        mTestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(getActivity(), MapsActivity.class));

                Notification notification = new Notification();
                notification.setSenderUid(CurrentUserProfile.uid);
                notification.setRate(32.0f);
                notification.setRideUid("asddsfv3wd43243");
                notification.setNotificationType(Notification.NotificationType.RATED_AS_DRIVER);

                String newUid = mNotificationsRef.push().getKey();
                mNotificationsRef.child(newUid).setValue(notification);

                CurrentUserProfile.notificationsMap.put(newUid, false);
                mUsersRef.child(CurrentUserProfile.uid).child(Database.NOTIFICATIONS).setValue(CurrentUserProfile.notificationsMap);

            }

        });

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
                if (!LocationUtils.hasLocationPermissions(getContext())) {
                    requestLocationPermissions();
                    return;
                }
                if (!LocationUtils.isGpsEnabled(getContext())) {
                    showNoGpsSnackBar();
                    return;
                }
                LocationUtils.findUserLocation(getActivity(), getContext());

                startActivity(new Intent(getActivity(), RideCalendar.class));
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");

        if (!LocationUtils.hasLocationPermissions(getContext())) {
            requestLocationPermissions();
            return;
        }
        if (!LocationUtils.isGpsEnabled(getContext())) {
            showNoGpsSnackBar();
            return;
        }
        LocationUtils.findUserLocation(getActivity(), getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
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

                        // Log.d(TAG, "onRequestPermissionsResult: if");
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    // Log.d(TAG, "onRequestPermissionsResult: else");
                    mSnackbar = Snackbar.make(
                            getActivity().findViewById(R.id.container),
                            R.string.permission_denied_explanation,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("Allow", new View.OnClickListener() {
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
                            });
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mSnackbar.getView().getLayoutParams();
                    layoutParams.setAnchorId(R.id.navigation);
                    layoutParams.anchorGravity = Gravity.TOP;
                    layoutParams.gravity = Gravity.TOP;
                    mSnackbar.getView().setLayoutParams(layoutParams);
                    mSnackbar.show();
                }
        }
    }

    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?

            // Log.d(TAG, "requestLocationPermissions: if");
            boolean shouldProvideRationale = shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION);

            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            if (shouldProvideRationale) {
                // Log.d(TAG, "requestLocationPermissions: if if");

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                mSnackbar = Snackbar.make(
                        getActivity().findViewById(R.id.container),
                        R.string.permission_rationale,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestPermissions(
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE_FINE_LOCATION_PERMISSIONS);
                            }
                        });

                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mSnackbar.getView().getLayoutParams();
                layoutParams.setAnchorId(R.id.navigation);
                layoutParams.anchorGravity = Gravity.TOP;
                layoutParams.gravity = Gravity.TOP;
                mSnackbar.getView().setLayoutParams(layoutParams);
                mSnackbar.show();
            } else {
                // No explanation needed; request the permission
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                // Request permission. It's possible this can be auto answered if device policy
                // sets the permission in a given state or the user denied the permission
                // previously and checked "Never ask again".

                // Log.d(TAG, "requestLocationPermissions: if else");
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_FINE_LOCATION_PERMISSIONS);
            }
        } else {
            // Log.d(TAG, "requestLocationPermissions: else ");
            LocationUtils.findUserLocation(getActivity(), getContext());
        }
    }

    private void showNoGpsSnackBar() {
        mSnackbar = Snackbar.make(
                getActivity().findViewById(R.id.container),
                R.string.enable_gps_to_check_weather,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openLocationSettings();
                    }
                });

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mSnackbar.getView().getLayoutParams();
        layoutParams.setAnchorId(R.id.navigation);
        layoutParams.anchorGravity = Gravity.TOP;
        layoutParams.gravity = Gravity.TOP;
        mSnackbar.getView().setLayoutParams(layoutParams);
        mSnackbar.show();
    }

    private void openLocationSettings() {
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }
}
