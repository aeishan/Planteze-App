package com.plantezeapp.Track.TransportationTypes;

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
import com.plantezeapp.Track.Track;
import com.plantezeapp.Track.Transportation;

import java.util.ArrayList;

public class PublicTransportation extends AppCompatActivity {

    private String item;
    private EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_public_transportation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button back;
        Button submit;

        Spinner transportationType = findViewById(R.id.transportationType);
        transportationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!(parent.getItemAtPosition(position).equals("Choose Option"))){
                    item = parent.getItemAtPosition(position).toString();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> transportation = new ArrayList<String>();
        transportation.add("Choose Option");
        transportation.add("Bus");
        transportation.add("Train");
        transportation.add("Subway");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, transportation);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        transportationType.setAdapter(adapter);

        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PublicTransportation.this, Transportation.class);
                startActivity(intent);
            }
        });

        submit=findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input = (EditText) findViewById(R.id.time);
                String time = input.getText().toString();

                if(item == null){
                    Toast.makeText(PublicTransportation.this, "Select a valid option", Toast.LENGTH_SHORT).show();
                }
                else if(time.isEmpty() || Double.parseDouble(time) <= 0){
                    Toast.makeText(PublicTransportation.this, "Enter a valid value", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(PublicTransportation.this, time + " hour(s) spent on " + item, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}