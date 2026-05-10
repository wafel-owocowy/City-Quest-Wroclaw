package com.example.city_quest_wroclaw.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attractions")
data class Attraction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val isVisited: Boolean = false,
    val imageResId: Int, // e.g. R.drawable.attraction_1
    val hasAudio: Boolean = false,
    val hasVideo: Boolean = false
)
