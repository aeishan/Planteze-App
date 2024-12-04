package com.plantezeapp.Database;

import android.util.Log;

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

    private User user;

    public void saveUser(User test) {
        Log.d("SAVE USER","Begin");
        DatabaseReference userRef = databaseReference.child("users").child(test.getuID());
        userRef.child("email").setValue(test.getEmail());
        Log.d("SAVE USER","Main");
        if(test.getCarbonFootprint() != null){
            //userRef.child("carbonFootprint").setValue(test.getCarbonFootprint().toMap());
            this.saveCarbonFootprint(test.getuID(), test.getCarbonFootprint());
            Log.d("SAVE USER","Carbon");
        }
        if(test.getEcoTracker() != null){
            this.saveEcoTracker(test.getuID(), test.getEcoTracker());
            //userRef.child("ecoTracker").setValue(test.getEcoTracker().toMap());
            Log.d("SAVE USER","Eco");
        }
    }

    public void fetchUser(String userID, final UserFetchListener listener){
        DatabaseReference userRef = databaseReference.child("users").child(userID);
        Log.d("CHECK HERE", "YESSSS");


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("CHECK HERE", "LOOK");
                user = dataSnapshot.getValue(User.class);
                Log.d("CHECK HERE", "LOOK2");
                if (user != null) {
                    Log.d("CHECK HERE", "YEAYAEA");
                    listener.onUserFetched(user);
                } else {
                    Log.d("CHECK HERE", "NOOOOO");

                    listener.onFetchFailed("No user found with ID: " + userID);
                    //System.out.println("No user found with ID: " + userID);
                    //Log.d("T","Null");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("CHECK HERE", "nahhhhh");
                System.out.println("User not found base " + databaseError.getCode());
            }
        });
    }

    public void saveCarbonFootprint(String userId, CarbonFootprint carbonFootprint) {
        DatabaseReference carbonRef = databaseReference.child("users").child(userId).child("carbonFootprint");
        carbonRef.setValue(carbonFootprint.toMap());
    }

    public void saveEcoTracker(String userId, EcoTracker ecoTracker) {
        DatabaseReference ecoRef = databaseReference.child("users").child(userId).child("ecoTracker");
        ecoRef.setValue(ecoTracker.toMap());
    }

    public void fetchEcoTrackerActivity(String userId, String category, String activityId) {
        DatabaseReference activityRef = databaseReference.child("users").child(userId).child("ecoTracker").child(category).child(activityId);

        activityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> activity = (Map<String, Object>) dataSnapshot.getValue();
                if (activity != null) {
                    System.out.println("EcoTracker activity is fetched successfully" + activity);
                } else {
                    System.out.println("No activity data found.");
                }
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


    public interface UserFetchListener {
        void onUserFetched(User user);
        void onFetchFailed(String errorMessage);
    }

}






