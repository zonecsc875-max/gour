package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.navigation.SantipurRoutingEngine
import com.example.data.repository.SantipurRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppLanguage {
    BENGALI,
    ENGLISH
}

enum class AppScreen {
    HOME,
    FESTIVALS_EXPLORER,
    RASH_YATRA_SPECIAL,
    KALI_PUJA_SPECIAL,
    DIRECTORY,
    PLACE_DETAIL,
    INTERACTIVE_MAP,
    CROWD_AVOIDANCE,
    ROUTE_PLANNER,
    CALENDAR,
    TRANSPORT_FACILITIES,
    HERITAGE_ARTICLES,
    SAVED_PLACES,
    CORRECTION_FORM,
    ADMIN_PANEL,
    ABOUT_SANTIPUR,
    IN_APP_NAVIGATION
}

data class UiState(
    val language: AppLanguage = AppLanguage.BENGALI,
    val currentScreen: AppScreen = AppScreen.HOME,
    val navigationStack: List<AppScreen> = listOf(AppScreen.HOME),
    val selectedPlaceId: String? = "boro_goswami",
    val searchQuery: String = "",
    val selectedCategoryFilter: PlaceCategory? = null,
    val userLat: Double = SantipurRepository.SANTIPUR_LAT,
    val userLng: Double = SantipurRepository.SANTIPUR_LNG,
    val hasLocationPermission: Boolean = false,
    val selectedCrowdFilter: CrowdStatus? = null,
    val toastMessage: String? = null,
    // In-app turn-by-turn navigation state
    val activeNavRoute: NavRoute? = null,
    val currentNavStepIndex: Int = 0,
    val selectedNavTransportMode: NavigationTransportMode = NavigationTransportMode.WALKING,
    val isVoiceGuidanceMuted: Boolean = false,
    val isNavSimulating: Boolean = false
)

class SantipurViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val savedPlaceIds = SantipurRepository.savedPlaceIds
    val crowdReports = SantipurRepository.crowdReports
    val advisories = SantipurRepository.advisories
    val userCorrections = SantipurRepository.userCorrections
    val customItineraryPlaceIds = SantipurRepository.customItineraryPlaceIds

    private var simulationJob: Job? = null

    fun toggleLanguage() {
        val next = if (_uiState.value.language == AppLanguage.BENGALI) AppLanguage.ENGLISH else AppLanguage.BENGALI
        _uiState.update { it.copy(language = next) }
    }

    fun navigateTo(screen: AppScreen, placeId: String? = null) {
        _uiState.update { current ->
            val newStack = current.navigationStack + screen
            current.copy(
                currentScreen = screen,
                navigationStack = newStack,
                selectedPlaceId = placeId ?: current.selectedPlaceId
            )
        }
    }

    fun openPlaceDetail(placeId: String) {
        navigateTo(AppScreen.PLACE_DETAIL, placeId)
    }

    // In-App Turn-by-Turn Navigation Engine triggers
    fun startInAppNavigation(
        destinationPlaceId: String,
        originPlaceId: String? = null,
        mode: NavigationTransportMode = _uiState.value.selectedNavTransportMode
    ) {
        val allPlaces = SantipurRepository.getAllPlaces()
        val destination = allPlaces.find { it.id == destinationPlaceId } ?: allPlaces.first()
        val origin = if (originPlaceId != null) allPlaces.find { it.id == originPlaceId } else null

        val route = SantipurRoutingEngine.generateRoute(
            destination = destination,
            originPlace = origin,
            mode = mode,
            userCurrentLat = _uiState.value.userLat,
            userCurrentLng = _uiState.value.userLng
        )

        _uiState.update { current ->
            val newStack = current.navigationStack + AppScreen.IN_APP_NAVIGATION
            current.copy(
                currentScreen = AppScreen.IN_APP_NAVIGATION,
                navigationStack = newStack,
                selectedPlaceId = destinationPlaceId,
                activeNavRoute = route,
                currentNavStepIndex = 0,
                selectedNavTransportMode = mode,
                isNavSimulating = false
            )
        }

        val msg = if (_uiState.value.language == AppLanguage.BENGALI) {
            "অভ্যন্তরীণ পথনির্দেশ শুরু হয়েছে: ${destination.nameBn}"
        } else {
            "In-app navigation started for: ${destination.nameEn}"
        }
        showToast(msg)
    }

    fun stopInAppNavigation() {
        simulationJob?.cancel()
        simulationJob = null
        _uiState.update {
            it.copy(
                activeNavRoute = null,
                currentNavStepIndex = 0,
                isNavSimulating = false
            )
        }
        navigateBack()
    }

    fun setNavTransportMode(mode: NavigationTransportMode) {
        val currentRoute = _uiState.value.activeNavRoute
        if (currentRoute != null) {
            val allPlaces = SantipurRepository.getAllPlaces()
            val destination = allPlaces.find { it.id == currentRoute.destinationPlaceId } ?: allPlaces.first()
            val updatedRoute = SantipurRoutingEngine.generateRoute(
                destination = destination,
                originPlace = null,
                mode = mode,
                userCurrentLat = _uiState.value.userLat,
                userCurrentLng = _uiState.value.userLng
            )
            _uiState.update {
                it.copy(
                    selectedNavTransportMode = mode,
                    activeNavRoute = updatedRoute,
                    currentNavStepIndex = 0
                )
            }
        } else {
            _uiState.update { it.copy(selectedNavTransportMode = mode) }
        }
    }

    fun nextNavStep() {
        val route = _uiState.value.activeNavRoute ?: return
        if (_uiState.value.currentNavStepIndex < route.steps.size - 1) {
            _uiState.update { it.copy(currentNavStepIndex = it.currentNavStepIndex + 1) }
        }
    }

    fun prevNavStep() {
        if (_uiState.value.currentNavStepIndex > 0) {
            _uiState.update { it.copy(currentNavStepIndex = it.currentNavStepIndex - 1) }
        }
    }

    fun toggleVoiceGuidance() {
        _uiState.update { it.copy(isVoiceGuidanceMuted = !it.isVoiceGuidanceMuted) }
        val msg = if (_uiState.value.isVoiceGuidanceMuted) {
            if (_uiState.value.language == AppLanguage.BENGALI) "ভয়েস গাইডেন্স নীরব করা হয়েছে" else "Voice muted"
        } else {
            if (_uiState.value.language == AppLanguage.BENGALI) "ভয়েস গাইডেন্স চালু করা হয়েছে" else "Voice enabled"
        }
        showToast(msg)
    }

    fun toggleNavSimulation() {
        val isSimulating = !_uiState.value.isNavSimulating
        _uiState.update { it.copy(isNavSimulating = isSimulating) }

        if (isSimulating) {
            simulationJob?.cancel()
            simulationJob = viewModelScope.launch {
                val route = _uiState.value.activeNavRoute ?: return@launch
                for (step in _uiState.value.currentNavStepIndex until route.steps.size) {
                    _uiState.update { it.copy(currentNavStepIndex = step) }
                    delay(4500) // Advance step every 4.5 seconds in simulation
                }
                _uiState.update { it.copy(isNavSimulating = false) }
                showToast("গন্তব্যে পৌঁছে গেছেন! (Arrived at destination!)")
            }
        } else {
            simulationJob?.cancel()
            simulationJob = null
        }
    }

    fun navigateBack(): Boolean {
        val currentStack = _uiState.value.navigationStack
        return if (currentStack.size > 1) {
            val updatedStack = currentStack.dropLast(1)
            val previousScreen = updatedStack.last()
            _uiState.update {
                it.copy(
                    currentScreen = previousScreen,
                    navigationStack = updatedStack
                )
            }
            true
        } else {
            false
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setCategoryFilter(category: PlaceCategory?) {
        _uiState.update { it.copy(selectedCategoryFilter = category) }
    }

    fun setCrowdFilter(status: CrowdStatus?) {
        _uiState.update { it.copy(selectedCrowdFilter = status) }
    }

    fun toggleSavePlace(placeId: String) {
        SantipurRepository.toggleSavePlace(placeId)
        val isSaved = SantipurRepository.isPlaceSaved(placeId)
        val msg = if (_uiState.value.language == AppLanguage.BENGALI) {
            if (isSaved) "সংরক্ষিত তালিকায় যোগ করা হয়েছে" else "সংরক্ষিত তালিকা থেকে সরানো হয়েছে"
        } else {
            if (isSaved) "Added to saved places" else "Removed from saved places"
        }
        showToast(msg)
    }

    fun addToCustomItinerary(placeId: String) {
        SantipurRepository.addToCustomItinerary(placeId)
        val msg = if (_uiState.value.language == AppLanguage.BENGALI) {
            "ভ্রমণ তালিকায় যোগ করা হয়েছে"
        } else {
            "Added to your tour itinerary"
        }
        showToast(msg)
    }

    fun removeFromCustomItinerary(placeId: String) {
        SantipurRepository.removeFromCustomItinerary(placeId)
    }

    fun reorderCustomItinerary(fromIndex: Int, toIndex: Int) {
        SantipurRepository.reorderCustomItinerary(fromIndex, toIndex)
    }

    fun submitCrowdReport(placeId: String, status: CrowdStatus, waitMinutes: Int, notes: String, author: String) {
        SantipurRepository.submitCrowdReport(placeId, status, waitMinutes, notes, author)
        val msg = if (_uiState.value.language == AppLanguage.BENGALI) {
            "আপনার ভিড় সংক্রান্ত তথ্য সফলভাবে জমা হয়েছে!"
        } else {
            "Your crowd report was submitted successfully!"
        }
        showToast(msg)
    }

    fun submitUserCorrection(placeName: String, contact: String, type: String, message: String) {
        SantipurRepository.submitUserCorrection(placeName, contact, type, message)
        val msg = if (_uiState.value.language == AppLanguage.BENGALI) {
            "ধন্যবাদ! তথ্য সংশোধনের আবেদন প্রশাসকের পর্যালোচনার জন্য জমা হয়েছে।"
        } else {
            "Thank you! Your correction submission has been sent for admin review."
        }
        showToast(msg)
    }

    fun adminApproveCorrection(id: String) {
        SantipurRepository.adminApproveCorrection(id)
        showToast("সংশোধন অনুমোদিত হয়েছে (Correction Approved)")
    }

    fun adminPublishAdvisory(titleBn: String, titleEn: String, messageBn: String, messageEn: String, severity: AdvisorySeverity) {
        SantipurRepository.adminPublishAdvisory(titleBn, titleEn, messageBn, messageEn, severity)
        showToast("নতুন ট্রাফিক সতর্কতা প্রকাশিত হয়েছে (Advisory Published)")
    }

    fun updateLocation(lat: Double, lng: Double, granted: Boolean) {
        _uiState.update {
            it.copy(
                userLat = lat,
                userLng = lng,
                hasLocationPermission = granted
            )
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    private fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun getFilteredPlaces(): List<Place> {
        val query = _uiState.value.searchQuery.trim().lowercase()
        val category = _uiState.value.selectedCategoryFilter
        val crowd = _uiState.value.selectedCrowdFilter

        return SantipurRepository.getAllPlaces().filter { place ->
            val matchesQuery = query.isEmpty() ||
                    place.nameBn.lowercase().contains(query) ||
                    place.nameEn.lowercase().contains(query) ||
                    place.locality.lowercase().contains(query) ||
                    place.deityBn.lowercase().contains(query) ||
                    place.deityEn.lowercase().contains(query)

            val matchesCategory = category == null || place.category == category
            val matchesCrowd = crowd == null || place.crowdStatus == crowd

            matchesQuery && matchesCategory && matchesCrowd
        }
    }

    fun getDistanceToPlace(place: Place): Double {
        return SantipurRepository.calculateDistanceKm(
            _uiState.value.userLat,
            _uiState.value.userLng,
            place.latitude,
            place.longitude
        )
    }
}
