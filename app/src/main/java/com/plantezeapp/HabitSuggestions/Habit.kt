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
    Habit(name = "Ride a bike to work", category = "Transportation"),
    Habit(name = "Carpool with others", category = "Transportation"),

    // Energy
    Habit(name = "Turn off lights when leaving a room", category = "Energy"),
    Habit(name = "Unplug devices when not in use", category = "Energy"),
    Habit(name = "Use energy-efficient appliances", category = "Energy"),
    Habit(name = "Limit air conditioning usage", category = "Energy"),

    // Food
    Habit(name = "Cook at home", category = "Food"),
    Habit(name = "Reduce food waste", category = "Food"),
    Habit(name = "Buy local produce", category = "Food"),
    Habit(name = "Use reusable containers for meals", category = "Food"),

    // Consumption
    Habit(name = "Avoid single-use plastics", category = "Consumption"),
    Habit(name = "Recycle paper, plastic, and metal", category = "Consumption"),
    Habit(name = "Donate used clothes instead of discarding", category = "Consumption"),
    Habit(name = "Buy second-hand items", category = "Consumption")
)