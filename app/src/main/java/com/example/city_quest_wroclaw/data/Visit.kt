package com.example.city_quest_wroclaw.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date

@Entity(tableName = "history")
data class Visit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "timestamp") val time: Date,
    @ColumnInfo(name = "attractionId") val attractionId: Int,
)
