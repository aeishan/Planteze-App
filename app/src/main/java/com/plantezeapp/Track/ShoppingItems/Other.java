package com.plantezeapp.Track.ShoppingItems;

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
import com.plantezeapp.Track.FoodConsumption;
import com.plantezeapp.Track.Shopping;

import java.util.ArrayList;

public class Other extends AppCompatActivity {

    private String item;
    private EditText input;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_other);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button back;
        Button submit;
        Spinner choice = findViewById(R.id.choice);

        choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        ArrayList<String> options = new ArrayList<String>();
        options.add("Choose Option");
        options.add("Furniture");
        options.add("Appliances");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        choice.setAdapter(adapter);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Other.this, Shopping.class);
                startActivity(intent);
            }
        });

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input = (EditText) findViewById(R.id.units);
                String units = input.getText().toString();

                if(item == null){
                    Toast.makeText(Other.this, "Select a valid option", Toast.LENGTH_SHORT).show();
                }
                else if(units.isEmpty() || Integer.parseInt(units) <= 0){
                    Toast.makeText(Other.this, "Enter a valid value", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Other.this, units + " units of " + item, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}