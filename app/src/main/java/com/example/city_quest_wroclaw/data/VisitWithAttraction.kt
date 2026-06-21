package com.example.city_quest_wroclaw.data

import androidx.room.Embedded
import androidx.room.Relation

data class VisitWithAttraction(
    @Embedded
    val attraction: Attraction,
    @Relation(
        entity = Visit::class,
        parentColumn = "id",
        entityColumn = "attractionId"
    )
    val visitList: List<Visit>
)