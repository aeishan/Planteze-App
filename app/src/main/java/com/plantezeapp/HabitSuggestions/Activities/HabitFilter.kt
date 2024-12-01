package com.plantezeapp.HabitSuggestions.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.plantezeapp.R

class HabitFilter : AppCompatActivity() {

    // Initialize selectedCategories list
    private var selectedCategories = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_filter)

        // Get buttons & ImageViews
        val transportationFilter: ImageView = findViewById(R.id.TransportationSelected_filter)
        val energyFilter: ImageView = findViewById(R.id.EnergySelected_filter)
        val foodFilter: ImageView = findViewById(R.id.FoodSelected_filter)
        val consumptionFilter: ImageView = findViewById(R.id.ConsumptionSelected_filter)
        val co2ReductionFilter: Button = findViewById(R.id.filter_co2_reduction)
        val activitiesFilter: Button = findViewById(R.id.filter_previous_activity)
        val confirmButton: Button = findViewById(R.id.btn_confirm_filter)
        val backButton: Button = findViewById(R.id.back_filterToCreation)

        // Select category filters
        transportationFilter.setOnClickListener {
            transportationFilter.isSelected = !transportationFilter.isSelected
            toggleCategory("Transportation", transportationFilter)
        }

        energyFilter.setOnClickListener {
            energyFilter.isSelected = !energyFilter.isSelected
            toggleCategory("Energy", energyFilter)
        }

        foodFilter.setOnClickListener {
            foodFilter.isSelected = !foodFilter.isSelected
            toggleCategory("Food", foodFilter)
        }

        consumptionFilter.setOnClickListener {
            consumptionFilter.isSelected = !consumptionFilter.isSelected
            toggleCategory("Consumption", consumptionFilter)
        }

        // Co2 Reduction Filter NOT DONE
        co2ReductionFilter.setOnClickListener {
            //Put energy & consumption together
            //HashMap<String Date, HashMap<String Category, Double Emission>>
            //setter = answers

        }

        // Past Activities Filter NOT DONE
        activitiesFilter.setOnClickListener {
            //Hashmap<Question, Answer>
            //hashmap (activity, response)

        }

        // Navigate from HabitFilter to HabitCreation
        confirmButton.setOnClickListener {
            // Pass the selected categories as an Intent extra
            val intent = Intent(this, HabitCreation::class.java)
            intent.putStringArrayListExtra("selectedCategories", ArrayList(selectedCategories))
            startActivity(intent)
        }

        // Navigate from HabitFilter to HabitCreation without filters
        backButton.setOnClickListener {
            val intent = Intent(this, HabitCreation::class.java)
            startActivity(intent)
        }
    }

    // Toggle category selection and manage the list of selected categories
    private fun toggleCategory(category: String, filterView: ImageView) {
        if (filterView.isSelected) {
            // Add category to list when selected
            selectedCategories.add(category)
        } else {
            // Remove category from list when deselected
            selectedCategories.remove(category)
        }
    }
}