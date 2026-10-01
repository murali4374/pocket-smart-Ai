package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_plans")
data class PlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val plannerType: String, // "home", "party", "jewelry"
    val title: String,
    val budget: Double,
    val estimatedTotal: Double,
    val remainingBudget: Double,
    val inputJson: String,
    val resultJson: String,
    val createdAt: Long = System.currentTimeMillis()
)
