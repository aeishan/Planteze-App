package com.plantezeapp.AnnualCarbonFootprint;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.text.DecimalFormat;

import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.plantezeapp.Database.CarbonFootprint;
import com.plantezeapp.Database.FirebaseHelper;
import com.plantezeapp.Database.User;
import com.plantezeapp.R;

public class CarbonFootprintBreakdown extends AppCompatActivity implements FirebaseHelper.UserFetchListener{
    float total = 0;
    double transE = 0;
    double foodE= 0;
    double housingE = 0;
    double consumptionE = 0;
    double countryVal = 0;
    String country = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_carbon_footprint_breakdown);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        FirebaseHelper help = new FirebaseHelper();
        FirebaseUser userFire = FirebaseAuth.getInstance().getCurrentUser();
        help.fetchUser(userFire.getUid(), this);
    }



    @Override
    public void onUserFetched(User user) {
        Log.d("MainActivity", "User fetched: " + user.getEmail());

        CarbonFootprint cfoot = user.getCarbonFootprint();

        Log.d("TOTAL", "CHECK " + cfoot.getAnswer("transportationE"));
        transE = Double.parseDouble(cfoot.getAnswer("transportationE"));
        Log.d("TOTAL", "CHECK " + transE);
        foodE = Double.parseDouble(cfoot.getAnswer("foodE"));
        housingE = Double.parseDouble(cfoot.getAnswer("housingE"));
        consumptionE = Double.parseDouble(cfoot.getAnswer("consumptionE"));
        country = cfoot.getAnswer("country");
        countryVal = Double.parseDouble(cfoot.getAnswer("countryValue"));

        PieChart pieChart = findViewById(R.id.pieChart);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false); // ensures legend is not drawn inside of chart
        legend.setTextSize(12f);
        legend.setFormSize(12f); // increase size of square
        legend.setForm(Legend.LegendForm.SQUARE);
        pieChart.setExtraBottomOffset(20f);

        TextView textView = findViewById(R.id.pieChartTitle);
        double[] values = {transE, foodE, housingE, consumptionE};
        Log.d("TOTAL", "" + transE);
        String[] labels =  {"Transportation", "Food", "Housing", "Consumption"};
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        double difference = 0;
        double globalDifference = 0;
        double percentage = 0;
        double globalPercentage = 0;


        for (int i = 0; i < values.length; i++){
            DecimalFormat df = new DecimalFormat("#.##");
            String newValue = df.format(values[i]);
            float roundedValue = Float.parseFloat(newValue);
            total = total + roundedValue;
            PieEntry pieEntry = new PieEntry(roundedValue, labels[i]);

            pieEntries.add(pieEntry);
        }

        difference = total - countryVal;
        globalDifference = total - 2000.0; // 2 tons (global target for reducing climate change) = 2000.0 kg
        percentage = difference / countryVal;
        globalPercentage = globalDifference / 2000.0;

        textView.setText("Your total annual carbon emission is: " + total + " kg/year. You are \nHere is your breakdown:");



//        // change all of these statements
//        if (difference < 0 && globalDifference < 0){
//            percentage = percentage * -100;
//            globalPercentage = globalPercentage * -100;
//
//            textView.setText("TOtal: " + total + " country: " + country + " val: " + countryVal);
//        }
//        else if (difference > 0 && globalDifference < 0){
//            percentage = percentage * 100;
//            globalPercentage = globalPercentage * -100;
//
//            textView.setText("Your total annual carbon emission is: " + total + " kg/year. You are \nHere is your breakdown:");
//        }
//        else if (difference < 0 && globalDifference > 0){
//            percentage = percentage * -100;
//            globalPercentage = globalPercentage * 100;
//
//            textView.setText("Your total annual carbon emission is: " + total + " kg/year. You are \nHere is your breakdown:");
//        }
//        else{
//            percentage = percentage * 100;
//            globalPercentage = globalPercentage * 100;
//
//            textView.setText("TOtal: " + total + " country: " + country + " val: " + countryVal);
//        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, " ");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#009999"));
        colors.add(Color.parseColor("#1b1b1e"));
        colors.add(Color.parseColor("#00008B"));
        colors.add(Color.parseColor("#8B0000"));

        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(12f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setCenterText("Carbon Footprint Breakdown");
        pieChart.setCenterTextSize(18f);
        pieChart.animateXY(2000, 2000);
        pieChart.getDescription().setEnabled(false);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {  // Entry usually contains both the value and label of the slice of the pie selected
                if (e instanceof PieEntry) {
                    PieEntry selectedPiece = (PieEntry) e;  // cast it since Entry is a generic type for all charts

                    float realValue = selectedPiece.getValue();
                    String label = selectedPiece.getLabel();

                    float percentage = (realValue / total) * 100;
                    DecimalFormat df2 = new DecimalFormat("#.##");
                    String newValue2 = df2.format(percentage);
                    float roundedPercentage = Float.parseFloat(newValue2);


                    Toast.makeText(
                            getApplicationContext(),
                            label + ": " + realValue + " kg/year\nApprox  " + roundedPercentage + "% of total emissions",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onNothingSelected() {
                // Nothing goes here
            }
        });

        pieChart.invalidate();


    }

    @Override
    public void onFetchFailed(String errorMessage) {
        Log.d("MainActivity", "Error: User not Fetched" );

    }
}