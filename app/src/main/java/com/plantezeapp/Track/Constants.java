package com.plantezeapp.Track;

import java.util.Map;
import java.util.HashMap;

public class Constants {
    public static final Map<String, Double> EMISSION_FACTOR;

    static{
        EMISSION_FACTOR = new HashMap<>();
        EMISSION_FACTOR.put("Gas Vehicle", 0.24);
        EMISSION_FACTOR.put("Diesel Vehicle", 0.27);
        EMISSION_FACTOR.put("Hybrid Vehicle", 0.16);
        EMISSION_FACTOR.put("Electric Vehicle", 0.05);
        EMISSION_FACTOR.put("Public Transportation", 5.25);
        EMISSION_FACTOR.put("Short Flight", 150.0);
        EMISSION_FACTOR.put("Long Flight", 550.0);
        EMISSION_FACTOR.put("Beef", 10.89);
        EMISSION_FACTOR.put("Pork", 4.63);
        EMISSION_FACTOR.put("Chicken", 2.69);
        EMISSION_FACTOR.put("Fish", 2.17);
        EMISSION_FACTOR.put("Plant-Based", 0.8);
        EMISSION_FACTOR.put("Clothing", 10.0);
        EMISSION_FACTOR.put("Electronics", 300.0);
        EMISSION_FACTOR.put("Gas Bill", 4.24);
        EMISSION_FACTOR.put("Water Bill", 1.56);
        EMISSION_FACTOR.put("Electric Bill", 7.07);
        EMISSION_FACTOR.put("Furniture", 58.6);
        EMISSION_FACTOR.put("Appliances", 233.33);
    }
}
