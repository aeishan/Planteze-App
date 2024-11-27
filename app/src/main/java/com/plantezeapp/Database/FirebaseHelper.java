package com.plantezeapp.Database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class FirebaseHelper {

    private DatabaseReference databaseReference;

    public FirebaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void saveUser(User user) {
        DatabaseReference userRef = databaseReference.child("users").child(user.getuID());
        userRef.child("email").setValue(user.getEmail());
        userRef.child("carbonFootprint").setValue(user.getCarbonFootprint().toMap());
        userRef.child("ecoTracker").setValue(user.getEcoTracker().toMap());
    }

    public void fetchUser(String uID, final FirebaseCallback<User> callback) {
        DatabaseReference userRef = databaseReference.child("users").child(uID);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                callback.onCallback(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error: " + databaseError.getMessage());
            }
        });
    }

    public void saveCarbonFootprint(String userId, CarbonFootprint carbonFootprint) {
        DatabaseReference carbonRef = databaseReference.child("users").child(userId).child("carbonFootprint");
        carbonRef.setValue(carbonFootprint.toMap());
    }

    public void fetchCarbonFootprintAnswer(String userId, String questionId, final FirebaseCallback<String> callback) {
        DatabaseReference answerRef = databaseReference.child("users").child(userId).child("carbonFootprint").child("answers").child(questionId);
        answerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String answer = dataSnapshot.getValue(String.class);
                callback.onCallback(answer);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error: " + databaseError.getMessage());
            }
        });
    }

    public void saveEcoTracker(String userId, EcoTracker ecoTracker) {
        DatabaseReference ecoRef = databaseReference.child("users").child(userId).child("ecoTracker");
        ecoRef.setValue(ecoTracker.toMap());
    }

    public void fetchEcoTrackerActivity(String userId, String category, String activityId, final FirebaseCallback<Map<String, Object>> callback) {
        DatabaseReference activityRef = databaseReference.child("users").child(userId).child("ecoTracker").child(category).child(activityId);
        activityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> activity = (Map<String, Object>) dataSnapshot.getValue();
                callback.onCallback(activity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error: " + databaseError.getMessage());
            }
        });
    }

    public void updateDailyEmission(String userId, String date, double emission) {
        DatabaseReference dailyEmissionRef = databaseReference.child("users").child(userId).child("ecoTracker").child("totalEmissionPerDay").child(date);
        dailyEmissionRef.setValue(emission).addOnSuccessListener(aVoid -> {
            System.out.println("Emission updated for " + date);
        }).addOnFailureListener(e -> {
            System.out.println("Failed to update emission: " + e.getMessage());
        });
    }


    public void updateOnboardingStatus(String userId, boolean isDone) {
        DatabaseReference userRef = databaseReference.child("users").child(userId);
        userRef.child("onboarding").setValue(isDone);
    }

    //Callback interface
    public interface FirebaseCallback<T> {
        void onCallback(T result);
    }

}

