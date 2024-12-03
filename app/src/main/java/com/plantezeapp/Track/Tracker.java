package com.plantezeapp.Track;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.plantezeapp.Calendar.CalendarTrack;
import com.plantezeapp.Calendar.ViewDetails;
import com.plantezeapp.Database.EcoTracker;
import com.plantezeapp.Database.FirebaseHelper;
import com.plantezeapp.Database.User;
import com.plantezeapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Tracker extends AppCompatActivity implements FirebaseHelper.UserFetchListener {

    private FirebaseHelper help;
    private User user;
    private FirebaseUser userFire;
    private Button test;
    private String date;
    private EcoTracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tracker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button track;
        Button calendar;
        Button details;

        track = findViewById(R.id.track);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Tracker.this, Track.class);
                startActivity(intent);
            }
        });

        calendar = findViewById(R.id.calendar);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Tracker.this, CalendarTrack.class);
                startActivity(intent);
            }
        });

        details = findViewById(R.id.details);
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Tracker.this, ViewDetails.class);
                intent.putExtra("sentDate", date);
                startActivity(intent);
            }
        });


        userFire = FirebaseAuth.getInstance().getCurrentUser();
        help = new FirebaseHelper();
        help.fetchUser(userFire.getUid(), this);

        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
        date = formatter.format(today);
    }

    @Override
    public void onUserFetched(User user) {
        // Do something with the fetched user
        Log.d("MainActivity", "User fetched: " + user.getEmail());
        this.user = user;
        user.setuID(userFire.getUid());

        // You can update the UI or perform other operations with the fetched user
        updateCO2Emission();
    }

    @Override
    public void onFetchFailed(String errorMessage) {
        Log.d("MainActivity", "Error: User not Fetched" );
        user = new User(userFire.getUid(), userFire.getEmail());
        help.saveUser(user);
    }

    public void updateCO2Emission(){
        updateTotal();
        TextView total = findViewById(R.id.dailyEmission);
        String rounded = String.format("%.2f", tracker.getEmissionForDay(date));
        total.setText("Total Emission: " + rounded + "kg of CO2");
    }

    public void updateTotal(){
        Double emission = 0.0;
        Map<String, Map<String, Object>> totalActivities;
        if(user.getEcoTracker() == null){
            tracker = new EcoTracker();
        }
        else{
            tracker = user.getEcoTracker();
        }

        if(tracker == null  || tracker.getActivityByDate() == null || tracker.getActivityByDate().get(date) == null){
            totalActivities = new HashMap<String, Map<String, Object>>();
        }
        else{
            totalActivities = tracker.getActivityByDate().get(date);
        }

        for(String category: totalActivities.keySet()){
            Log.d("Test Total Update", category);
            Double categoryEmission = 0.0;
            for(String activityId : totalActivities.get(category).keySet()){
                Log.d("Test Total Update", activityId);
                if(totalActivities.get(category).get(activityId) instanceof HashMap){
                    Map<String, Double> activity =  (Map<String, Double>) totalActivities.get(category).get(activityId);
                    for(String specific : activity.keySet()){
                        Log.d("Test Total Update", specific);

                        Double factor = 0.0;
                        if(activity.get(specific) instanceof Number){
                            Log.d("Test Total Update", "Reached");
                            factor = ((Number) activity.get(specific)).doubleValue();
                        }

                        Log.d("Test Total Update", factor.toString());
                        if(activityId.equals("Electronics") || activityId.equals("Appliances") || activityId.equals("Furniture") || activityId.equals("Public Transportation")){
                            emission += Information.EMISSION_FACTOR.get(activityId) * factor;
                            categoryEmission += Information.EMISSION_FACTOR.get(activityId) * factor;
                        }
                        else{
                            emission += Information.EMISSION_FACTOR.get(specific) * factor;
                            categoryEmission += Information.EMISSION_FACTOR.get(specific) * factor;
                        }
                        Log.d("Test Total Update", emission.toString());
                    }
                }
            }

            Map<String, Map<String, Double>> emissionPerDayAndCat = tracker.getEmissionByDateAndCat();
            Map<String, Double> emissionPerCat;

            if(emissionPerDayAndCat.get(date) != null){
                Log.d("CAT EMISSION","Receiving");
                emissionPerCat = emissionPerDayAndCat.get(date);
                Log.d("CAT EMISSION","Received");
            }
            else{
                Log.d("CAT EMISSION","Null");
                emissionPerCat = new HashMap<String, Double>();
            }

            emissionPerCat.put(category, categoryEmission);
            emissionPerDayAndCat.put(date, emissionPerCat);
            tracker.setEmissionByDateAndCat(emissionPerDayAndCat);
            Log.d("Test Update Total", "Before saving category emission");
            help.saveUser(user);
            Log.d("Test Update Total", "After saving category emission: " + category + emissionPerCat.get(category));
        }

        Map<String, Double> emissionPerDay;

        if(tracker.getTotalEmissionPerDay() == null){
            emissionPerDay = new HashMap<String, Double>();;
        }
        else{
            emissionPerDay = tracker.getTotalEmissionPerDay();;
        }

        Log.d("Test Update Total", "Before saving to emissionPerDay");
        emissionPerDay.put(date, emission);
        Log.d("Test Update Total", "Before saveUser");
        help.saveUser(user);
    }
}