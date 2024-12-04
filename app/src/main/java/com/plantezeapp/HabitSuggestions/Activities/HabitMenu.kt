package com.plantezeapp.HabitSuggestions.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.plantezeapp.HabitSuggestions.Habit
import com.plantezeapp.HabitSuggestions.HabitAdapter
import com.plantezeapp.HabitSuggestions.HabitDao
import com.plantezeapp.HabitSuggestions.HabitDatabase
import com.plantezeapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.plantezeapp.Track.Tracker
import java.util.*

class HabitMenu : AppCompatActivity() {

    private lateinit var habitAdapter: HabitAdapter
    private lateinit var habitDao: HabitDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_menu)

        val addHabitButton: FloatingActionButton = findViewById(R.id.addHabit)
        val recyclerView: RecyclerView = findViewById(R.id.rv_habits)
        val searchView: SearchView = findViewById(R.id.searchView)
        val goBackButton: FloatingActionButton = findViewById(R.id.goBack)

        // Initialize DAO
        val database = HabitDatabase.getDatabase(this)
        habitDao = database.habitDao()

        // Initialize HabitAdapter
        habitAdapter = HabitAdapter(mutableListOf(),
            { habitToDelete ->
                lifecycleScope.launch(Dispatchers.IO) {
                    habitDao.deleteHabit(habitToDelete)
                }
            },
            { habitToComplete ->
                onCompletedHabitClick(habitToComplete)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = habitAdapter

        // Observe Habit List
        habitDao.getAllHabits().observe(this, { habits ->
            habits?.let {
                habitAdapter.updateData(it)
            }
        })

        // Search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { filterHabits(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    habitDao.getAllHabits().observe(this@HabitMenu, { habits ->
                        habits?.let {
                            habitAdapter.updateData(it)
                        }
                    })
                } else {
                    filterHabits(newText)
                }
                return true
            }
        })

        // Navigate to HabitCreation
        addHabitButton.setOnClickListener {
            val intent = Intent(this, HabitCreation::class.java)
            startActivity(intent)
        }

        // Navigate to Tracker
        goBackButton.setOnClickListener {
            val intent = Intent(this, Tracker::class.java)
            startActivity(intent)
        }
    }

    private fun filterHabits(query: String) {
        habitDao.searchHabits("%$query%").observe(this, { habits ->
            habits?.let {
                habitAdapter.updateData(it)
            }
        })
    }

    private fun onCompletedHabitClick(habit: Habit) {
        // Increment the completion count
        val updatedHabit = habit.copy(completionCount = habit.completionCount + 1)

        // Update the habit in the database
        lifecycleScope.launch(Dispatchers.IO) {
            habitDao.updateHabit(updatedHabit)
        }

        // Notify the adapter to update the specific item
        habitAdapter.notifyDataSetChanged()

        // Show a Toast message
        Toast.makeText(this, "Habit Completed!", Toast.LENGTH_SHORT).show()
    }

}

