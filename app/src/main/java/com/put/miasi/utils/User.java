package com.put.miasi.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// public class User implements Parcelable {
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String uid;
    private String avatarUrl;
    private String firstName;
    private String surname;
    private String email;
    private String phone;
    // private List<String> offeredRides;

    private HashMap<String, Boolean> offeredRides;      /* String - RideUid, Boolean - has passengers were rated, true if yes, and you cant rate them later */
    private HashMap<String, Boolean> participatedRides; /* String - RideUid, Boolean - has passengers were rated, true if yes, and you cant rate them later */
    private HashMap<String, Boolean> notifications;     /* String - NotificationUid, Boolean - has notification was seen? true if yes (you have to open dialog and click OK to make it seen */
    // private List<String> participatedRides;
    private float driverRating;
    private int numberOfDriverRatings;
    private float passengerRating;
    private int numberOfPassengerRatings;

    public User() {
        this.offeredRides = new HashMap<>();
        this.participatedRides = new HashMap<>();
    }

    public User(String avatarUrl, String firstName, String surname, String email, String phone) {
        this.avatarUrl = avatarUrl;
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
    }

    @Exclude
    public String getUid() {
        return uid;
    }

    @Exclude
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public HashMap<String, Boolean> getNotifications() {
        return notifications;
    }

    public void setNotifications(HashMap<String, Boolean> notifications) {
        this.notifications = notifications;
    }

    // public List<String> getOfferedRides() {
    //     return offeredRides;
    // }
    //
    // public void setOfferedRides(List<String> offeredRides) {
    //     this.offeredRides = offeredRides;
    // }
    //
    // public List<String> getParticipatedRides() {
    //     return participatedRides;
    // }
    //
    // public void setParticipatedRides(List<String> participatedRides) {
    //     this.participatedRides = participatedRides;
    // }


    public HashMap<String, Boolean> getOfferedRides() {
        return offeredRides;
    }

    public void setOfferedRides(HashMap<String, Boolean> offeredRides) {
        this.offeredRides = offeredRides;
    }

    public HashMap<String, Boolean> getParticipatedRides() {
        return participatedRides;
    }

    public void setParticipatedRides(HashMap<String, Boolean> participatedRides) {
        this.participatedRides = participatedRides;
    }

    public float getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(float driverRating) {
        this.driverRating = driverRating;
    }

    public int getNumberOfDriverRatings() {
        return numberOfDriverRatings;
    }

    public void setNumberOfDriverRatings(int numberOfDriverRatings) {
        this.numberOfDriverRatings = numberOfDriverRatings;
    }

    public float getPassengerRating() {
        return passengerRating;
    }

    public void setPassengerRating(float passengerRating) {
        this.passengerRating = passengerRating;
    }

    public int getNumberOfPassengerRatings() {
        return numberOfPassengerRatings;
    }

    public void setNumberOfPassengerRatings(int numberOfPassengerRatings) {
        this.numberOfPassengerRatings = numberOfPassengerRatings;
    }

    @Exclude
    public String getFullname() {
        return this.firstName + " " + this.surname;
    }

    public List<String> getOfferedRidesList() {
        List<String> offered = new ArrayList<>();
        for (String key : this.offeredRides.keySet()) {
            offered.add(key);
        }
        return offered;
    }

    public List<String> getParticipatedRidesList() {
        List<String> participated = new ArrayList<>();
        for (String key : this.participatedRides.keySet()) {
            participated.add(key);
        }
        return participated;
    }

    @Exclude
    public float getDriverRatingAvg() {
        float avg = driverRating / numberOfDriverRatings;
        if (Float.isNaN(avg) || Float.isInfinite(avg)){
            return 0.0f;
        }
        return avg;
    }

    @Exclude
    public float getPassengerRatingAvg() {
        float avg = passengerRating / numberOfPassengerRatings;
        if (Float.isNaN(avg) || Float.isInfinite(avg)){
            return 0.0f;
        }
        return avg;
    }

    @Override
    public String toString() {
        return "User{" +
                "\nuid='" + uid + '\'' +
                "\n, avatarUrl='" + avatarUrl + '\'' +
                "\n, firstName='" + firstName + '\'' +
                "\n, surname='" + surname + '\'' +
                "\n, email='" + email + '\'' +
                "\n, phone='" + phone + '\'' +
                "\n, notifications=" + notifications +
                "\n, offeredRides=" + offeredRides +
                "\n, participatedRides=" + participatedRides +
                "\n, driverRating=" + driverRating +
                "\n, numberOfDriverRatings=" + numberOfDriverRatings +
                "\n, passengerRating=" + passengerRating +
                "\n, numberOfPassengerRatings=" + numberOfPassengerRatings +
                '}';
    }

    // public User(Parcel in) {
    //     this.uid = in.readString();
    //     this.avatarUrl = in.readString();
    //     this.firstName = in.readString();
    //     this.surname = in.readString();
    //     this.email = in.readString();
    //     this.phone = in.readString();
    //     // this.offeredRides = new ArrayList<>();
    //     // in.readList(this.offeredRides, String.class.getClassLoader());
    //     // this.participatedRides = new ArrayList<>();
    //     // in.readList(this.participatedRides, String.class.getClassLoader());
    //     this.offeredRides = new HashMap<>();
    //     this.offeredRides = (HashMap<String, Boolean>) in.readSerializable();
    //     this.participatedRides = new HashMap<>();
    //     this.participatedRides = (HashMap<String, Boolean>) in.readSerializable();
    //     this.driverRating = in.readDouble();
    //     this.numberOfDriverRatings = in.readInt();
    //     this.passengerRating = in.readDouble();
    //     this.numberOfPassengerRatings = in.readInt();
    // }
    //
    // @Override
    // public int describeContents() {
    //     return 0;
    // }
    //
    // @Override
    // public void writeToParcel(Parcel dest, int flags) {
    //     dest.writeString(this.uid);
    //     dest.writeString(this.avatarUrl);
    //     dest.writeString(this.firstName);
    //     dest.writeString(this.surname);
    //     dest.writeString(this.email);
    //     dest.writeString(this.phone);
    //     // dest.writeList(this.offeredRides);
    //     // dest.writeList(this.participatedRides);
    //     dest.writeSerializable(this.offeredRides);
    //     dest.writeSerializable(this.participatedRides);
    //     dest.writeDouble(this.driverRating);
    //     dest.writeInt(this.numberOfDriverRatings);
    //     dest.writeDouble(this.passengerRating);
    //     dest.writeInt(this.numberOfPassengerRatings);
    // }
    //
    // public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    //     public User createFromParcel(Parcel in) {
    //         return new User(in);
    //     }
    //
    //     public User[] newArray(int size) {
    //         return new User[size];
    //     }
    // };
}
