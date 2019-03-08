package com.put.miasi.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.math.BigDecimal;

public class Utils {

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(Fragment fragment) {
        InputMethodManager imm = (InputMethodManager) fragment.getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(fragment.getView().getRootView().getWindowToken(), 0);
    }

    public static String getStringDistanceFromLongMeters(long m) {
        return m / 1000 + " km";
    }

    public static String getStringDuartionFromLongSeconds(long s) {
        Log.d("qwerty", "s=" + s);
        double duration = s / 3600.0;
        Log.d("qwerty", "duration=" + duration);
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(duration));
        int intValue = bigDecimal.intValue();
        Log.d("qwerty", "intValue=" + intValue);
        double doubleValue = bigDecimal.subtract(new BigDecimal(intValue)).doubleValue();
        Log.d("qwerty", "doubleValue=" + doubleValue);
        double decimalTime = doubleValue * 6;
        double d = decimalTime * 10;
        int min = (int) d;
        Log.d("qwerty", "decimalTime=" + decimalTime);
        Log.d("qwerty", "full=" + intValue + "hours" + min + "min");
        return intValue + " hours " + min + " min";
    }
}
