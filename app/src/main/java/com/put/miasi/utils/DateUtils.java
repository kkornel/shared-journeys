package com.put.miasi.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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


}
