package com.put.miasi.utils;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DateUtils {

    public static String STANDARD_DATE_TIME_FORMAT = "dd/MM/yyyy hh:mm";
    public static String STANDARD_DATE_FORMAT = "dd/MM/yyyy";
    public static String STANDARD_TIME_FORMAT = "hh:mm";

    public static String convertSingleDateToDouble(int s) {
        return "0" + s;
    }

    public static long getMilliSecondsFromDate(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String getWithLeadingZero(int sth) {
        return (sth < 10) ? DateUtils.convertSingleDateToDouble(sth) : String.valueOf(sth);
    }

    public static Calendar getCalendarFromMilliSecs(long ms) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        return calendar;
    }

    public static String getYearFromCalendar(Calendar cl) {
        String year = String.valueOf(cl.get(Calendar.YEAR));
        String s1 = String.valueOf(year.charAt(2));
        String s2 = String.valueOf(year.charAt(3));
        return s1 + s2;
    }

    public static String getMonthFromCalendar(Calendar cl) {
        int month = cl.get(Calendar.MONTH);
        OfferLog.d("halo?", String.valueOf(month));
        return getWithLeadingZero(month);
    }

    public static String getDayFromCalendar(Calendar cl) {
        int day = cl.get(Calendar.DAY_OF_MONTH);
        return getWithLeadingZero(day);
    }

    public static String getHourFromCalendar(Calendar cl) {
        int hour = cl.get(Calendar.HOUR_OF_DAY);
        return getWithLeadingZero(hour);
    }

    public static String getMinFromCalendar(Calendar cl) {
        int min = cl.get(Calendar.MINUTE);
        return getWithLeadingZero(min);
    }

    public static Date getDateFromMilli(long ms) {
        return new Date(ms);
    }

    public static void sortListByDate(List<RideOffer> list) {
        Collections.sort(list, new Comparator<RideOffer>() {
            public int compare(RideOffer o1, RideOffer o2) {
                if (getDateFromMilli(o1.getDate()) == null || getDateFromMilli(o2.getDate()) == null)
                    return 0;
                return getDateFromMilli(o2.getDate()).compareTo(getDateFromMilli(o1.getDate()));
            }
        });
    }

    public static String getStringDistanceFromLongMeters(long m) {
        return m / 1000 + " km";
    }

    public static String getStringDurationFromLongSeconds(long s) {
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

    public static int getDurationHoursFromLongSeconds(long s) {
        double duration = s / 3600.0;
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(duration));
        int intValue = bigDecimal.intValue();
        return intValue;
    }

    public static int getDurationMinsFromLongSeconds(long s) {
        double duration = s / 3600.0;
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(duration));
        int intValue = bigDecimal.intValue();
        double doubleValue = bigDecimal.subtract(new BigDecimal(intValue)).doubleValue();
        double decimalTime = doubleValue * 6;
        double d = decimalTime * 10;
        int min = (int) d;
        return min;
    }
}
