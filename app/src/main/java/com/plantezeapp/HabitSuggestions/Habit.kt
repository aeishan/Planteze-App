package com.plantezeapp.HabitSuggestions

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_table")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    var completionCount: Int = 0
)

// Create a list of habit suggestions
val habitSuggestions = listOf(
    // Transportation
    Habit(name = "Walk instead of driving", category = "Transportation"),
    Habit(name = "Use public transport", category = "Transportation"),
    Habit(name = "Ride a bike to work or school", category = "Transportation"),
    Habit(name = "Use ride-sharing services when public transport isn't available", category = "Transportation"),

    // Energy
    Habit(name = "Turn off lights when leaving a room", category = "Energy"),
    Habit(name = "Unplug devices when not in use", category = "Energy"),
    Habit(name = "Set electronics to energy-saving mode", category = "Energy"),
    Habit(name = "Limit air conditioning usage", category = "Energy"),

    // Food
    Habit(name = "Cook at home at least once a day", category = "Food"),
    Habit(name = "Compost food scraps daily", category = "Food"),
    Habit(name = "Freeze leftovers for later use", category = "Food"),
    Habit(name = "Use reusable containers for meals", category = "Food"),

    // Consumption
    Habit(name = "Bring your own bags when shopping", category = "Consumption"),
    Habit(name = "Recycle paper, plastic, and metal", category = "Consumption"),
    Habit(name = "Donate used clothes instead of discarding", category = "Consumption"),
    Habit(name = "Buy second-hand items instead of new ones", category = "Consumption")
)