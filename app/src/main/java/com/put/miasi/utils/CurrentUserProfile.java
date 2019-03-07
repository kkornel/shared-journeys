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

    public static void loadUserData(String uids, User user) {
        uid = uids;
        avatarUrl = user.getAvatarUrl();
        firstName = user.getFirstName();
        surname = user.getSurname();
        email = user.getEmail();
        phone = user.getPhone();
        offeredRides = user.getOfferedRides();
    }

    public static String toStringy() {
        return "CurrentUserProfile{" +
                "\nuid='" + uid + '\'' +
                "\n avatarUrl='" + avatarUrl + '\'' +
                "\n firstName='" + firstName + '\'' +
                "\n surname='" + surname + '\'' +
                "\n email='" + email + '\'' +
                "\n phone='" + phone + '\'' +
                "\n offeredRides=" + offeredRides +
                '}';
    }
}
