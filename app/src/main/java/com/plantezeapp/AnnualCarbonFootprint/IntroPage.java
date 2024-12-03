package com.plantezeapp.AnnualCarbonFootprint;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.plantezeapp.R;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.plantezeapp.UserLogin.RegisterAccountPage;
import com.plantezeapp.UserLogin.WelcomePage;

public class IntroPage extends AppCompatActivity {
    private Button moveToQuestion;
    static String item = "";
    static double val = 0;
    static HashMap<String, Double> countryValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        moveToQuestion=findViewById(R.id.moveToQuestionPage);
        Spinner spinner = findViewById(R.id.countryList);
        countryValues = new HashMap<>();


        List<String> firstColumnData = readFirstColumnFromCSV("Global_Averages.csv");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, firstColumnData);
        // creates adapter using firstColumnData (which is List of Strings)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        moveToQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item.equals("Country")){
                    val = countryValues.get(item) * 1000;
                    Intent intent=new Intent(IntroPage.this, QuestionPage.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(IntroPage.this, "Please select a country/region", Toast.LENGTH_SHORT).show();

                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = parent.getItemAtPosition(position).toString();
//                val = countryValues.get(item);
               //Toast.makeText(IntroPage.this, "hi " + countryValues.get(item), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private List<String> readFirstColumnFromCSV(String fileName) {
        List<String> dataList = new ArrayList<>();
        Log.d("Reader1", "Data List: " + "heyhey");

        try {
            InputStream inputStream = getAssets().open(fileName); // opens the file from the assets folder
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;

            Log.d("Readerrrr", "Data List: " + "heyhey");

            int count = 0;

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                Log.d("CSVReaderrrr", "Data List: " + columns[0]);
                if (columns.length > 0) {
                    dataList.add(columns[0].trim());

                    if (count != 0) {
                        double value = Double.parseDouble(columns[1].trim());
                        countryValues.put(columns[0].trim(), value);
                        Log.d("valuesHere", "look: " + countryValues.get(columns[0].trim()));
                    }
                    count++;
                }
            }

            reader.close();

        }
        catch (Exception e) {
            Log.d("Reader2", "Data List: " + "heyhey");
            System.out.println("You have an error: " + e);
        }

        Log.d("CSVReader", "Data List: " + dataList);
        return dataList;
    }
}
