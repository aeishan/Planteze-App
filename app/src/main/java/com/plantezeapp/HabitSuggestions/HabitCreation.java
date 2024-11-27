package com.plantezeapp.HabitSuggestions;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.plantezeapp.R;

public class HabitCreation extends Fragment {

    public HabitCreation() {
        super(R.layout.fragment_habit_creation);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get buttons & dropdown
        View chooseFilterButton = view.findViewById(R.id.choose_filter);
        View chooseConfirmButton = view.findViewById(R.id.btn_confirm_creation);

        //Navigate from HabitCreation to HabitFilter
        chooseFilterButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_habitCreation_to_habitFilter);
        });

    }

}