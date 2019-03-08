package com.put.miasi.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {
    private String uid;
    private String avatarUrl;
    private String firstName;
    private String surname;
    private String email;
    private String phone;
    private List<String> offeredRides;

    public User() {
    }

    public User(String avatarUrl, String firstName, String surname, String email, String phone) {
        this.avatarUrl = avatarUrl;
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
    }

    public User(String avatarUrl, String firstName, String surname, String email, String phone, List<String> offeredRides) {
        this.avatarUrl = avatarUrl;
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.offeredRides = offeredRides;
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

    public List<String> getOfferedRides() {
        return offeredRides;
    }

    public void setOfferedRides(List<String> offeredRides) {
        this.offeredRides = offeredRides;
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
                "\n, offeredRides=" + offeredRides +
                '}';
    }

    public User(Parcel in) {
        this.uid = in.readString();
        this.avatarUrl = in.readString();
        this.firstName = in.readString();
        this.surname = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.offeredRides = new ArrayList<>();
        in.readList(this.offeredRides, String.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.avatarUrl);
        dest.writeString(this.firstName);
        dest.writeString(this.surname);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeList(this.offeredRides);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
