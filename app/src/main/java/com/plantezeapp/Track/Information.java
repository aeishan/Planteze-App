package com.plantezeapp.Track;

import com.plantezeapp.Database.User;

import java.util.Map;
import java.util.HashMap;

public class Information {
    public static final Map<String, Double> EMISSION_FACTOR;
    //public static User user;

    static{
        EMISSION_FACTOR = new HashMap<>();
        EMISSION_FACTOR.put("Gasoline", 0.24);
        EMISSION_FACTOR.put("Diesel", 0.27);
        EMISSION_FACTOR.put("Hybrid", 0.16);
        EMISSION_FACTOR.put("Electric", 0.05);
        EMISSION_FACTOR.put("Public Transportation", 5.25);
        EMISSION_FACTOR.put("Short Haul(Less than 1500km)", 150.0);
        EMISSION_FACTOR.put("Long Haul(More than 1500km)", 550.0);
        EMISSION_FACTOR.put("Beef", 10.89);
        EMISSION_FACTOR.put("Pork", 4.63);
        EMISSION_FACTOR.put("Chicken", 2.69);
        EMISSION_FACTOR.put("Fish", 2.17);
        EMISSION_FACTOR.put("Plant-Based", 0.8);
        EMISSION_FACTOR.put("Clothes", 10.0);
        EMISSION_FACTOR.put("Electronics", 300.0);
        EMISSION_FACTOR.put("Gas Bill", 4.24);
        EMISSION_FACTOR.put("Water Bill", 1.56);
        EMISSION_FACTOR.put("Electricity Bill", 7.07);
        EMISSION_FACTOR.put("Furniture", 58.6);
        EMISSION_FACTOR.put("Appliances", 233.33);
        EMISSION_FACTOR.put("Walking", 0.0);
        EMISSION_FACTOR.put("Cycling", 0.0);
    }
}
