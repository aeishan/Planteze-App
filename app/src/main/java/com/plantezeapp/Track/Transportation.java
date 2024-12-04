package com.plantezeapp.Track;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.plantezeapp.R;
import com.plantezeapp.Track.TransportationTypes.Flight;
import com.plantezeapp.Track.TransportationTypes.PersonalVehicle;
import com.plantezeapp.Track.TransportationTypes.PublicTransportation;
import com.plantezeapp.Track.TransportationTypes.Walking;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Transportation extends AppCompatActivity {

    private String date;
    private boolean receivedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transportation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button back;
        Button vehicle;
        Button transportation;
        Button walk;
        Button flight;
        Intent intent = getIntent();
        date = intent.getStringExtra("sentDate");

        if(date != null){
            Log.d("Date","Received Transportation");
            receivedDate = true;
        }

        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Transportation.this, Track.class);
                intent.putExtra("sentDate", date);
                startActivity(intent);
            }
        });

        vehicle=findViewById(R.id.vehicle);
        vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Transportation.this, PersonalVehicle.class);
                if(receivedDate){
                    intent.putExtra("sentDate", date);
                }
                startActivity(intent);
            }
        });

        transportation=findViewById(R.id.pub);
        transportation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Transportation.this, PublicTransportation.class);
                if(receivedDate){
                    intent.putExtra("sentDate", date);
                }
                startActivity(intent);
            }
        });

        walk=findViewById(R.id.walk);
        walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Transportation.this, Walking.class);
                if(receivedDate){
                    intent.putExtra("sentDate", date);
                }
                startActivity(intent);
            }
        });

        flight=findViewById(R.id.flight);
        flight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Transportation.this, Flight.class);
                if(receivedDate){
                    intent.putExtra("sentDate", date);
                }
                startActivity(intent);
            }
        });
    }
}