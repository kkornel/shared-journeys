package com.put.miasi.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.put.miasi.R;

public class LocationUtils {
    public static final int REQUEST_CODE_FINE_LOCATION_PERMISSIONS = 53;


    public static boolean hasLocationPermissions(Context context) {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static void requestLocationPermissions(final Activity activity, Context context, View view) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION);

            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            if (shouldProvideRationale) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                Snackbar.make(
                        view,
                        R.string.permission_rationale,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(
                                        activity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE_FINE_LOCATION_PERMISSIONS);
                            }
                        })
                        .show();
            } else {
                // No explanation needed; request the permission
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
                // Request permission. It's possible this can be auto answered if device policy
                // sets the permission in a given state or the user denied the permission
                // previously and checked "Never ask again".
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_FINE_LOCATION_PERMISSIONS);
            }
        } else {

        }
    }
}
