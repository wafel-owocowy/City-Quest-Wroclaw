package com.example.city_quest_wroclaw.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AchievementDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(achievement: Achievement)

    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): LiveData<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE id = :achievementID LIMIT 1")
    suspend fun findById(achievementID: Int): Achievement?
}
