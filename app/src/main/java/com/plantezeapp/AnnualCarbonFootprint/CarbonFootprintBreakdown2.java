package com.plantezeapp.AnnualCarbonFootprint;

import android.content.Intent;
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.text.DecimalFormat;

import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.plantezeapp.MainActivity;
import com.plantezeapp.R;

public class CarbonFootprintBreakdown2 extends AppCompatActivity {
    static float total = 0;
    double transE = 0;
    double foodE= 0;
    double housingE = 0;
    double consumptionE = 0;
    double countryVal = IntroPage.val;

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
        Button moveToAvg = findViewById(R.id.toComparisonPage);

        moveToAvg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CarbonFootprintBreakdown2.this, Comparison.class);
                startActivity(intent);
            }
        });

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
        double[] values = {QuestionPage.transportationEmission, QuestionPage.foodEmission,
                QuestionPage.housingEmission, QuestionPage.consumptionEmission};
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

        textView.setText("Your total annual carbon emission is: " + total + " kg/year.\nHere is your breakdown:");

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


                    Toast.makeText(getApplicationContext(), label + ": " + realValue + " kg/year\nApprox  " + roundedPercentage + "% of total emissions",
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
}