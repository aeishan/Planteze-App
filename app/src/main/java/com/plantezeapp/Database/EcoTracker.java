package com.plantezeapp.Database;

import java.util.HashMap;
import java.util.Map;

public class EcoTracker {
    private String userId;
    private Map<String, Map<String, Object>> activities;
    private Map<String, Double> totalEmissionPerDay;

    // Constructor
    public EcoTracker() {
        this.activities = new HashMap<>();
        this.totalEmissionPerDay = new HashMap<>();
    }

    public EcoTracker(String userId) {
        this.userId = userId;
        this.activities = new HashMap<>();
        this.totalEmissionPerDay = new HashMap<>();
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public Map<String, Map<String, Object>> getActivities() {
        return activities;
    }
    public void setActivities(Map<String, Map<String, Object>> activities) {
        this.activities = activities;
    }

    public void addActivity(String category, String activityId, Map<String, Object> activity) {
        activities.putIfAbsent(category, new HashMap<>());
        activities.get(category).put(activityId, activity);
    }

    public Map<String, Object> getActivity(String category, String activityId) {
        if (activities.containsKey(category)) {
            return (Map<String, Object>) activities.get(category).get(activityId);
        }
        return null;
    }

    public void editActivity(String category, String activityId, Map<String, Object> activity) {
        if (activities.containsKey(category) && activities.get(category).containsKey(activityId)) {
            activities.get(category).put(activityId, activity);
        }
    }
    public void removeActivity(String category, String activityId) {
        if (activities.containsKey(category)) {
            activities.get(category).remove(activityId);
        }
    }

   //Convert to Map to Firebase
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("activities", activities);
        result.put("totalEmissionPerDay", totalEmissionPerDay);
        return result;
    }
}
