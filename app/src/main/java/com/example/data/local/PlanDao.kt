package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Query("SELECT * FROM saved_plans ORDER BY createdAt DESC")
    fun getAllPlans(): Flow<List<PlanEntity>>

    @Query("SELECT * FROM saved_plans WHERE plannerType = :type ORDER BY createdAt DESC")
    fun getPlansByType(type: String): Flow<List<PlanEntity>>

    @Query("SELECT * FROM saved_plans WHERE id = :id LIMIT 1")
    fun getPlanById(id: Long): Flow<PlanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: PlanEntity): Long

    @Query("DELETE FROM saved_plans WHERE id = :id")
    suspend fun deletePlanById(id: Long)

    @Query("DELETE FROM saved_plans")
    suspend fun clearAll()
}
