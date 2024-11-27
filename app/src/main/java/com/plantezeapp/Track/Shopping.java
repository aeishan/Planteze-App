package com.plantezeapp.Track;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.plantezeapp.R;
import com.plantezeapp.Track.ShoppingItems.Bills;
import com.plantezeapp.Track.ShoppingItems.Clothing;
import com.plantezeapp.Track.ShoppingItems.Electronics;
import com.plantezeapp.Track.ShoppingItems.Other;
import com.plantezeapp.Track.TransportationTypes.Walking;

public class Shopping extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button back;
        Button clothing;
        Button electronics;
        Button bills;
        Button other;

        clothing=findViewById(R.id.clothing);
        clothing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Shopping.this, Clothing.class);
                startActivity(intent);
            }
        });

        electronics=findViewById(R.id.electronics);
        electronics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Shopping.this, Electronics.class);
                startActivity(intent);
            }
        });

        bills=findViewById(R.id.bills);
        bills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Shopping.this, Bills.class);
                startActivity(intent);
            }
        });

        other=findViewById(R.id.other);
        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Shopping.this, Other.class);
                startActivity(intent);
            }
        });

        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Shopping.this, Track.class);
                startActivity(intent);
            }
        });
    }
}