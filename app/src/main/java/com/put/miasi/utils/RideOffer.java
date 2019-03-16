package com.put.miasi.utils;


import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;

public class RideOffer implements Serializable {
    private static final long serialVersionUID = 1L;

    public String key;
    public String driverUid;
    public long date;
    public LatLon startPoint;
    public LatLon destinationPoint;
    public long distance;
    public long duration;
    public Car car;
    public int seats;
    public String luggage;
    public int price;
    public String message;

    // String - passenger UID, Integer - number of seats booked
    public HashMap<String, Integer> passengers;

    public RideOffer() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    @Exclude
    public String getKey() {
        return key;
    }
    @Exclude
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

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public HashMap<String, Integer> getPassengers() {
        return passengers;
    }

    public void setPassengers(HashMap<String, Integer> passengers) {
        this.passengers = passengers;
    }

    @Override
    public String toString() {
        return "RideOffer{\n" +
                "key='" + key + '\'' +
                ",\n driverUid='" + driverUid + '\'' +
                ",\n date=" + date +
                ",\n startPoint=" + startPoint +
                ",\n destinationPoint=" + destinationPoint +
                ",\n distance=" + distance +
                ",\n duration=" + duration +
                ",\n car='" + car + '\'' +
                ",\n seats=" + seats +
                ",\n luggage='" + luggage + '\'' +
                ",\n price='" + price + '\'' +
                ",\n message='" + message + '\'' +
                ",\n passengers=" + passengers +
                '}';
    }

    // Parcelling part

    // public RideOffer(Parcel in) {
    //     this.key = in.readString();
    //     this.driverUid = in.readString();
    //     this.date = in.readLong();
    //     this.startPoint = in.readParcelable(LatLon.class.getClassLoader());
    //     this.destinationPoint = in.readParcelable(LatLon.class.getClassLoader());
    //     this.distance = in.readLong();
    //     this.duration = in.readLong();
    //     this.car = in.readParcelable(Car.class.getClassLoader());
    //     this.seats = in.readInt();
    //     this.luggage = in.readString();
    //     this.price = in.readInt();
    //     this.message = in.readString();
    //     this.passengers = new ArrayList<>();
    //     in.readList(this.passengers, String.class.getClassLoader());
    // }
    //
    // @Override
    // public int describeContents() {
    //     return 0;
    // }
    //
    // @Override
    // public void writeToParcel(Parcel dest, int flags) {
    //     dest.writeString(this.key);
    //     dest.writeString(this.driverUid);
    //     dest.writeLong(this.date);
    //     dest.writeParcelable(this.startPoint, flags);
    //     dest.writeParcelable(this.destinationPoint, flags);
    //     dest.writeLong(this.distance);
    //     dest.writeLong(this.duration);
    //     dest.writeParcelable(this.car, flags);
    //     dest.writeInt(this.seats);
    //     dest.writeString(this.luggage);
    //     dest.writeInt(this.price);
    //     dest.writeString(this.message);
    //     dest.writeList(this.passengers);
    // }
    //
    // public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    //     public RideOffer createFromParcel(Parcel in) {
    //         return new RideOffer(in);
    //     }
    //
    //     public RideOffer[] newArray(int size) {
    //         return new RideOffer[size];
    //     }
    // };
}
