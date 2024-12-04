package com.plantezeapp.EcoGauge;

import com.plantezeapp.Database.FirebaseHelper;
import com.plantezeapp.Database.User;
import com.plantezeapp.Database.EcoTracker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;
import android.widget.Toast;

public class EcoGauge {
    private static FirebaseHelper firebaseHelper;

    static double totalEmissions;

     public String startDate;
     public String endDate;

     public double averageEmission;


    //Constructor
    public EcoGauge(FirebaseHelper firebaseHelper) {
        this.firebaseHelper = firebaseHelper;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void getTotalEmissionsForDate(String userID,  String date1, String date2, String timePeriod, final EmissionCalculated listener) {
        Log.d("EcoGauge", "Fetching emissions for user: " + userID + " from " + date1 + " to " + date2 + " for time period: " + timePeriod);
        firebaseHelper.fetchUser(userID, new FirebaseHelper.UserFetchListener() {
            @Override
            public void onUserFetched(User user) {
                EcoTracker ecoTracker = user.getEcoTracker();
                if (ecoTracker == null){
                    Log.d("EcoGauge", "error");
                }
                startDate = getStartDateForPeriod(timePeriod);  // Set startDate
                endDate = date2; // Set endDate
                Log.d("EcoGauge", "Start date: " + startDate + " | End date: " + endDate);
                totalEmissions = calculateEmissionForDate(ecoTracker, endDate, timePeriod);
                Log.d("EcoGauge", "Total Emissions for the period: " + totalEmissions);

                listener.onEmissionsCalculated(totalEmissions);

            }

            @Override
            public void onFetchFailed(String errorMessage) {
                listener.onEmissionsCalculated(0.0);
                Log.d("EcoGauge", "Failed to fetch user: " + errorMessage);

            }
        });

    }

    public double calculateEmissionForDate(EcoTracker ecoTracker, String date2, String timePeriod) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yy");
        String startDate = getStartDateForPeriod(timePeriod);
        String endDate = date2;

        Log.d("EcoGauge", "Calculating emissions from " + startDate + " to " + endDate);

        double totalEmission = 0.0;

        Map<String, Double> emissionByDate = ecoTracker.getTotalEmissionPerDay(); //Gets emission value of DateA
        Log.d("EcoGauge", "Emission data in ecoTracker: " + emissionByDate);

        // Iterate through dates
        LocalDate start = LocalDate.parse(startDate, dtf);
        LocalDate end = LocalDate.parse(endDate, dtf);
        Log.d("EcoGauge", "Start parsing dates from: " + start + " to " + end);

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String dateStr = date.format(dtf);

            if (emissionByDate.containsKey(dateStr)) {
                double dailyEmission = emissionByDate.getOrDefault(dateStr, 0.0);
                Log.d("EcoGauge", "Emissions for " + dateStr + ": " + dailyEmission);
                totalEmission += dailyEmission;
            } else{
                Log.d("EcoGauge", "No emissions for date: " + dateStr);

            }

        }
        Log.d("EcoGauge", "Total emissions calculated for the period: " + totalEmission);
        return totalEmission;
    }

     public String getStartDateForPeriod(String timePeriod) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yy");
        LocalDate currentDate = LocalDate.now();

        String startDate = ""; // Initialize here to ensure it's always set
        if (timePeriod.equalsIgnoreCase("weekly")) {
            startDate = currentDate.minusWeeks(1).format(dtf); // Previous week
        } else if (timePeriod.equalsIgnoreCase("monthly")) {
            startDate = currentDate.minusMonths(1).format(dtf); // Start of current month
        } else if (timePeriod.equalsIgnoreCase("yearly")) {
            startDate = currentDate.minusYears(1).format(dtf); // Start of current year
        } else {
            // Default: current date if the period is unknown
            startDate = currentDate.format(dtf);
        }
        Log.d("EcoGauge", "Start date for period (" + timePeriod + "): " + startDate);
        return startDate; // Return the calculated start date
    }

    public double calculateAverageEmissions(Map<String, Double> emissionByDate) {
        double totalEmissions = 0.0;
        int totalDays = 0;
        for (Map.Entry<String, Double> entry : emissionByDate.entrySet()) {
            totalEmissions += entry.getValue();
            totalDays++;
        }

        if (totalDays == 0) {
            return 0;
        }

        return totalEmissions / totalDays;
    }



    public interface EmissionCalculated {
        void onEmissionsCalculated(double totalEmissions);
    }

}
