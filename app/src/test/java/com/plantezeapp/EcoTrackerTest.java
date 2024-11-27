package com.plantezeapp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.Before;

import com.google.firebase.database.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference.CompletionListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.*;

import com.plantezeapp.Database.EcoTracker;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(MockitoJUnitRunner.class)
public class EcoTrackerTest {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mEcoTrackerRef;

    @Before
    public void setUp() throws Exception {
        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance();
        mEcoTrackerRef = mDatabase.getReference("ecoTrackers").child("testUser");
    }

    // Test to verify if adding an activity updates the Firebase database
    @Test
    public void testAddActivityAndUpdateEmission() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        // Create EcoTracker instance and add activity
        Map<String, Object> activity = new HashMap<>();
        activity.put("name", "Car Ride");
        activity.put("emission", 15.0);
        EcoTracker ecoTracker = new EcoTracker("testUser");
        ecoTracker.addActivity("transport", "activity1", activity);

        // Set the total emission for the day
        Map<String, Double> emissions = new HashMap<>();
        emissions.put("2024-11-27", 15.0);
        ecoTracker.setTotalEmissionPerDay(emissions);

        // Upload data to Firebase
        mEcoTrackerRef.setValue(ecoTracker.toMap()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                latch.countDown();
            } else {
                latch.countDown();
            }
        });

        // Wait for the operation to complete
        latch.await();

        // Verify data in Firebase
        mEcoTrackerRef.child("totalEmissionPerDay").child("2024-11-27").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double totalEmission = dataSnapshot.getValue(Double.class);
                    assertEquals(15.0, totalEmission, 0.0);
                } else {
                    fail("Data not found in Firebase.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                fail("Error reading from Firebase: " + databaseError.getMessage());
            }
        });
    }

    // Test to verify if removing an activity updates the Firebase database
    @Test
    public void testRemoveActivity() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        // Create EcoTracker instance and add activity
        EcoTracker ecoTracker = new EcoTracker("testUser");
        Map<String, Object> activity = new HashMap<>();
        activity.put("name", "Car Ride");
        activity.put("emission", 15.0);
        ecoTracker.addActivity("transport", "activity1", activity);

        // Upload data to Firebase
        mEcoTrackerRef.setValue(ecoTracker.toMap()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                latch.countDown();
            } else {
                latch.countDown();
            }
        });

        // Wait for upload completion
        latch.await();

        // Remove activity
        ecoTracker.removeActivity("transport", "activity1");

        // Upload updated data
        mEcoTrackerRef.setValue(ecoTracker.toMap()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                latch.countDown();
            } else {
                latch.countDown();
            }
        });

        // Wait for upload completion
        latch.await();

        // Verify removal from Firebase
        mEcoTrackerRef.child("transport").child("activity1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    System.out.println("Activity removed successfully.");
                } else {
                    fail("Failed to remove activity.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                fail("Error reading from Firebase: " + databaseError.getMessage());
            }
        });
    }



}

