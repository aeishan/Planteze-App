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
            this.saveCarbonFootprint(user.getuID(), user.getCarbonFootprint());
            Log.d("SAVE USER","Carbon");
        }
        if(test.getEcoTracker() != null){
            this.saveEcoTracker(user.getuID(), user.getEcoTracker());
            //userRef.child("ecoTracker").setValue(test.getEcoTracker().toMap());
            Log.d("SAVE USER","Eco");
        }

    }

    public void fetchUser(String userID, final UserFetchListener listener){
        DatabaseReference userRef = databaseReference.child("users").child(userID);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    System.out.println("User fetched successfully: " + user);
                    listener.onUserFetched(user);
                    /*Log.d("Tester","Not Null");
                    if(user.getEmail() == null){
                        Log.d("Tester", "Null ID");
                    }
                    else{
                        Log.d("Tester",user.getEmail());
                    }*/


                } else {
                    listener.onFetchFailed("No user found with ID: " + userID);
                    //System.out.println("No user found with ID: " + userID);
                    //Log.d("T","Null");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
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

    public void checkOnboardingStatus(String userId, final OnboardingStatusListener listener) {
        DatabaseReference userRef = databaseReference.child("users").child(userId).child("onboarding");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isCompleted = dataSnapshot.getValue(Boolean.class);
                listener.onStatusChecked(isCompleted != null && isCompleted);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error checking onboarding status: " + databaseError.getMessage());
                listener.onStatusChecked(false);
            }
        });
    }


    public void checkCompletionStatus(String userId, final CompletionStatusListener listener){
        DatabaseReference userRef = databaseReference.child("users").child(userId).child("onboarding");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean ecoTrackerCompleted = dataSnapshot.child("ecoTrackerCompleted").getValue(Boolean.class) != null &&
                        dataSnapshot.child("ecoTrackerCompleted").getValue(Boolean.class);
                Boolean carbonFootprintCompleted = dataSnapshot.child("carbonFootprintCompleted").getValue(Boolean.class) != null &&
                        dataSnapshot.child("carbonFootprintCompleted").getValue(Boolean.class);

                listener.onCompletionStatusChecked(ecoTrackerCompleted && carbonFootprintCompleted);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error checking completion statuses: " + databaseError.getMessage());
                listener.onCompletionStatusChecked(false);
            }

        });
    }

    public void updateOnboardingStatus(String userId) {
        DatabaseReference userRef = databaseReference.child("users").child(userId);
        userRef.child("ecoTrackerCompleted").setValue(true);
        userRef.child("carbonFootprintCompleted").setValue(true);

       // boolean onboardingCompleted = ecoTrackerCompleted && carbonFootprintCompleted;
       // userRef.child("onboardingCompleted").setValue(onboardingCompleted);


    }

    public interface UserFetchListener {
        void onUserFetched(User user);
        void onFetchFailed(String errorMessage);

    }

    public interface OnboardingStatusListener {
        void onStatusChecked(boolean isCompleted);
    }

    public interface CompletionStatusListener{
        void onCompletionStatusChecked(boolean isCompleted);
    }
}


