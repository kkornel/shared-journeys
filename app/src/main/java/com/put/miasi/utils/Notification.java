package com.put.miasi.utils;

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

    private NotificationType notificationType;
    private String senderUid;
    private float rate;
    private String rideUid;

    public Notification() {

    }

    public Notification(NotificationType notificationType, String senderUid, String rideUid, float rate) {
        this.notificationType = notificationType;
        this.senderUid = senderUid;
        this.rideUid = rideUid;
        this.rate = rate;
    }

    public Notification(NotificationType notificationType, String senderUid, String rideUid) {
        this.notificationType = notificationType;
        this.senderUid = senderUid;
        this.rideUid = rideUid;
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

    @Override
    public String toString() {
        return "Notification{" +
                ", type=" + notificationType +
                ", senderUid='" + senderUid + '\'' +
                ", rate=" + rate +
                ", rideUid='" + rideUid + '\'' +
                '}';
    }
}
