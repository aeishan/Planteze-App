package com.plantezeapp.Track;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.plantezeapp.R;

import java.util.ArrayList;

public class FoodConsumption extends AppCompatActivity {
    private String item;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_consumption);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button back;
        Button submit;

        Spinner foodChoice = findViewById(R.id.foodType);
        foodChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!(parent.getItemAtPosition(position).equals("Choose Option"))) {
                    item = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> foods = new ArrayList<String>();
        foods.add("Choose Option");
        foods.add("Beef");
        foods.add("Pork");
        foods.add("Chicken");
        foods.add("Fish");
        foods.add("Plant-Based");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, foods);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        foodChoice.setAdapter(adapter);



        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FoodConsumption.this, Track.class);
                startActivity(intent);
            }
        });

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input = (EditText) findViewById(R.id.servings);
                String servings = input.getText().toString();

                if(item == null){
                    Toast.makeText(FoodConsumption.this, "Select a valid option", Toast.LENGTH_SHORT).show();
                }
                else if(servings.isEmpty() || Double.parseDouble(servings) <= 0){
                    Toast.makeText(FoodConsumption.this, "Enter a valid value", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(FoodConsumption.this, servings + " servings of " + item, Toast.LENGTH_SHORT).show();
                    Double emission = Constants.EMISSION_FACTOR.get(item) * Double.parseDouble(servings);
                    Toast.makeText(FoodConsumption.this, emission + " kg of CO2 emitted", Toast.LENGTH_SHORT).show();
                 }

            }
        });
    }
}