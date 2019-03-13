package com.put.miasi.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.put.miasi.R;

public class LocationUtils {
    private static final String TAG = "LocationUtils";

    public static final int REQUEST_CODE_FINE_LOCATION_PERMISSIONS = 53;

    public enum LocationErrorType {
        FIND_LOCATION_NOT_PERMITTED,
        LOCATION_SERVICE_IS_NOT_AVAILABLE,
        Unknown,
    }

    private static final int LOCATION_REQUEST_INTERVAL_MILLI_SECONDS = 10000;
    private static final int LOCATION_REQUEST_FASTEST_INTERVAL_MILLI_SECONDS =
            LOCATION_REQUEST_INTERVAL_MILLI_SECONDS / 2;
    private static int LOCATION_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY;

    private static LocationManager mLocationManager;

    private static FusedLocationProviderClient mFusedLocationClient;
    private static LocationRequest mLocationRequest;
    private static LocationCallback mLocationCallback;

    private static boolean mGpsEnabled = false;
    private static LocationErrorType mErrorType = null;

    public static Location lastKnowLocation = null;

    public static float distanceToYou(LatLng place) {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(place.latitude);
        location.setLongitude(place.longitude);
        return lastKnowLocation.distanceTo(location);
    }

    public static void findUserLocation(Activity activity, Context context) {
        if (mFusedLocationClient == null || mLocationManager == null) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            createLocationRequest();
        }

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                lastKnowLocation = locationResult.getLastLocation();
                stopLocationUpdates();
            }
        };

        try {
            mGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            mErrorType = LocationErrorType.LOCATION_SERVICE_IS_NOT_AVAILABLE;
        }

        if (!mGpsEnabled) {
            mErrorType = LocationErrorType.LOCATION_SERVICE_IS_NOT_AVAILABLE;
            return;
        }

        if (mGpsEnabled) {
            try {
                startLocationUpdates();
            } catch (SecurityException e) {
                mErrorType = LocationErrorType.FIND_LOCATION_NOT_PERMITTED;
            }
        }

        if (mErrorType == null) {
            getLastLocation(activity);
        }
    }

    private static void getLastLocation(Activity activity) {
        try {
            if (mGpsEnabled) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    lastKnowLocation = location;
                                }
                            }
                        });
            }
        } catch (SecurityException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    private static void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL_MILLI_SECONDS);
        mLocationRequest.setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL_MILLI_SECONDS);
        mLocationRequest.setPriority(LOCATION_ACCURACY);
    }

    private static void startLocationUpdates() {
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        } catch (SecurityException se) {
            Log.e(TAG, se.getMessage());
        }
    }

    private static void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public static boolean isGpsEnabled(final Context context) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }

    public static boolean hasLocationPermissions(Context context) {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }
}
