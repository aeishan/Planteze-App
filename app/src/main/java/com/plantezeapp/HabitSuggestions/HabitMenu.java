package com.plantezeapp.HabitSuggestions;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.plantezeapp.R;

import java.util.List;


public class HabitMenu extends Fragment {

    public HabitMenu() {
        super(R.layout.fragment_habit_menu); // Pass the layout resource to the Fragment constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton addHabits = view.findViewById(R.id.addHabit);

        //Navigate from HabitMenu to HabitCreation
        addHabits.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_habitMenu_to_habitCreation);
        });

    }
}