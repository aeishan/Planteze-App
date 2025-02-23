package com.plantezeapp.HabitSuggestions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.plantezeapp.R

class HabitAdapter(
    private var habits: MutableList<Habit>,
    private val onDeleteClick: (Habit) -> Unit,
    private val onCompletedHabitClick: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val habitTitle: TextView = view.findViewById(R.id.habit_name)
        val habitCategory: TextView = view.findViewById(R.id.categoryText)
        val deleteButton: ImageView = view.findViewById(R.id.deleteButton)
        val completedHabitButton: Button = view.findViewById(R.id.completed_habit_button)
        val habitDaysCompleted: TextView = view.findViewById(R.id.habit_daysCompleted)
        val completedHabitText: TextView = view.findViewById(R.id.completedHabitText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_habit_item, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.habitTitle.text = habit.name
        holder.habitCategory.text = habit.category
        holder.habitDaysCompleted.text = habit.completionCount.toString() // Show completion count
        holder.completedHabitText.text = "Completed Habit: "

        holder.deleteButton.setOnClickListener {
            onDeleteClick(habit)  // Call the delete function when the delete button is clicked
            //loop dictionary -> go through whcihever is bigger -> values get keys
            //HashMap<String Date, HashMap<String Category, Double Emission>>
        }

        holder.completedHabitButton.setOnClickListener {
            onCompletedHabitClick(habit)
        }
    }

    override fun getItemCount(): Int = habits.size

    // Update the list and notify changes
    fun updateData(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
}

