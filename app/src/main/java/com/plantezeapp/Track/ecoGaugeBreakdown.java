package com.plantezeapp.Track;

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
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.plantezeapp.MainActivity;

import com.plantezeapp.R;

public class ecoGaugeBreakdown extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eco_gauge_breakdown);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        double total = Gauge.foodE + Gauge.transE + Gauge.otherE;
        Button toAvgPage = findViewById(R.id.toAvgPage);

        toAvgPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ecoGaugeBreakdown.this, ecoGaugeBreakdown.class);
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
        double[] values = {Gauge.foodE, Gauge.transE,
                Gauge.otherE};
        String[] labels =  {"Food", "Transportation", "Other"};
        ArrayList<PieEntry> pieEntries = new ArrayList<>();


        for (int i = 0; i < values.length; i++){
            float new_val = (float) (values[i]);  // since PieEntry needs float
            PieEntry pieEntry = new PieEntry(new_val, labels[i]);

            pieEntries.add(pieEntry);
        }

        textView.setText("Your total annual carbon emission from " + Gauge.start + " to "  + Gauge.end + " is " + total + " kg.\nHere is your breakdown:");
        Log.d("WE GOT IT", "YEAAAA");


        PieDataSet pieDataSet = new PieDataSet(pieEntries, " ");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#8B0000"));
        colors.add(Color.parseColor("#1b1b1e"));
        colors.add(Color.parseColor("#00008B"));

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


                    Toast.makeText(
                            getApplicationContext(),
                            label + ": " + realValue + " kg",
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