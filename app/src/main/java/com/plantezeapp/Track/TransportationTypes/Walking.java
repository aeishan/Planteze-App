package com.plantezeapp.Track.TransportationTypes;

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
import com.plantezeapp.Track.FoodConsumption;
import com.plantezeapp.Track.Track;
import com.plantezeapp.Track.Transportation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Walking extends AppCompatActivity implements FirebaseHelper.UserFetchListener {

    private User user;
    private String item;
    private EditText input;
    private Button submit;
    private String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_walking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button back;
        Spinner activity = findViewById(R.id.activity);

        FirebaseUser userFire = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseHelper help = new FirebaseHelper();
        help.fetchUser(userFire.getUid(), this);

        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
        date = formatter.format(today);

        activity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!(parent.getItemAtPosition(position).equals("Choose Option"))){
                    item = parent.getItemAtPosition(position).toString();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> act = new ArrayList<String>();
        act.add("Choose Option");
        act.add("Walking");
        act.add("Cycling");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, act);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        activity.setAdapter(adapter);

        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Walking.this, Transportation.class);
                startActivity(intent);
            }
        });

        submit=findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input = (EditText) findViewById(R.id.distance);
                String distance = input.getText().toString();
                user.setuID(userFire.getUid());

                if(item == null){
                    Toast.makeText(Walking.this, "Select a valid option", Toast.LENGTH_SHORT).show();
                }
                else if(distance.isEmpty() || Double.parseDouble(distance) <= 0){
                    Toast.makeText(Walking.this, "Enter a valid value", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (user.getEcoTracker() == null){
                        user.ecoTracker = new EcoTracker();
                    }

                    EcoTracker tracker = user.getEcoTracker();
                    Map<String, Map<String, Map<String, Object>>> activities = tracker.getActivityByDate();
                    HashMap<String, Double> activity;

                    if(activities.get(date) == null || activities.get(date).get("Transportation") == null || activities.get(date).get("Transportation").get("Walk or Cycle") == null){
                        Log.d("TEST SUBMIT", "Null Hashmap");
                        activity = new HashMap<String, Double>();
                    }
                    else{
                        Log.d("TEST SUBMIT", "Retrieving Hashmap");
                        activity = (HashMap<String, Double>) activities.get(date).get("Transportation").get("Walk or Cycle");
                    }

                    activity.put(item, Double.parseDouble(distance));
                    tracker.addActivity(date,"Transportation", "Walk or Cycle", activity);
                    Log.d("TEST SUBMIT", user.getEmail());

                    help.saveUser(user);

                    Toast.makeText(Walking.this, distance + " kilometers of " + item, Toast.LENGTH_SHORT).show();
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