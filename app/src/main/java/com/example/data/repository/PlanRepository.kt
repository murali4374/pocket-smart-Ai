package com.example.data.repository

import com.example.data.local.PlanDao
import com.example.data.local.PlanEntity
import kotlinx.coroutines.flow.Flow

class PlanRepository(private val planDao: PlanDao) {
    val allPlans: Flow<List<PlanEntity>> = planDao.getAllPlans()

    fun getPlansByType(type: String): Flow<List<PlanEntity>> = planDao.getPlansByType(type)

    fun getPlanById(id: Long): Flow<PlanEntity?> = planDao.getPlanById(id)

    suspend fun savePlan(plan: PlanEntity): Long = planDao.insertPlan(plan)

    suspend fun deletePlanById(id: Long) = planDao.deletePlanById(id)

    suspend fun clearAllPlans() = planDao.clearAll()
}
