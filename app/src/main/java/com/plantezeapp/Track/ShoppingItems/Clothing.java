package com.plantezeapp.Track.ShoppingItems;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.plantezeapp.Track.Information;
import com.plantezeapp.Track.Shopping;
import com.plantezeapp.Track.Track;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Clothing extends AppCompatActivity implements FirebaseHelper.UserFetchListener {

    private User user;
    private EditText input;
    private Button submit;
    private String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clothing);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button back;

        FirebaseUser userFire = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseHelper help = new FirebaseHelper();
        help.fetchUser(userFire.getUid(), this);

        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
        date = formatter.format(today);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Clothing.this, Shopping.class);
                startActivity(intent);
            }
        });

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input = (EditText) findViewById(R.id.num);
                String numClothes = input.getText().toString();
                user.setuID(userFire.getUid());

                if(numClothes.isEmpty() || Integer.parseInt(numClothes) <=0){
                    Toast.makeText(Clothing.this, "Enter a valid value", Toast.LENGTH_SHORT).show();
                }
                else{
                    Double emission = Information.EMISSION_FACTOR.get("Clothes") * Double.parseDouble(numClothes);

                    if (user.getEcoTracker() == null){
                        user.ecoTracker = new EcoTracker();
                        Log.d("TEST SUBMIT", "Create EcoTracker");
                    }

                    EcoTracker tracker = user.getEcoTracker();
                    Map<String, Map<String, Map<String, Object>>> activities = tracker.getActivityByDate();
                    HashMap<String, Double> activity;

                    if(activities.get(date) == null || activities.get(date).get("Other") == null || activities.get(date).get("Other").get("Clothing") == null){
                        Log.d("TEST SUBMIT", "Null Hashmap");
                        activity = new HashMap<String, Double>();
                    }
                    else{
                        Log.d("TEST SUBMIT", "Retrieving Hashmap");
                        activity = (HashMap<String, Double>) activities.get(date).get("Other").get("Clothing");
                    }

                    activity.put("Clothes", Double.parseDouble(numClothes));
                    tracker.addActivity(date, "Other", "Clothing", activity);
                    Log.d("TEST SUBMIT", user.getEmail());

                    help.saveUser(user);

                    Toast.makeText(Clothing.this, emission + " kg of CO2 emitted", Toast.LENGTH_SHORT).show();
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