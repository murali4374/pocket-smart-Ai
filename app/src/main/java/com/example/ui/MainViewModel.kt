package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.PlanEntity
import com.example.data.model.*
import com.example.data.remote.BudgetRecommendationEngine
import com.example.data.repository.PlanRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiMessage {
    data class Success(val message: String) : UiMessage
    data class Error(val message: String) : UiMessage
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PlanRepository
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = PlanRepository(db.planDao())
    }

    val allPlans: StateFlow<List<PlanEntity>> = repository.allPlans.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // User message snackbar
    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    val uiMessage: StateFlow<UiMessage?> = _uiMessage.asStateFlow()

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    // ==========================================
    // 1. HOME INTERIOR PLANNER STATE
    // ==========================================
    var homeBudget = MutableStateFlow("50000")
    var homeRoomType = MutableStateFlow("Living Room")
    var homeStyle = MutableStateFlow("Modern")
    var homeColors = MutableStateFlow("Neutral, Beige")
    var homeItems = MutableStateFlow(
        listOf(
            HomeItemInput("Sofa Set", 1),
            HomeItemInput("Coffee Table", 1),
            HomeItemInput("Curtains & Blinds", 2)
        )
    )

    private val _isGeneratingHome = MutableStateFlow(false)
    val isGeneratingHome: StateFlow<Boolean> = _isGeneratingHome.asStateFlow()

    private val _homeResult = MutableStateFlow<HomePlanResult?>(null)
    val homeResult: StateFlow<HomePlanResult?> = _homeResult.asStateFlow()

    fun addHomeItem(name: String = "", qty: Int = 1) {
        homeItems.value = homeItems.value + HomeItemInput(name, qty)
    }

    fun removeHomeItem(index: Int) {
        if (homeItems.value.size > 1) {
            homeItems.value = homeItems.value.toMutableList().also { it.removeAt(index) }
        }
    }

    fun updateHomeItem(index: Int, name: String, qty: Int) {
        val list = homeItems.value.toMutableList()
        if (index in list.indices) {
            list[index] = HomeItemInput(name, qty)
            homeItems.value = list
        }
    }

    fun generateHomePlan() {
        val budgetVal = homeBudget.value.toDoubleOrNull() ?: 50000.0
        val validItems = homeItems.value.filter { it.itemName.isNotBlank() }
        val req = HomePlanRequest(
            budget = budgetVal,
            roomType = homeRoomType.value,
            style = homeStyle.value,
            preferredColors = homeColors.value,
            items = if (validItems.isNotEmpty()) validItems else listOf(HomeItemInput("Furniture Set", 1))
        )

        viewModelScope.launch {
            _isGeneratingHome.value = true
            try {
                val result = BudgetRecommendationEngine.generateHomePlan(req)
                _homeResult.value = result
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("Failed to calculate plan: ${e.message}")
            } finally {
                _isGeneratingHome.value = false
            }
        }
    }

    fun saveHomePlan() {
        val res = _homeResult.value ?: return
        viewModelScope.launch {
            try {
                val adapter = moshi.adapter(HomePlanResult::class.java)
                val json = adapter.toJson(res)
                val entity = PlanEntity(
                    plannerType = "home",
                    title = "${homeRoomType.value} (${homeStyle.value})",
                    budget = res.budget,
                    estimatedTotal = res.estimatedTotal,
                    remainingBudget = res.remainingBudget,
                    inputJson = "${homeStyle.value}, ${homeColors.value}",
                    resultJson = json
                )
                repository.savePlan(entity)
                _uiMessage.value = UiMessage.Success("Interior plan saved to history!")
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("Failed to save plan: ${e.message}")
            }
        }
    }

    // ==========================================
    // 2. PARTY EVENT PLANNER STATE
    // ==========================================
    var partyBudget = MutableStateFlow("60000")
    var partyEventType = MutableStateFlow("Birthday")
    var partyGuestCount = MutableStateFlow("35")
    var partyLocation = MutableStateFlow("Bangalore")
    var partyDate = MutableStateFlow("Tomorrow")
    var partyFoodPref = MutableStateFlow("Both Veg & Non-Veg")

    private val _isGeneratingParty = MutableStateFlow(false)
    val isGeneratingParty: StateFlow<Boolean> = _isGeneratingParty.asStateFlow()

    private val _partyResult = MutableStateFlow<PartyPlanResult?>(null)
    val partyResult: StateFlow<PartyPlanResult?> = _partyResult.asStateFlow()

    fun generatePartyPlan() {
        val budgetVal = partyBudget.value.toDoubleOrNull() ?: 60000.0
        val guests = partyGuestCount.value.toIntOrNull() ?: 35
        val req = PartyPlanRequest(
            budget = budgetVal,
            eventType = partyEventType.value,
            guestCount = guests,
            location = partyLocation.value.ifBlank { "City" },
            date = partyDate.value,
            foodPreference = partyFoodPref.value
        )

        viewModelScope.launch {
            _isGeneratingParty.value = true
            try {
                val result = BudgetRecommendationEngine.generatePartyPlan(req)
                _partyResult.value = result
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("Failed to calculate event plan: ${e.message}")
            } finally {
                _isGeneratingParty.value = false
            }
        }
    }

    fun savePartyPlan() {
        val res = _partyResult.value ?: return
        viewModelScope.launch {
            try {
                val adapter = moshi.adapter(PartyPlanResult::class.java)
                val json = adapter.toJson(res)
                val entity = PlanEntity(
                    plannerType = "party",
                    title = "${partyEventType.value} (${res.guestCount} Guests)",
                    budget = res.budget,
                    estimatedTotal = res.estimatedTotal,
                    remainingBudget = res.remainingBudget,
                    inputJson = "${partyLocation.value}, ${partyFoodPref.value}",
                    resultJson = json
                )
                repository.savePlan(entity)
                _uiMessage.value = UiMessage.Success("Party plan saved to history!")
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("Failed to save plan: ${e.message}")
            }
        }
    }

    // ==========================================
    // 3. JEWELRY & STYLING PLANNER STATE
    // ==========================================
    var jewelryBudget = MutableStateFlow("30000")
    var jewelryOccasion = MutableStateFlow("Wedding")
    var jewelryType = MutableStateFlow("Necklace")
    var jewelryStyle = MutableStateFlow("Traditional")
    var jewelryTone = MutableStateFlow("Gold")

    private val _selectedOutfitImageUri = MutableStateFlow<Uri?>(null)
    val selectedOutfitImageUri: StateFlow<Uri?> = _selectedOutfitImageUri.asStateFlow()

    private val _outfitBitmap = MutableStateFlow<Bitmap?>(null)
    val outfitBitmap: StateFlow<Bitmap?> = _outfitBitmap.asStateFlow()

    private val _isGeneratingJewelry = MutableStateFlow(false)
    val isGeneratingJewelry: StateFlow<Boolean> = _isGeneratingJewelry.asStateFlow()

    private val _jewelryResult = MutableStateFlow<JewelryPlanResult?>(null)
    val jewelryResult: StateFlow<JewelryPlanResult?> = _jewelryResult.asStateFlow()

    fun setOutfitImage(uri: Uri?) {
        _selectedOutfitImageUri.value = uri
        if (uri != null) {
            try {
                val stream = getApplication<Application>().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(stream)
                _outfitBitmap.value = bitmap
            } catch (e: Exception) {
                _outfitBitmap.value = null
            }
        } else {
            _outfitBitmap.value = null
        }
    }

    fun removeOutfitImage() {
        _selectedOutfitImageUri.value = null
        _outfitBitmap.value = null
    }

    fun generateJewelryPlan() {
        val budgetVal = jewelryBudget.value.toDoubleOrNull() ?: 30000.0
        val imageBase64 = _outfitBitmap.value?.let { BudgetRecommendationEngine.bitmapToBase64(it) }

        val req = JewelryPlanRequest(
            budget = budgetVal,
            occasion = jewelryOccasion.value,
            jewelryType = jewelryType.value,
            style = jewelryStyle.value,
            colorPreference = jewelryTone.value,
            outfitImageBase64 = imageBase64
        )

        viewModelScope.launch {
            _isGeneratingJewelry.value = true
            try {
                val result = BudgetRecommendationEngine.generateJewelryPlan(req)
                _jewelryResult.value = result
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("Failed to calculate jewelry plan: ${e.message}")
            } finally {
                _isGeneratingJewelry.value = false
            }
        }
    }

    fun saveJewelryPlan() {
        val res = _jewelryResult.value ?: return
        viewModelScope.launch {
            try {
                val adapter = moshi.adapter(JewelryPlanResult::class.java)
                val json = adapter.toJson(res)
                val entity = PlanEntity(
                    plannerType = "jewelry",
                    title = "${jewelryOccasion.value} ${jewelryTone.value} ${jewelryType.value}",
                    budget = res.budget,
                    estimatedTotal = res.estimatedTotal,
                    remainingBudget = res.remainingBudget,
                    inputJson = "${jewelryStyle.value}, ${jewelryTone.value}",
                    resultJson = json
                )
                repository.savePlan(entity)
                _uiMessage.value = UiMessage.Success("Jewelry styling plan saved to history!")
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("Failed to save plan: ${e.message}")
            }
        }
    }

    // ==========================================
    // 4. PLAN HISTORY ACTIONS
    // ==========================================
    fun deletePlan(id: Long) {
        viewModelScope.launch {
            try {
                repository.deletePlanById(id)
                _uiMessage.value = UiMessage.Success("Plan removed from history")
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("Failed to delete plan: ${e.message}")
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            try {
                repository.clearAllPlans()
                _uiMessage.value = UiMessage.Success("All plans cleared")
            } catch (e: Exception) {
                _uiMessage.value = UiMessage.Error("Failed to clear plans")
            }
        }
    }
}
