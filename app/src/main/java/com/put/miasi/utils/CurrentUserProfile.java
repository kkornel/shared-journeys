package com.put.miasi.utils;

import java.util.List;

public class CurrentUserProfile {
    public static String uid;
    public static String avatarUrl;
    public static String firstName;
    public static String surname;
    public static String email;
    public static String phone;
    public static List<String> offeredRides;
    private static List<String> participatedRides;
    private static double driverRating;
    private static int numberOfDriverRatings;
    private static double passengerRating;
    private static int numberOfPassengerRatings;

    public static void loadUserData(String uids, User user) {
        uid = uids;
        avatarUrl = user.getAvatarUrl();
        firstName = user.getFirstName();
        surname = user.getSurname();
        email = user.getEmail();
        phone = user.getPhone();
        offeredRides = user.getOfferedRides();
        participatedRides = user.getParticipatedRides();
        driverRating = user.getDriverRating();
        numberOfDriverRatings = user.getNumberOfDriverRatings();
        passengerRating = user.getPassengerRating();
        numberOfPassengerRatings = user.getNumberOfPassengerRatings();
    }

    public static String toStringy() {
        return "CurrentUserProfile{" +
                "\nuid='" + uid + '\'' +
                "\n, avatarUrl='" + avatarUrl + '\'' +
                "\n, firstName='" + firstName + '\'' +
                "\n, surname='" + surname + '\'' +
                "\n, email='" + email + '\'' +
                "\n, phone='" + phone + '\'' +
                "\n, offeredRides=" + offeredRides +
                "\n, participatedRides=" + participatedRides +
                "\n, driverRating=" + driverRating +
                "\n, numberOfDriverRatings=" + numberOfDriverRatings +
                "\n, passengerRating=" + passengerRating +
                "\n, numberOfPassengerRatings=" + numberOfPassengerRatings +
                '}';
    }
}
