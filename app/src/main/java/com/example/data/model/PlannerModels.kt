package com.example.data.model

import com.squareup.moshi.JsonClass

// --- Common Platform Helper ---
fun makeSearchUrl(platform: String, query: String): String {
    val encoded = java.net.URLEncoder.encode(query, "UTF-8")
    val plat = platform.lowercase()
    return when {
        plat.contains("ikea") -> "https://www.ikea.com/in/en/search/?q=$encoded"
        plat.contains("flipkart") -> "https://www.flipkart.com/search?q=$encoded"
        plat.contains("swiggy") -> "https://www.swiggy.com/search?query=$encoded"
        plat.contains("zomato") -> "https://www.zomato.com/search?q=$encoded"
        plat.contains("oyo") -> "https://www.oyorooms.com/search?keyword=$encoded"
        else -> "https://www.amazon.in/s?k=$encoded"
    }
}

// ==========================================
// 1. HOME INTERIOR MODELS
// ==========================================
@JsonClass(generateAdapter = true)
data class HomeItemInput(
    val itemName: String,
    val quantity: Int = 1
)

@JsonClass(generateAdapter = true)
data class HomePlanRequest(
    val budget: Double,
    val roomType: String,
    val style: String,
    val preferredColors: String = "Neutral",
    val items: List<HomeItemInput>
)

@JsonClass(generateAdapter = true)
data class HomeRecommendation(
    val category: String,
    val product: String,
    val estimatedPrice: Double,
    val quantity: Int,
    val totalPrice: Double,
    val reason: String,
    val platform: String,
    val searchUrl: String = ""
)

@JsonClass(generateAdapter = true)
data class HomePlanResult(
    val budget: Double,
    val estimatedTotal: Double,
    val remainingBudget: Double,
    val recommendations: List<HomeRecommendation>
)

// ==========================================
// 2. PARTY EVENT MODELS
// ==========================================
@JsonClass(generateAdapter = true)
data class PartyPlanRequest(
    val budget: Double,
    val eventType: String,
    val guestCount: Int,
    val location: String,
    val date: String,
    val foodPreference: String
)

@JsonClass(generateAdapter = true)
data class PartyBreakdown(
    val food: Double = 0.0,
    val venue: Double = 0.0,
    val decoration: Double = 0.0,
    val entertainment: Double = 0.0,
    val photography: Double = 0.0,
    val miscellaneous: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class PartyRecommendation(
    val category: String,
    val product: String,
    val totalPrice: Double,
    val reason: String,
    val platform: String,
    val searchUrl: String = ""
)

@JsonClass(generateAdapter = true)
data class PartyPlanResult(
    val budget: Double,
    val estimatedTotal: Double,
    val remainingBudget: Double,
    val guestCount: Int = 1,
    val costPerGuest: Double = 0.0,
    val breakdown: PartyBreakdown = PartyBreakdown(),
    val recommendations: List<PartyRecommendation>
)

// ==========================================
// 3. JEWELRY & STYLING MODELS
// ==========================================
@JsonClass(generateAdapter = true)
data class JewelryPlanRequest(
    val budget: Double,
    val occasion: String,
    val jewelryType: String,
    val style: String,
    val colorPreference: String,
    val outfitImageBase64: String? = null
)

@JsonClass(generateAdapter = true)
data class OutfitAnalysis(
    val outfitColor: String,
    val outfitStyle: String,
    val suitableJewelryColor: String
)

@JsonClass(generateAdapter = true)
data class JewelryRecommendation(
    val category: String,
    val product: String,
    val totalPrice: Double,
    val reason: String,
    val platform: String,
    val searchUrl: String = ""
)

@JsonClass(generateAdapter = true)
data class JewelryPlanResult(
    val budget: Double,
    val estimatedTotal: Double,
    val remainingBudget: Double,
    val outfitAnalysis: OutfitAnalysis? = null,
    val recommendations: List<JewelryRecommendation>
)
