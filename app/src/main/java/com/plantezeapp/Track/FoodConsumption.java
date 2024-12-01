package com.plantezeapp.Track;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FoodConsumption extends AppCompatActivity implements FirebaseHelper.UserFetchListener {

    private User user;
    private String item;
    private EditText input;
    private Button submit;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_consumption);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button back;
        Spinner foodChoice = findViewById(R.id.foodType);

        FirebaseUser userFire = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseHelper help = new FirebaseHelper();
        help.fetchUser(userFire.getUid(), this);

        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
        date = formatter.format(today);

        foodChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!(parent.getItemAtPosition(position).equals("Choose Option"))) {
                    item = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> foods = new ArrayList<String>();
        foods.add("Choose Option");
        foods.add("Beef");
        foods.add("Pork");
        foods.add("Chicken");
        foods.add("Fish");
        foods.add("Plant-Based");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, foods);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        foodChoice.setAdapter(adapter);



        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FoodConsumption.this, Track.class);
                startActivity(intent);
            }
        });

        submit = findViewById(R.id.submit);
        submit.setEnabled(false);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input = (EditText) findViewById(R.id.servings);
                String servings = input.getText().toString();
                user.setuID(userFire.getUid());

                if(item == null){
                    Toast.makeText(FoodConsumption.this, "Select a valid option", Toast.LENGTH_SHORT).show();
                }
                else if(servings.isEmpty() || Double.parseDouble(servings) <= 0){
                    Toast.makeText(FoodConsumption.this, "Enter a valid value", Toast.LENGTH_SHORT).show();
                }
                else{
                    Double emission = Information.EMISSION_FACTOR.get(item) * Double.parseDouble(servings);

                    if (user.getEcoTracker() == null){
                        user.ecoTracker = new EcoTracker();
                        Log.d("TEST SUBMIT", "Create EcoTracker");
                    }

                    EcoTracker tracker = user.getEcoTracker();
                    Map<String, Map<String, Map<String, Object>>> activities = tracker.getActivityByDate();
                    HashMap<String, Double> activity;

                    if(activities.get(date) == null || activities.get(date).get("Food") == null || activities.get(date).get("Food").get("Meal") == null){
                        Log.d("TEST SUBMIT", "Null Hashmap");
                        activity = new HashMap<String, Double>();
                    }
                    else{
                        Log.d("TEST SUBMIT", "Retrieving Hashmap");
                        activity = (HashMap<String, Double>) activities.get(date).get("Food").get("Meal");
                    }

                    activity.put(item, Double.parseDouble(servings));
                    tracker.addActivity(date, "Food", "Meal", activity);
                    Log.d("TEST SUBMIT", user.getEmail());

                    help.saveUser(user);

                    Toast.makeText(FoodConsumption.this, emission + " kg of CO2 emitted", Toast.LENGTH_SHORT).show();
                 }

            }
        });
    }

    @Override
    public void onUserFetched(User user) {
        Log.d("MainActivity", "User fetched: " + user.getEmail());
        this.user = user;
        submit.setEnabled(true);
    }

    @Override
    public void onFetchFailed(String errorMessage) {
        Log.d("MainActivity", "Error: User not Fetched" );
    }
}