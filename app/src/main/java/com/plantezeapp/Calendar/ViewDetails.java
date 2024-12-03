package com.plantezeapp.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.plantezeapp.Database.EcoTracker;
import com.plantezeapp.Database.FirebaseHelper;
import com.plantezeapp.Database.User;
import com.plantezeapp.R;
import com.plantezeapp.Track.FoodConsumption;
import com.plantezeapp.Track.Information;
import com.plantezeapp.Track.Track;
import com.plantezeapp.Track.Tracker;

import java.util.HashMap;
import java.util.Map;

public class ViewDetails extends AppCompatActivity implements FirebaseHelper.UserFetchListener {

    private User user;
    private FirebaseHelper help;
    private FirebaseUser userFire;
    private String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button back;
        Intent intent = getIntent();
        date = intent.getStringExtra("sentDate");

        userFire = FirebaseAuth.getInstance().getCurrentUser();
        help = new FirebaseHelper();
        help.fetchUser(userFire.getUid(), this);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewDetails.this, Tracker.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onUserFetched(User user) {
        Log.d("MainActivity", "User fetched: " + user.getEmail());
        this.user = user;
        user.setuID(userFire.getUid());

        updateTextView();
    }

    @Override
    public void onFetchFailed(String errorMessage) {
        Log.d("MainActivity", "Error: User not Fetched" );
    }

    private void updateTextView(){
        TextView textView = findViewById(R.id.activities);
        StringBuilder builder = new StringBuilder();
        EcoTracker tracker = user.getEcoTracker();
        Map<String, Map<String, Object>> activityByDate;

        if(tracker.getActivityByDate() == null ||tracker.getActivityByDate().get(date) == null){
            textView.setText("No Data to Show");
            return;
        }
        else{
            activityByDate = tracker.getActivityByDate().get(date);
        }

        for (Map.Entry<String, Map<String, Object>> categoryEntry : activityByDate.entrySet()){
            Map<String, Object> activityData = categoryEntry.getValue();
            for (Map.Entry<String, Object> activityEntry : activityData.entrySet()){
                Map<String, Double> activity = (Map<String, Double>) activityEntry.getValue();

                for(String specific : activity.keySet()){
                    builder.append(specific).append(": ").append(activity.get(specific)).append("\n");
                }
            }
        }
        textView.setText(builder.toString());
    }
}