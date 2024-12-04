package com.plantezeapp.EcoGauge;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.plantezeapp.Database.EcoTracker;
import com.plantezeapp.Database.FirebaseHelper;
import com.plantezeapp.Database.User;
import com.plantezeapp.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineGraph extends AppCompatActivity  implements FirebaseHelper.UserFetchListener {
    private User user;
    private FirebaseUser userFire;
    private FirebaseHelper help;
    private LineChart lineChart;
    private Button fetchGraphButton;
    private FirebaseAuth mAuth;
    private FirebaseHelper firebaseHelper;
    private EcoGauge ecoGauge;
    private double averageEmissions;

    private double globalEmission;

    private double countryValue;
    private double percentageCountry;
    private double percentageGlobal;

    private String selectedTimePeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        lineChart = findViewById(R.id.lineChart);
        fetchGraphButton = findViewById(R.id.fetchGraphButton);
        mAuth = FirebaseAuth.getInstance();
        firebaseHelper = new FirebaseHelper();
        ecoGauge = new EcoGauge(firebaseHelper);

        Spinner timePeriodSpinner = findViewById(R.id.timePeriodSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_period_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timePeriodSpinner.setAdapter(adapter);

        timePeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedTimePeriod = parent.getItemAtPosition(position).toString();
                Log.d("LineGraph", "Selected time period: " + selectedTimePeriod);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTimePeriod = null;
                Log.d("LineGraph", "No time period selected.");
            }
        });

        fetchGraphButton.setEnabled(false);
        fetchGraphButton.setOnClickListener(v -> fetchGraphData());
        Log.d("LineGraph", "Fetch graph button clicked.");

        userFire = FirebaseAuth.getInstance().getCurrentUser();
        help = new FirebaseHelper();
        help.fetchUser(userFire.getUid(), this);
    }

    private void fetchGraphData() {
        lineChart.setData(null);
        lineChart.clear();
        lineChart.invalidate();

        if (selectedTimePeriod == null) {
            Toast.makeText(LineGraph.this, "Please select a time period.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userFire != null) {
            String userID = userFire.getUid();
            String endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yy"));
            String startDate = ecoGauge.getStartDateForPeriod(selectedTimePeriod);
            Log.d("EcoGauge", "Start date for " + selectedTimePeriod + ": " + startDate);

            Log.d("LineGraph", "Fetching data for period: " + selectedTimePeriod);
            Log.d("LineGraph", "Start date: " + startDate + ", End date: " + endDate);

            ecoGauge.getTotalEmissionsForDate(userID, startDate, endDate, selectedTimePeriod, totalemission -> {
                plotGraph();
            });
        }
    }

    private void plotGraph() {
        lineChart.clear();

        EcoTracker ecoTracker = user.getEcoTracker();
        if (ecoTracker == null){
            Log.d("EcoGauge", "error");
        }
        Map<String, Double> emissionByDate = ecoTracker.getTotalEmissionPerDay();
        String startDate = ecoGauge.startDate;
        String endDate = ecoGauge.endDate;

        if (startDate == null || endDate == null || emissionByDate == null) {
            Log.e("LineGraph", "Static values from EcoGauge are not initialized properly!");
            Toast.makeText(this, "Error loading graph data. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yy");
        LocalDate start = LocalDate.parse(startDate, dtf);
        LocalDate end = LocalDate.parse(endDate, dtf);

        List<Entry> entries = new ArrayList<>();
        int dayIndex = 0;

        double total = 0.0;
        double days = 0.0;
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String dateStr = date.format(dtf);
            if (emissionByDate.containsKey(dateStr)) {
                double dailyEmission = emissionByDate.get(dateStr);
                total += emissionByDate.get(dateStr);
                entries.add(new Entry(dayIndex, (float) dailyEmission));
                Log.d("LineGraph", "Added data point: (" + dayIndex + ", " + dailyEmission + ")");
            } else {
                Log.d("LineGraph", "No emissions for date: " + dateStr);
            }
            dayIndex++;
            days++;
        }
        globalEmission = 2000;
        averageEmissions = total/days;

        if(selectedTimePeriod.equals("Weekly")){
            globalEmission /= 52;
            countryValue /= 52;
        }

        if(selectedTimePeriod.equals("Monthly")){
            globalEmission /= 12;
            countryValue /= 12;
        }

        Toast.makeText(LineGraph.this, averageEmissions + "TEST", Toast.LENGTH_SHORT).show();
        Log.d("LineGraph", "Average emissions for selected period: " + averageEmissions + " kg");

        percentageCountry = Math.abs(((averageEmissions - countryValue)/countryValue)*100);
        percentageGlobal = Math.abs(((averageEmissions - globalEmission)/globalEmission)*100);
        if (averageEmissions > globalEmission){
            TextView averageEmissionComparison = findViewById(R.id.averageEmissionComparison);
            averageEmissionComparison.setText("Your average emission is: " +String.format("%.2f", percentageGlobal)+ "% higher than the global average. " );
        }else if (averageEmissions < globalEmission){
            TextView averageEmissionComparison = findViewById(R.id.averageEmissionComparison);
            averageEmissionComparison.setText("Your average emission is: "+ String.format("%.2f", percentageGlobal)+"% lower than the global average. " );

        }

        if(averageEmissions > countryValue){
            TextView countryComparison = findViewById(R.id.countryComparison);
            countryComparison.setText("Your average emission is: " +String.format("%.2f", percentageCountry)+ "% higher than the region.");
        }else if(averageEmissions < countryValue){
            TextView countryComparison = findViewById(R.id.countryComparison);
            countryComparison.setText("Your average emission is: " +String.format("%.2f", percentageCountry)+ "% lower than the region.");
        }


        TextView averageEmissionsTextView = findViewById(R.id.averageEmissionsTextView);
        averageEmissionsTextView.setText("Average Emissions: " + selectedTimePeriod + ": " + String.format("%.2f", averageEmissions) + " kg");


        if (entries.isEmpty()) {
            Toast.makeText(this, "No data to display for the selected period.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Selected Time", selectedTimePeriod);
        LineDataSet lineDataSet = new LineDataSet(entries, selectedTimePeriod + "Emissions");
        styleDataSet(lineDataSet);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        customizeChart();
        lineChart.invalidate();
        Log.d("LineGraph", "Graph plotted successfully.");
    }

    private void styleDataSet(LineDataSet dataSet) {
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.RED);
        dataSet.setCircleRadius(4f);
    }

    private void customizeChart() {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false); // Disable right Y-axis

        lineChart.setDescription(null); // Disable description
    }

    public void onUserFetched(User user) {
        Log.d("EcoGauge", "Total Emissions for the period: ");
        this.user = user;
        this.user.setuID(userFire.getUid());
        countryValue = Double.parseDouble(user.getCarbonFootprint().getAnswers().get("countryValue")) * 1000;
        fetchGraphButton.setEnabled(true);
    }
    @Override
    public void onFetchFailed(String errorMessage) {
        Log.d("EcoGauge", "Failed to fetch user: " + errorMessage);

    }
}


