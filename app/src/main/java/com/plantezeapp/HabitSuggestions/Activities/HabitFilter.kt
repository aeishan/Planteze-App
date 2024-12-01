package com.plantezeapp.HabitSuggestions.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.plantezeapp.R

class HabitFilter : AppCompatActivity() {

    private var selectedCategories = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_habit_filter)

        val transportationFilter: ImageView = findViewById(R.id.TransportationSelected_filter)
        val energyFilter: ImageView = findViewById(R.id.EnergySelected_filter)
        val foodFilter: ImageView = findViewById(R.id.FoodSelected_filter)
        val consumptionFilter: ImageView = findViewById(R.id.ConsumptionSelected_filter)
        val co2ReductionFilter: Button = findViewById(R.id.filter_co2_reduction)
        val activitiesFilter: Button = findViewById(R.id.filter_previous_activity)
        val confirmButton: Button = findViewById(R.id.btn_confirm_filter)
        val backButton: Button = findViewById(R.id.back_filterToCreation)

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

        // CO2 reduction filter NOT DONE
        co2ReductionFilter.setOnClickListener {
            /* val databaseReference = FirebaseDatabase.getInstance()
                .getReference("users/$userId/ecoTracker/emissionByDateAndCat/dd-mm-yy") */
        }

        activitiesFilter.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                fetchAndApplyFirebaseDataActivities(
                    currentUser.uid,
                    transportationFilter,
                    energyFilter,
                    foodFilter,
                    consumptionFilter
                )
            } else {
                Log.e("HabitFilter", "No authenticated user found.")
            }
        }

        confirmButton.setOnClickListener {
            if (selectedCategories.isNotEmpty()) {
                val intent = Intent(this, HabitCreation::class.java)
                intent.putStringArrayListExtra("selectedCategories", ArrayList(selectedCategories))
                startActivity(intent)
            } else {
                Log.d("HabitFilter", "No categories selected, cannot proceed")
                Toast.makeText(applicationContext,"No filters added.", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            val intent = Intent(this, HabitCreation::class.java)
            startActivity(intent)
        }
    }

    //Adding filters based on selected list
    private fun toggleCategory(category: String, filterView: ImageView) {
        if (filterView.isSelected) {
            selectedCategories.add(category)
        } else {
            selectedCategories.remove(category)
        }
        Log.d("HabitFilter", "Updated selected categories: $selectedCategories")
    }

    private fun fetchAndApplyFirebaseDataActivities(
        userId: String, //$userId
        transportationFilter: ImageView,
        energyFilter: ImageView,
        foodFilter: ImageView,
        consumptionFilter: ImageView,
    ) {
        val databaseReference = FirebaseDatabase.getInstance()
            .getReference("users/$userId/carbonFootprint/answers")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val q1 = snapshot.child("0").getValue(String::class.java) ?: ""
                    val q10 = snapshot.child("12").getValue(String::class.java) ?: ""
                    val q17 = snapshot.child("19").getValue(String::class.java) ?: ""
                    val q18 = snapshot.child("20").getValue(String::class.java) ?: ""
                    val q21 = snapshot.child("23").getValue(String::class.java) ?: ""

                    if ((q1 == "Yes")) {
                        transportationFilter.isSelected = true
                        toggleCategory("Transportation", transportationFilter)
                    }
                    if ((q10 == "Frequently" || q10 == "Occasionally")) {
                        energyFilter.isSelected = true
                        toggleCategory("Food", foodFilter)
                    }
                    if ((q17 == "No")) {
                        foodFilter.isSelected = true
                        toggleCategory("Energy", energyFilter)
                    }
                    if ((q18 == "Monthly" || q18 == "Quarterly")) {
                        consumptionFilter.isSelected = true
                        toggleCategory("Consumption", consumptionFilter)
                    }
                    if ((q21 == "Never" || q21 == "Occasionally")) {
                        consumptionFilter.isSelected = true
                        toggleCategory("Consumption", consumptionFilter)
                    }

                } catch (e: Exception) {
                    Log.e("HabitFilter", "Error parsing Firebase data: ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HabitFilter", "Firebase Database error: ${error.message}")
            }
        })
    }
}
