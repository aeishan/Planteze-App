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
import java.text.DecimalFormat;

public class Gauge extends AppCompatActivity implements FirebaseHelper.UserFetchListener {

    Button calc;
    EditText input;
    EditText input2;
    static String start;
    static String end;
    private EcoTracker ecoT;
    static double foodE;
    static double transE;
    static double otherE;

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
        calc = findViewById(R.id.calculateTotal);
        input = findViewById(R.id.userInput);
        input2 = findViewById(R.id.userInput2);



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
                if (input.getText().toString().isEmpty() || input2.getText().toString().isEmpty()){
                    Toast.makeText(Gauge.this, "Please input both dates in the correct format.", Toast.LENGTH_SHORT).show();
                }
                else{
                    String regex = "\\d{4}-\\d{2}-\\d{2}";
                    if (Pattern.matches(regex, input.getText()) && Pattern.matches(regex, input2.getText())){
                        try {
                            LocalDate inputDate = LocalDate.parse(input.getText());
                            LocalDate inputDate2 = LocalDate.parse(input2.getText());

                            if (inputDate.isAfter(inputDate2)) {
                                Toast.makeText(Gauge.this, "End date must be after start date", Toast.LENGTH_SHORT).show();
                            } else {
                                ecoT = user.getEcoTracker();
                                if (ecoT == null){
                                    Toast.makeText(Gauge.this, "No data found in Eco Tracker.", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(Gauge.this, Tracker.class);
                                    startActivity(intent);
                                }
                                else{
                                    foodE = 0;
                                    transE = 0;
                                    otherE = 0;

                                    calculateEmission(input.getText().toString(), input2.getText().toString());
                                    start = input.getText().toString();
                                    end = input2.getText().toString();
                                    Intent intent=new Intent(Gauge.this, ecoGaugeBreakdown.class);
                                    startActivity(intent);

                                }
                            }
                        }
                        catch (Exception e){
                            Toast.makeText(Gauge.this, "Please input both dates in the correct format.", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                        Toast.makeText(Gauge.this, "Please input both dates in the correct format.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    @Override
    public void onFetchFailed(String errorMessage) {
        Log.d("MainActivity", "Error: User not Fetched" );
    }

    void calculateEmission(String date, String date2){
        LocalDate userDate = LocalDate.parse(date);
        LocalDate userDate2 = LocalDate.parse(date2);

        Toast.makeText(Gauge.this, "Calculating Value", Toast.LENGTH_SHORT).show();


        LocalDate temp = userDate; // create a running variable

        while (!temp.isAfter(userDate2)){
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

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String new_food = decimalFormat.format(foodE);
        String new_trans = decimalFormat.format(transE);
        String new_other = decimalFormat.format(otherE);

        foodE = Double.parseDouble(new_food);
        transE = Double.parseDouble(new_trans);
        otherE = Double.parseDouble(new_other);



        Toast.makeText(Gauge.this, "DONE!", Toast.LENGTH_SHORT).show();




    }
}