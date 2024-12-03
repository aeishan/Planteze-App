package com.plantezeapp.Calendar;

import android.content.Intent;
import android.icu.util.ULocale;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import com.plantezeapp.Track.Information;
import com.plantezeapp.Track.Track;
import com.plantezeapp.Track.Tracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Map;

public class CalendarTrack extends AppCompatActivity implements FirebaseHelper.UserFetchListener {

    private User user;
    private FirebaseUser userFire;
    private FirebaseHelper help;
    private String formattedDate;
    CalendarView calendarView;
    Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar_track);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button back;
        Button edit;

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CalendarTrack.this, Tracker.class);
                startActivity(intent);
            }
        });

        edit = findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CalendarTrack.this, Track.class);
                intent.putExtra("sentDate", formattedDate);
                startActivity(intent);
            }
        });

        help = new FirebaseHelper();
        ULocale locale = ULocale.forLanguageTag("en-CA@calendar=gregorian");
        calendarView = findViewById(R.id.calendarView);
        calendar = Calendar.getInstance();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Date date = new Date(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
                formattedDate = formatter.format(date);
                Toast.makeText(CalendarTrack.this, formattedDate, Toast.LENGTH_SHORT).show();

                getData();
            }
        });
    }

    private void getData(){
        userFire = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("Calendar", userFire.getUid());
        help.fetchUser(userFire.getUid(), this);
    }

    @Override
    public void onUserFetched(User user) {
        Log.d("Calendar", "User fetched: " + user.getEmail());
        this.user = user;
        this.user.setuID(userFire.getUid());

        // You can update the UI or perform other operations with the fetched user
        updateEmission();
    }

    @Override
    public void onFetchFailed(String errorMessage) {
        Log.d("Calendar", "Error: User not Fetched" );
    }

    private void updateEmission(){
        updateTotal();
        TextView total = findViewById(R.id.emission);
        String rounded = String.format("%.2f", user.getEcoTracker().getEmissionForDay(formattedDate));
        total.setText("Total Emission: " + rounded + "kg of CO2");
    }

    public void updateTotal(){
        Double emission = 0.0;
        Map<String, Map<String, Object>> totalActivities;
        EcoTracker tracker = user.getEcoTracker();
        if(user.getEcoTracker().getActivityByDate().get(formattedDate) == null){
            totalActivities = new HashMap<String, Map<String, Object>>();
        }
        else{
            totalActivities = user.getEcoTracker().getActivityByDate().get(formattedDate);
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

            if(emissionPerDayAndCat.get(formattedDate) != null){
                //Log.d("CAT EMISSION","Receiving");
                emissionPerCat = emissionPerDayAndCat.get(formattedDate);
                //Log.d("CAT EMISSION","Received");
            }
            else{
                //Log.d("CAT EMISSION","Null");
                emissionPerCat = new HashMap<String, Double>();
            }

            emissionPerCat.put(category, categoryEmission);
            emissionPerDayAndCat.put(formattedDate, emissionPerCat);
            tracker.setEmissionByDateAndCat(emissionPerDayAndCat);
            //Log.d("Test Update Total", "Before saving category emission");
            help.saveUser(user);
            //Log.d("Test Update Total", "After saving category emission: " + category + emissionPerCat.get(category));
        }

        Map<String, Double> emissionPerDay = user.getEcoTracker().getTotalEmissionPerDay();;
        //Log.d("Test Update Total", "Before saving to emissionPerDay");
        emissionPerDay.put(formattedDate, emission);
        //Log.d("Test Update Total", "Before saveUser");
        help.saveUser(user);
    }
}
