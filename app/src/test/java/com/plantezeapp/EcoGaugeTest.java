package com.plantezeapp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

import com.plantezeapp.Database.EcoTracker;
import com.plantezeapp.Database.FirebaseHelper;
import com.plantezeapp.EcoGauge.EcoGauge;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)  // Disable manifest loading for Robolectric
public class EcoGaugeTest {

    @Mock
    private FirebaseHelper firebaseHelperMock;

    private EcoGauge ecoGauge;

    @Mock
    private EcoTracker ecoTrackerMock;


    @Before
    public void setUp() {
        // Initialize mocks
        MockitoAnnotations.initMocks(this);

        // Inject mock FirebaseHelper into EcoGauge
        ecoGauge = new EcoGauge(firebaseHelperMock);
    }

    @Test
    public void testCalculateEmissionForWeekly_withMockData() {
        // Set up mock data matching dd/M/uuuu format
        Map<String, Double> emissionsPerDay = new HashMap<>();
        emissionsPerDay.put("26/11/2024", 10.0); // Within the last 7 days
        emissionsPerDay.put("27/11/2024", 15.0);
        emissionsPerDay.put("28/11/2024", 20.0);
        emissionsPerDay.put("29/11/2024", 25.0);
        emissionsPerDay.put("30/11/2024", 30.0); // Last date included in range
        emissionsPerDay.put("01/12/2024", 35.0); // Outside weekly range from 26/11

        when(ecoTrackerMock.getTotalEmissionPerDay()).thenReturn(emissionsPerDay);

        // Call method
        double result = ecoGauge.calculateEmissionForDate(ecoTrackerMock, "30/11/2024", "weekly");

        // Verify result (sum of emissions from 26/11 to 30/11)
        assertEquals(100.0, result, 0.01);
    }
}