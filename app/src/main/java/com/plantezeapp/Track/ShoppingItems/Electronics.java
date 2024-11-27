package com.plantezeapp.Track.ShoppingItems;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.plantezeapp.R;
import com.plantezeapp.Track.FoodConsumption;
import com.plantezeapp.Track.Shopping;
import com.plantezeapp.Track.Track;

import java.util.ArrayList;

public class Electronics extends AppCompatActivity {

    private String item;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_electronics);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button back;
        Button submit;
        Spinner electronicType = findViewById(R.id.electronicType);

        electronicType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        ArrayList<String> electronics = new ArrayList<String>();
        electronics.add("Choose Option");
        electronics.add("Smartphone");
        electronics.add("Tablet");
        electronics.add("Laptop");
        electronics.add("Computer");
        electronics.add("TV");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, electronics);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        electronicType.setAdapter(adapter);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Electronics.this, Shopping.class);
                startActivity(intent);
            }
        });

        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input = (EditText) findViewById(R.id.devices);
                String devices = input.getText().toString();

                if(item == null){
                    Toast.makeText(Electronics.this, "Select a valid option", Toast.LENGTH_SHORT).show();
                }
                else if(devices.isEmpty() || Integer.parseInt(devices) <= 0){
                    Toast.makeText(Electronics.this, "Enter a valid value", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Electronics.this, devices + " units of " + item, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}