package com.put.miasi.utils;

import java.io.Serializable;

public class Passenger implements Serializable {
    private static final long serialVersionUID = 1L;

    private User user;
    private int numOfSeatsReserved;

    public Passenger(User user, int numOfSeatsReserved) {
        this.user = user;
        this.numOfSeatsReserved = numOfSeatsReserved;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNumOfSeatsReserved() {
        return numOfSeatsReserved;
    }

    public void setNumOfSeatsReserved(int numOfSeatsReserved) {
        this.numOfSeatsReserved = numOfSeatsReserved;
    }
}
