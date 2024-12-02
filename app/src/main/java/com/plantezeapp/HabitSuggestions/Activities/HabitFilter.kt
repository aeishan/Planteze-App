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

        co2ReductionFilter.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser //check this out, fetch.user?
            if (currentUser !=null) {
                fetchAndApplyFirebaseDataEmissions(
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

        activitiesFilter.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser //check this out, fetch.user?
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

    private fun fetchAndApplyFirebaseDataEmissions(
        userId: String,
        transportationFilter: ImageView,
        energyFilter: ImageView,
        foodFilter: ImageView,
        consumptionFilter: ImageView,
    ) {
        // Reference to the emission data for the current user
        val databaseReference = FirebaseDatabase.getInstance()
            .getReference("users/$userId/ecoTracker/emissionByDateAndCat")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    // Find latest date by comparing the keys (dd-mm-yy format)
                    var latestDate: String? = null
                    var maxTransport = 0.0
                    var maxFood = 0.0
                    var maxOther = 0.0

                    for (dateSnapshot in snapshot.children) {
                        val date = dateSnapshot.key
                        // Find the latest date
                        if (date != null && (latestDate == null || compareDates(date, latestDate) > 0)) {
                            latestDate = date
                        }
                    }

                    // If latest date found, fetch emissions for that day
                    if (latestDate != null) {
                        val emissionData = snapshot.child(latestDate)
                        maxTransport = emissionData.child("Transportation").getValue(Double::class.java) ?: 0.0
                        maxFood = emissionData.child("Food").getValue(Double::class.java) ?: 0.0
                        maxOther = emissionData.child("Other").getValue(Double::class.java) ?: 0.0
                    }

                    // Compare emissions & apply highest category filter
                    when {
                        maxTransport >= maxFood && maxTransport >= maxOther -> {
                            transportationFilter.isSelected = true
                            toggleCategory("Transportation", transportationFilter)
                        }
                        maxFood >= maxTransport && maxFood >= maxOther -> {
                            foodFilter.isSelected = true
                            toggleCategory("Food", foodFilter)
                        }
                        else -> {
                            energyFilter.isSelected = true
                            consumptionFilter.isSelected = true
                            toggleCategory("Energy", energyFilter)
                            toggleCategory("Consumption", consumptionFilter)
                        }
                    }

                } catch (e: Exception) {
                    Log.e("HabitFilter", "Error fetching or parsing Firebase emission data: ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HabitFilter", "Firebase Database error: ${error.message}")
            }
        })
    }

    // Helper function to compare dates in dd-mm-yy format
    private fun compareDates(date1: String, date2: String): Int {
        val dateParts1 = date1.split("-").map { it.toInt() }
        val dateParts2 = date2.split("-").map { it.toInt() }

        // Compare year, month, day
        return when {
            dateParts1[2] != dateParts2[2] -> dateParts1[2] - dateParts2[2] // Compare year
            dateParts1[1] != dateParts2[1] -> dateParts1[1] - dateParts2[1] // Compare month
            else -> dateParts1[0] - dateParts2[0] // Compare day
        }
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
                    val q1 = snapshot.child("Q0").getValue(String::class.java) ?: ""
                    val q10 = snapshot.child("Q12").getValue(String::class.java) ?: ""
                    val q17 = snapshot.child("Q19").getValue(String::class.java) ?: ""
                    val q18 = snapshot.child("Q20").getValue(String::class.java) ?: ""
                    val q21 = snapshot.child("Q23").getValue(String::class.java) ?: ""

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
