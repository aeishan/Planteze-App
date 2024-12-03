package com.plantezeapp.Track;

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
import com.plantezeapp.Calendar.CalendarTrack;
import com.plantezeapp.Database.EcoTracker;
import com.plantezeapp.Database.FirebaseHelper;
import com.plantezeapp.Database.User;
import com.plantezeapp.R;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class Gauge extends AppCompatActivity implements FirebaseHelper.UserFetchListener {

    Button calc;
    EditText input;
    private EcoTracker ecoT;
    double foodE;
    double transE;
    double otherE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gauge);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d("CHECK HERE", "YEAUEA");
        calc = findViewById(R.id.calculateTotal);
        input = findViewById(R.id.userInput);



        FirebaseUser userFire = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseHelper help = new FirebaseHelper();
        help.fetchUser(userFire.getUid(), this);

    }

    @Override
    public void onUserFetched(User user) {
        Log.d("MainActivity", "User fetched: " + user.getEmail());

        calc = findViewById(R.id.calculateTotal);
        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().isEmpty()){
                    Toast.makeText(Gauge.this, "Please input a date in the correct format.", Toast.LENGTH_SHORT).show();
                }
                else{
                    String regex = "\\d{4}-\\d{2}-\\d{2}";
                    if (Pattern.matches(regex, input.getText())){
                        try {
                            LocalDate inputDate = LocalDate.parse(input.getText());

                            if (inputDate.isAfter(LocalDate.now())) {
                                Toast.makeText(Gauge.this, "Please input a date in the correct format.", Toast.LENGTH_SHORT).show();
                            } else {
                                ecoT = user.getEcoTracker();  // is eco tracker null for a new user??
                                if (ecoT == null){
                                    Toast.makeText(Gauge.this, "No data found in Eco Tracker.", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(Gauge.this, Tracker.class);
                                    startActivity(intent);
                                }
                                else{
                                    Log.d("THIS ONE", "" + ecoT);

                                    calculateEmission(input.getText().toString());
                                }
                            }
                        }
                        catch (Exception e){
                            Toast.makeText(Gauge.this, "Please input a date in the correct format.", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                        Toast.makeText(Gauge.this, "Please input a date in the correct format.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    public void onFetchFailed(String errorMessage) {
        Log.d("MainActivity", "Error: User not Fetched" );
    }

    void calculateEmission(String date){
        LocalDate currentDate = LocalDate.now();
        LocalDate userDate = LocalDate.parse(date);

        Toast.makeText(Gauge.this, "Calculating Value", Toast.LENGTH_SHORT).show();


        LocalDate temp = userDate; // create a running variable

        while (!temp.isAfter(currentDate)){
            try{
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
                String rightDate = temp.format(formatter);
                Map<String, Double> use = ecoT.getEmissionByDateAndCat().get(rightDate);

                if (use != null) {
                    for (String option : use.keySet()) {
                        if (option.equals("Food")) {
                            foodE = foodE + use.get(option);

                        } else if (option.equals("Other")) {
                            otherE = otherE + use.get(option);

                        } else if (option.equals("Transportation")) {
                            transE = transE + use.get(option);

                        }
                    }
                }




            }
            catch (Exception e){
                Log.d("EXCEPTION", "CAT or DATE not in database");
            }
            temp = temp.plusDays(1);
        }

        Log.d("THIS ONE NOW", "food: " + foodE + ", trans: " + transE + ", other: " + otherE);
        Toast.makeText(Gauge.this, "DONE!!", Toast.LENGTH_SHORT).show();




    }
}