package com.plantezeapp.AnnualCarbonFootprint;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.text.DecimalFormat;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.plantezeapp.MainActivity;
import com.plantezeapp.R;

public class Comparison extends AppCompatActivity {
    private double percentage = 0;
    private double globalPercentage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comparison);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button moveToAvg = findViewById(R.id.moveToTracker);

        moveToAvg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Comparison.this, MainActivity.class);
                startActivity(intent);
            }
        });


        TextView textView = findViewById(R.id.barChartTitle);
        BarChart barChart = findViewById(R.id.barChart);
        double[] values = {CarbonFootprintBreakdown2.total, IntroPage.val, 2000}; // assuming that global avgs are in tons
        String[] labels =  {"User", "Average Resident of " + IntroPage.item, "Global Target (to reduce climate change)"};
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        double difference = 0;
        double globalDifference = 0;


        for (int i = 0; i < values.length; i++){
            DecimalFormat df = new DecimalFormat("#.##");
            String newValue = df.format(values[i]);
            float roundedValue = Float.parseFloat(newValue);

            BarEntry barEntry = new BarEntry((float) (i + 1), roundedValue);

            barEntries.add(barEntry);
        }

        difference = CarbonFootprintBreakdown2.total - IntroPage.val;
        globalDifference = CarbonFootprintBreakdown2.total - 2000.0; // 2 tons (global target for reducing climate change) = 2000.0 kg
        percentage = difference / IntroPage.val;
        globalPercentage = globalDifference / 2000.0;
        percentage = percentage * 100;
        globalPercentage = globalPercentage * 100;

        if (percentage < 0){
            percentage = percentage * -1;
        }
        if (globalPercentage < 0){
            globalPercentage = globalPercentage * -1;
        }

        textView.setText("Here is how you (blue) compare to the average resident of " + IntroPage.item +
                " (red) and the global targets to reduce climate change (black):");
        Log.d("WE GOT IT", "YEAAAA");


        BarDataSet barDataSet = new BarDataSet(barEntries, "Values");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#00008B"));
        colors.add(Color.parseColor("#8B0000"));
        colors.add(Color.parseColor("#1b1b1e"));

        barDataSet.setColors(colors);
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.animateXY(1000, 1000);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {  // Entry usually contains both the value and label of the slice of the pie selected
                if (e instanceof BarEntry) {
                    BarEntry selectedPiece = (BarEntry) e;  // cast it since Entry is a generic type for all charts

                    float realValue = selectedPiece.getY();
                    float label = selectedPiece.getX();

                    DecimalFormat df2 = new DecimalFormat("#.##");
                    String percent2 = df2.format(percentage);
                    float roundedPercentage = Float.parseFloat(percent2);
                    String gPercent2 = df2.format(globalPercentage);
                    float roundedGPercentage = Float.parseFloat(gPercent2);

                    if (label == 1.0){
                        Toast.makeText(Comparison.this, "User Total Emission: " + CarbonFootprintBreakdown2.total, Toast.LENGTH_LONG).show();
                    }
                    else if (label == 2.0){
                        Toast.makeText(Comparison.this, "You are " + roundedPercentage + "% away from average emission in " + IntroPage.item + ".", Toast.LENGTH_LONG).show();
                    }
                    else if (label == 3.0){
                        Toast.makeText(Comparison.this, "You are " + roundedGPercentage + "% away from global targets.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onNothingSelected() {
                // Nothing needs goes here (but necessary for the listener)
            }
        });

        barChart.invalidate();
    }
}