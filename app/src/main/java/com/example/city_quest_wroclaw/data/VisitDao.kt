package com.example.city_quest_wroclaw.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.city_quest_wroclaw.data.VisitWithAttraction

@Dao
interface VisitDao{
    @Transaction
    @Query("SELECT * FROM history JOIN ATTRACTIONS on attractionId=:visitId ")
    fun getAttractionWithItsVisits(visitId: Int): List<VisitWithAttraction>
}