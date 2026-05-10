package com.example.city_quest_wroclaw.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AttractionDao {
    @Query("SELECT * FROM attractions")
    fun getAllAttractions(): Flow<List<Attraction>>

    @Query("SELECT * FROM attractions WHERE id = :id")
    fun getAttractionById(id: Int): Flow<Attraction>

    @Query("UPDATE attractions SET isVisited = :isVisited WHERE id = :id")
    suspend fun updateVisitedStatus(id: Int, isVisited: Boolean): Int
    
    @Update
    suspend fun update(attraction: Attraction): Int
    
    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insert(attraction: Attraction): Long
}
