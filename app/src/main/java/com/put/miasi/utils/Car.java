package com.put.miasi.utils;

import java.io.Serializable;

public class Car implements Serializable {
    private static final long serialVersionUID = 1L;

    private String brand;
    private String model;
    private String color;

    public Car() {

    }

    public Car(String brand, String model, String color) {
        this.brand = brand;
        this.model = model;
        this.color = color;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Car{" +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", color='" + color + '\'' +
                '}';
    }

    // public Car(Parcel in) {
    //     this.brand = in.readString();
    //     this.model = in.readString();
    //     this.color = in.readString();
    // }
    //
    // @Override
    // public int describeContents() {
    //     return 0;
    // }
    //
    // @Override
    // public void writeToParcel(Parcel dest, int flags) {
    //     dest.writeString(this.brand);
    //     dest.writeString(this.model);
    //     dest.writeString(this.color);
    // }
    //
    // public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    //     public Car createFromParcel(Parcel in) {
    //         return new Car(in);
    //     }
    //
    //     public Car[] newArray(int size) {
    //         return new Car[size];
    //     }
    // };
}
