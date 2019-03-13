package com.put.miasi.utils;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum NotificationType {
        RATED_AS_PASSENGER,
        RATED_AS_DRIVER,
        NEW_PASSENGER,
        PASSENGER_RESIGNED,
        RIDE_DECLINED,
        RIDE_CANCELED
    }

    private String notificationUid;
    private NotificationType notificationType;
    private String senderUid;
    private float rate;
    private int seatsBooked;
    private String rideUid;
    private long timeStamp;

    public Notification() {

    }

    public Notification(NotificationType notificationType, String senderUid, String rideUid, float rate) {
        this.notificationType = notificationType;
        this.senderUid = senderUid;
        this.rideUid = rideUid;
        this.rate = rate;
        this.timeStamp = DateUtils.getTimeStamp();
    }

    public Notification(NotificationType notificationType, String senderUid, String rideUid, int seatsBooked) {
        this.notificationType = notificationType;
        this.senderUid = senderUid;
        this.rideUid = rideUid;
        this.seatsBooked = seatsBooked;
        this.timeStamp = DateUtils.getTimeStamp();
    }

    public Notification(NotificationType notificationType, String senderUid, String rideUid) {
        this.notificationType = notificationType;
        this.senderUid = senderUid;
        this.rideUid = rideUid;
        this.timeStamp = DateUtils.getTimeStamp();
    }

    @Exclude
    public String getNotificationUid() {
        return notificationUid;
    }

    @Exclude
    public void setNotificationUid(String notificationUid) {
        this.notificationUid = notificationUid;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getRideUid() {
        return rideUid;
    }

    public void setRideUid(String rideUid) {
        this.rideUid = rideUid;
    }

    public int getSeatsBooked() {
        return seatsBooked;
    }

    public void setSeatsBooked(int seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationUid='" + notificationUid + '\'' +
                ", notificationType=" + notificationType +
                ", senderUid='" + senderUid + '\'' +
                ", rate=" + rate +
                ", seatsBooked=" + seatsBooked +
                ", rideUid='" + rideUid + '\'' +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
