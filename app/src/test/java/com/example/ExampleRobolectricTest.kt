package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.HomeItemInput
import com.example.data.model.HomePlanRequest
import com.example.data.model.PartyPlanRequest
import com.example.data.model.JewelryPlanRequest
import com.example.data.remote.BudgetRecommendationEngine
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("PocketSmart AI", appName)
    }

    @Test
    fun `home plan allocation does not exceed budget`() = runBlocking {
        val budget = 50000.0
        val req = HomePlanRequest(
            budget = budget,
            roomType = "Living Room",
            style = "Modern",
            items = listOf(
                HomeItemInput("Sofa Set", 1),
                HomeItemInput("Coffee Table", 1)
            )
        )
        val result = BudgetRecommendationEngine.generateHomePlan(req)
        assertTrue("Estimated total should not exceed budget", result.estimatedTotal <= budget)
        assertTrue("Remaining budget should be >= 0", result.remainingBudget >= 0)
        assertTrue("Should have recommendations", result.recommendations.isNotEmpty())
    }

    @Test
    fun `party plan allocation respects budget ceiling`() = runBlocking {
        val budget = 60000.0
        val req = PartyPlanRequest(
            budget = budget,
            eventType = "Birthday",
            guestCount = 35,
            location = "Bangalore",
            date = "Tomorrow",
            foodPreference = "Both Veg & Non-Veg"
        )
        val result = BudgetRecommendationEngine.generatePartyPlan(req)
        assertTrue("Estimated total should not exceed budget", result.estimatedTotal <= budget)
        assertTrue("Cost per guest should be positive", result.costPerGuest > 0)
        assertEquals(35, result.guestCount)
    }

    @Test
    fun `jewelry plan allocation stays within limit`() = runBlocking {
        val budget = 30000.0
        val req = JewelryPlanRequest(
            budget = budget,
            occasion = "Wedding",
            jewelryType = "Necklace",
            style = "Traditional",
            colorPreference = "Gold"
        )
        val result = BudgetRecommendationEngine.generateJewelryPlan(req)
        assertTrue("Estimated total should not exceed budget", result.estimatedTotal <= budget)
        assertTrue("Should have recommendations", result.recommendations.isNotEmpty())
    }
}
