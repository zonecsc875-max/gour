package com.example.data.model

enum class NavigationTransportMode(
    val titleBn: String,
    val titleEn: String,
    val iconName: String,
    val averageSpeedKmh: Double
) {
    WALKING("পায়ে হেঁটে (পদব্রজে)", "Walking", "directions_walk", 4.2),
    TOTO("টোটো / অটো", "Toto (E-Rickshaw)", "electric_rickshaw", 14.0),
    DRIVING("গাড়ি / বাইক", "Car & 2-Wheeler", "directions_car", 20.0)
}

enum class NavManeuver {
    DEPART,
    STRAIGHT,
    TURN_LEFT,
    TURN_RIGHT,
    SLIGHT_LEFT,
    SLIGHT_RIGHT,
    ROUNDABOUT,
    DESTINATION
}

data class NavStep(
    val stepIndex: Int,
    val instructionBn: String,
    val instructionEn: String,
    val streetNameBn: String,
    val streetNameEn: String,
    val distanceMeters: Int,
    val maneuver: NavManeuver,
    val latitude: Double,
    val longitude: Double,
    val advisoryNoticeBn: String? = null,
    val advisoryNoticeEn: String? = null
)

data class NavRoute(
    val originNameBn: String,
    val originNameEn: String,
    val destinationNameBn: String,
    val destinationNameEn: String,
    val destinationPlaceId: String,
    val mode: NavigationTransportMode,
    val totalDistanceMeters: Int,
    val estimatedMinutes: Int,
    val steps: List<NavStep>,
    val coordinates: List<Pair<Double, Double>>,
    val hasFestivalRestrictions: Boolean = false,
    val festivalNoticeBn: String? = null
)
