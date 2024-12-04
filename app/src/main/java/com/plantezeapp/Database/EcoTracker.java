package com.plantezeapp.Database;

import java.util.HashMap;
import java.util.Map;

public class EcoTracker {
    private String userId;
    private Map<String, Map<String, Map<String, Object>>> activityByDate;
    private Map<String, Double> totalEmissionPerDay;

    private Map<String, Map<String, Double>> emissionByDateAndCat;

    // Constructor
    public EcoTracker() {
        this.totalEmissionPerDay = new HashMap<>();
        this.activityByDate = new HashMap<>();
        this.emissionByDateAndCat = new HashMap<>();
    }

    public EcoTracker(String userId) {
        this.userId = userId;
        this.totalEmissionPerDay = new HashMap<>();
        this.activityByDate = new HashMap<>();
        this.emissionByDateAndCat = new HashMap<>();
        this.activityByDate = new HashMap<>();
        this.emissionByDateAndCat = new HashMap<>();
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    //EmissionPerDay Based on Date
    //EmissionPerDay Based on Date
    public Map<String, Double> getTotalEmissionPerDay() {
        return totalEmissionPerDay;
    }

    public void setTotalEmissionPerDay(Map<String, Double> totalEmissionPerDay) {
        this.totalEmissionPerDay = totalEmissionPerDay;
    }
    public double getEmissionForDay(String date) {
        return totalEmissionPerDay.getOrDefault(date, 0.0);
    }

    public void addOrUpdateEmission(String date, double emission) {
        totalEmissionPerDay.put(date, emission);
    }

    //Emission Based on Date and Category
    public void setEmissionByDateAndCat(Map<String, Map<String, Double>> emissionByDateAndCat){
        this.emissionByDateAndCat = emissionByDateAndCat;
    }
    public Map<String, Map<String, Double>> getEmissionByDateAndCat() {
        return emissionByDateAndCat;
    }

    public void addOrUpdateEmissionByDateAndCat(String date, String category, double emission) {
        emissionByDateAndCat.putIfAbsent(date, new HashMap<>());
        emissionByDateAndCat.get(date).put(category, emission);
    }

    //Activity Functions
    public Map<String, Map<String, Map<String, Object>>> getActivityByDate() {
        return activityByDate;
    }

    public void setActivityByDate(Map<String, Map<String, Map<String, Object>>> activityByDate) {
        this.activityByDate = activityByDate;
    }

    public void addActivity(String date, String category, String activityId, Map<String, Double> activity) {
        activityByDate.putIfAbsent(date, new HashMap<>());
        activityByDate.get(date).putIfAbsent(category, new HashMap<>());
        activityByDate.get(date).get(category).put(activityId, activity);
    }

    public Map<String, Double> getActivity(String date, String category, String activityId) {
        if (activityByDate.containsKey(date) && activityByDate.get(date).containsKey(category) && activityByDate.get(date).get(category).containsKey(activityId)) {
            return (Map<String, Double>) activityByDate.get(date).get(category).get(activityId);
        }
        return null;
    }

    public void editActivity(String date, String activityId, Map<String, Object> updatedActivity) {
        if (activityByDate.containsKey(date) && activityByDate.get(date).containsKey(activityId)) {
            activityByDate.get(date).put(activityId, updatedActivity);
        }
    }

    public void removeActivity(String date, String activityId) {
        if (activityByDate.containsKey(date)) {
            activityByDate.get(date).remove(activityId);
            if (activityByDate.get(date).isEmpty()) {
                activityByDate.remove(date);
            }
        }
    }

    //Convert to Map to Firebase
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("activityByDate", activityByDate);
        result.put("totalEmissionPerDay", totalEmissionPerDay);
        result.put("emissionByDateAndCat", emissionByDateAndCat);
        return result;
    }
}