package com.fit2081.a1_grace_35721383.ui.food

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FoodIntakeDao {

    // Insert or replace a food intake entry
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodIntake(foodIntake: FoodIntake)

    // Get all food intake entries (useful for debugging or logs)
    @Query("SELECT * FROM food_intake_table")
    suspend fun getAllFoodIntakes(): List<FoodIntake>

    // Get the latest food intake for a specific user (if only storing one entry per user)
    @Query("SELECT * FROM food_intake_table WHERE userId = :userId LIMIT 1")
    suspend fun getFoodIntakeByUserId(userId: Int): FoodIntake?
}