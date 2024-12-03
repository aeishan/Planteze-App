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

public class Track extends AppCompatActivity {

    private String date;
    private boolean receivedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_track);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button transportation;
        Button food;
        Button shopping;
        Button back;

        receivedDate = false;
        Intent intent = getIntent();
        date = intent.getStringExtra("sentDate");

        if(date != null){
            receivedDate = true;
        }

        transportation=findViewById(R.id.transportation);
        transportation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Track.this, Transportation.class);
                if(receivedDate){
                    intent.putExtra("sentDate", date);
                }
                startActivity(intent);
            }
        });

        food=findViewById(R.id.food);
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Track.this, FoodConsumption.class);
                if(receivedDate){
                    intent.putExtra("sentDate", date);
                }
                startActivity(intent);
            }
        });

        shopping=findViewById(R.id.purchases);
        shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Track.this, Shopping.class);
                if(receivedDate){
                    intent.putExtra("sentDate", date);
                }
                startActivity(intent);
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Track.this, Tracker.class);
                if(receivedDate){
                    intent.putExtra("sentDate", date);
                }
                startActivity(intent);
            }
        });
    }
}