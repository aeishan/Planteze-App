package com.plantezeapp.HabitSuggestions

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface HabitDao {
    @Insert
    suspend fun addHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    //Habit Filters
    @Query("SELECT * FROM habit_table WHERE name LIKE :searchQuery OR category LIKE :searchQuery")
    fun searchHabits(searchQuery: String): LiveData<List<Habit>>

    @Query("SELECT * FROM habit_table")
    fun getAllHabits(): LiveData<List<Habit>>
}