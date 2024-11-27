package com.plantezeapp.HabitSuggestions;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.plantezeapp.R;

public class HabitFilter extends Fragment {

    public HabitFilter() {
        super(R.layout.fragment_habit_filter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get buttons & ImageViews
        ImageView transportationFilter = view.findViewById(R.id.TransportationSelected_filter);
        ImageView energyFilter = view.findViewById(R.id.EnergySelected_filter);
        ImageView foodFilter = view.findViewById(R.id.FoodSelected_filter);
        ImageView consumptionFilter = view.findViewById(R.id.ConsumptionSelected_filter);
        Button co2ReductionPotentialFilter = view.findViewById(R.id.filter_co2_reduction);
        Button activityFilter = view.findViewById(R.id.filter_previous_activity);
        Button confirmButton = view.findViewById(R.id.btn_confirm_filter);


        //Navigate from HabitFilter to HabitCreation
        confirmButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_habitFilter_to_habitCreation);
        });
    }

}