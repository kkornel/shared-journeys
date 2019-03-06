package com.put.miasi.utils;

import java.util.List;

public class RideOffer {

    public String key;
    public String driverUid;
    public long date;
    public LatLon startPoint;
    public LatLon destinationPoint;
    public String car;
    public int seats;
    public String luggage;
    public String message;
    public List<String> passengers;

    public RideOffer() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDriverUid() {
        return driverUid;
    }

    public void setDriverUid(String driverUid) {
        this.driverUid = driverUid;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public LatLon getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(LatLon startPoint) {
        this.startPoint = startPoint;
    }

    public LatLon getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(LatLon destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getLuggage() {
        return luggage;
    }

    public void setLuggage(String luggage) {
        this.luggage = luggage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
    }
}
