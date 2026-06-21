package com.example.city_quest_wroclaw.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface StatisticsDao{

    @Query("SELECT COUNT(*) FROM history")
    fun getVisitCount(): Int
}