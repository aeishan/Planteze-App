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
import com.plantezeapp.Database.EcoTracker;
import com.plantezeapp.Database.FirebaseHelper;
import com.plantezeapp.Database.User;
import com.plantezeapp.R;

import java.util.HashMap;
import java.util.Map;

public class Track extends AppCompatActivity implements FirebaseHelper.UserFetchListener {

    private String date;
    private boolean receivedDate;
    private User user;
    private FirebaseUser userFire;
    private FirebaseHelper help;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_track);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button transportation;
        Button food;
        Button shopping;
        Button back;

        receivedDate = false;
        Intent intent = getIntent();
        date = intent.getStringExtra("sentDate");

        help = new FirebaseHelper();
        userFire = FirebaseAuth.getInstance().getCurrentUser();
        help.fetchUser(userFire.getUid(), this);

        if(date != null){
            receivedDate = true;
        }

        transportation=findViewById(R.id.transportation);
        transportation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Track.this, Transportation.class);
                if(receivedDate){
                    intent.putExtra("sentDate", date);
                }
                startActivity(intent);
            }
        });

        food=findViewById(R.id.food);
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Track.this, FoodConsumption.class);
                if(receivedDate){
                    intent.putExtra("sentDate", date);
                }
                startActivity(intent);
            }
        });

        shopping=findViewById(R.id.purchases);
        shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Track.this, Shopping.class);
                if(receivedDate){
                    intent.putExtra("sentDate", date);
                }
                startActivity(intent);
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Track.this, Tracker.class);
                if(receivedDate){
                    intent.putExtra("sentDate", date);
                }
                startActivity(intent);
            }
        });
    }

    public void onUserFetched(User user) {
        Log.d("Calendar", "User fetched: " + user.getEmail());
        this.user = user;
        this.user.setuID(userFire.getUid());

        // You can update the UI or perform other operations with the fetched user
        if(user.getEcoTracker() == null){
            return;
        }
        updateTotal();
    }

    @Override
    public void onFetchFailed(String errorMessage) {
        Log.d("Calendar", "Error: User not Fetched" );
    }

    public void updateTotal(){
        Double emission = 0.0;
        Map<String, Map<String, Object>> totalActivities;
        EcoTracker tracker;
        Map<String, Map<String, Map<String, Object>>> activityByDate;

        if(user.getEcoTracker() == null){
            tracker = new EcoTracker();
        }
        else{
            tracker = user.getEcoTracker();
        }

        if(tracker.getActivityByDate() == null){
            activityByDate = new HashMap<String, Map<String, Map<String, Object>>>();
        }
        else{
            activityByDate = tracker.getActivityByDate();
        }

        if(activityByDate.get(date) == null){
            totalActivities = new HashMap<String, Map<String, Object>>();
        }
        else{
            totalActivities = activityByDate.get(date);
        }

        for(String category: totalActivities.keySet()){
            //Log.d("Test Total Update", category);
            Double categoryEmission = 0.0;
            for(String activityId : totalActivities.get(category).keySet()){
                //Log.d("Test Total Update", activityId);
                if(totalActivities.get(category).get(activityId) instanceof HashMap){
                    Map<String, Double> activity =  (Map<String, Double>) totalActivities.get(category).get(activityId);
                    for(String specific : activity.keySet()){
                        //Log.d("Test Total Update", specific);

                        Double factor = 0.0;
                        if(activity.get(specific) instanceof Number){
                            //Log.d("Test Total Update", "Reached");
                            factor = ((Number) activity.get(specific)).doubleValue();
                        }

                        //Log.d("Test Total Update", factor.toString());
                        if(activityId.equals("Electronics") || activityId.equals("Appliances") || activityId.equals("Furniture") || activityId.equals("Public Transportation")){
                            emission += Information.EMISSION_FACTOR.get(activityId) * factor;
                            categoryEmission += Information.EMISSION_FACTOR.get(activityId) * factor;
                        }
                        else{
                            emission += Information.EMISSION_FACTOR.get(specific) * factor;
                            categoryEmission += Information.EMISSION_FACTOR.get(specific) * factor;
                        }
                        //Log.d("Test Total Update", emission.toString());
                    }
                }
            }

            Map<String, Map<String, Double>> emissionPerDayAndCat = tracker.getEmissionByDateAndCat();
            Map<String, Double> emissionPerCat;

            if(emissionPerDayAndCat.get(date) != null){
                //Log.d("CAT EMISSION","Receiving");
                emissionPerCat = emissionPerDayAndCat.get(date);
                //Log.d("CAT EMISSION","Received");
            }
            else{
                //Log.d("CAT EMISSION","Null");
                emissionPerCat = new HashMap<String, Double>();
            }

            emissionPerCat.put(category, categoryEmission);
            emissionPerDayAndCat.put(date, emissionPerCat);
            tracker.setEmissionByDateAndCat(emissionPerDayAndCat);
            //Log.d("Test Update Total", "Before saving category emission");
            help.saveUser(user);
            //Log.d("Test Update Total", "After saving category emission: " + category + emissionPerCat.get(category));
        }

        Map<String, Double> emissionPerDay = tracker.getTotalEmissionPerDay();;
        //Log.d("Test Update Total", "Before saving to emissionPerDay");
        emissionPerDay.put(date, emission);
        //Log.d("Test Update Total", "Before saveUser");
        user.ecoTracker = tracker;
        user.setuID(userFire.getUid());
        help.saveUser(user);
    }
}