package com.flightreservation.patterns;

public interface Observer {

    void update(String message);

    void onPromotion(String promotion);
}
