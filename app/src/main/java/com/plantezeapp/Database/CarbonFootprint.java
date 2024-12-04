package com.plantezeapp.Database;

import java.util.HashMap;
import java.util.Map;

public class CarbonFootprint {
    private String userId;
    private Map<String, String> answers;

    public CarbonFootprint() {

        this.answers = new HashMap<>();
    }

    public CarbonFootprint(String userId) {
        this.userId = userId;
        this.answers = new HashMap<>();
    }

    public String getUserId() {

        return userId;
    }

    public void setUserId(String userId) {

        this.userId = userId;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }

    public Map<String, String> getAnswers() {
        return this.answers;
    }

    public void setAnswer(String questionId, String answer) {
        answers.put(questionId, answer);
    }

    public String getAnswer(String questionId) {
        return answers.get(questionId);
    }
    // Convert to Map for Firebase
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("answers", answers);
        return result;
    }
}
