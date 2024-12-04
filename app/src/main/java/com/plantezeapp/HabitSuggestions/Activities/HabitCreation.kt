package com.plantezeapp.HabitSuggestions.Activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.plantezeapp.HabitSuggestions.HabitDatabase
import com.plantezeapp.HabitSuggestions.NotificationReceiver
import com.plantezeapp.HabitSuggestions.habitSuggestions
import com.plantezeapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class HabitCreation : AppCompatActivity() {

    private lateinit var habitDatabase: HabitDatabase
    private var selectedCategories: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_creation)

        // Get database
        habitDatabase = HabitDatabase.getDatabase(this)

        // Get selected categories from the intent
        selectedCategories = intent.getStringArrayListExtra("selectedCategories") ?: emptyList()

        // Get buttons & dropdown
        val chooseFilterButton: Button = findViewById(R.id.choose_filter)
        val chooseConfirmButton: Button = findViewById(R.id.btn_confirm_creation)
        val habitSpinner: Spinner = findViewById(R.id.HabitSpinner)
        val backButton: Button = findViewById(R.id.back_CreationToMenu)

        // If no categories are selected, show all habits; otherwise, filter based on selected categories
        val filteredHabits = if (selectedCategories.isEmpty()) {
            habitSuggestions // Show all habits if no categories are selected
        } else {
            habitSuggestions.filter { it.category in selectedCategories } // Filter by selected categories
        }

        // Extract habit names from filtered habits
        val habitNames = filteredHabits.map { it.name }

        // Make Adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, habitNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set adapter to Spinner
        habitSpinner.adapter = adapter

        // Navigate to HabitFilterActivity
        chooseFilterButton.setOnClickListener {
            val intent = Intent(this, HabitFilter::class.java)
            startActivity(intent)
        }

        // Navigate back to HabitMenu
        backButton.setOnClickListener {
            val intent = Intent(this, HabitMenu::class.java)
            startActivity(intent)
        }

        // Save selected habit to the database
        chooseConfirmButton.setOnClickListener {
            val selectedHabitName = habitSpinner.selectedItem.toString()
            val selectedHabit = filteredHabits.find { it.name == selectedHabitName }

            // Make sure a valid habit is selected
            if (selectedHabit != null) {
                lifecycleScope.launch {
                    habitDatabase.habitDao().addHabit(selectedHabit) // Insert the habit into the database
                    Toast.makeText(this@HabitCreation, "Habit Started!", Toast.LENGTH_SHORT).show()

                    // Check and schedule reminders based on active habits
                    updateHabitReminder()

                    // Navigate to HabitMenuActivity
                    val intent = Intent(this@HabitCreation, HabitMenu::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this, "Error: Habit not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Updates the reminder based on whether there are active habits
    private fun updateHabitReminder() {
        lifecycleScope.launch(Dispatchers.IO) {
            val activeHabits = habitDatabase.habitDao().getAllHabitsSync() // Fetch active habits synchronously
            withContext(Dispatchers.Main) {
                if (activeHabits.isNotEmpty()) {
                    scheduleHabitReminder()
                } else {
                    cancelHabitReminder()
                }
            }
        }
    }

    // Schedules a daily reminder
    private fun scheduleHabitReminder() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    // Cancels the daily reminder
    private fun cancelHabitReminder() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}
