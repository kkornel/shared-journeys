package com.put.miasi.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class GeoUtils {

    public static String getCityFromLatLng(Context context, LatLng location) {
        List<Address> addressList = null;

        Geocoder geocoder = new Geocoder(context);
        try {
            addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addressList.get(0);

        return address.getLocality();
    }

}
