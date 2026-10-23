package com.example.data.navigation

import com.example.data.model.*
import com.example.data.repository.SantipurRepository
import kotlin.math.roundToInt

object SantipurRoutingEngine {

    fun generateRoute(
        destination: Place,
        originPlace: Place?,
        mode: NavigationTransportMode,
        userCurrentLat: Double,
        userCurrentLng: Double
    ): NavRoute {
        val originLat = originPlace?.latitude ?: userCurrentLat
        val originLng = originPlace?.longitude ?: userCurrentLng

        val originNameBn = originPlace?.nameBn ?: "আপনার বর্তমান অবস্থান (Current Location)"
        val originNameEn = originPlace?.nameEn ?: "Your Current Location"

        val directDistKm = SantipurRepository.calculateDistanceKm(
            originLat, originLng, destination.latitude, destination.longitude
        )

        // Road network detour factor (actual road distance is ~1.25x direct distance)
        val roadDistKm = directDistKm * 1.25
        val totalMeters = (roadDistKm * 1000).roundToInt().coerceAtLeast(350)

        // Estimated minutes based on transport mode and festival congestion
        val speedKmh = mode.averageSpeedKmh
        val rawMinutes = (roadDistKm / speedKmh * 60).roundToInt()
        val congestionMinutes = when (mode) {
            NavigationTransportMode.WALKING -> 2
            NavigationTransportMode.TOTO -> 4
            NavigationTransportMode.DRIVING -> 7 // Vehicle delays in narrow fair lanes
        }
        val estimatedMinutes = (rawMinutes + congestionMinutes).coerceAtLeast(3)

        // Generate realistic intermediate route coordinates for Canvas polyline
        val polyline = mutableListOf<Pair<Double, Double>>()
        polyline.add(originLat to originLng)

        // Synthesize realistic street waypoints between origin and destination
        val midLat1 = originLat + (destination.latitude - originLat) * 0.35 + 0.0008
        val midLng1 = originLng + (destination.longitude - originLng) * 0.35 - 0.0006
        polyline.add(midLat1 to midLng1)

        val midLat2 = originLat + (destination.latitude - originLat) * 0.70 - 0.0004
        val midLng2 = originLng + (destination.longitude - originLng) * 0.70 + 0.0005
        polyline.add(midLat2 to midLng2)

        polyline.add(destination.latitude to destination.longitude)

        // Step 1: Start
        val step1Dist = (totalMeters * 0.25).roundToInt()
        val step1 = NavStep(
            stepIndex = 0,
            instructionBn = "যাত্রা শুরু করুন: প্রধান রাস্তা বরাবর ${step1Dist} মিটার সোজা এগিয়ে যান",
            instructionEn = "Head straight on the main road for ${step1Dist}m",
            streetNameBn = "শান্তিপুর লিংক রোড",
            streetNameEn = "Santipur Link Road",
            distanceMeters = step1Dist,
            maneuver = NavManeuver.DEPART,
            latitude = originLat,
            longitude = originLng,
            advisoryNoticeBn = if (mode == NavigationTransportMode.DRIVING) "উৎসবের কারণে সামনে যান চলাচল একমুখী" else null,
            advisoryNoticeEn = if (mode == NavigationTransportMode.DRIVING) "One-way traffic ahead due to festival" else null
        )

        // Step 2: Intermediate turn onto historical corridor
        val step2Dist = (totalMeters * 0.45).roundToInt()
        val step2 = NavStep(
            stepIndex = 1,
            instructionBn = "নেতাজি সুভাষ রোড ও সুত্রাগড় মোড়ে ডানদিকে বাঁক নিন এবং ${step2Dist} মিটার চলুন",
            instructionEn = "Turn right onto Netaji Subhas Road / Sutragarh corridor and proceed for ${step2Dist}m",
            streetNameBn = "নেতাজি সুভাষ রোড (ঐতিহাসিক রাস পরিক্রমা পথ)",
            streetNameEn = "Netaji Subhas Road (Rash Procession Corridor)",
            distanceMeters = step2Dist,
            maneuver = NavManeuver.TURN_RIGHT,
            latitude = midLat1,
            longitude = midLng1,
            advisoryNoticeBn = "পথচারী ভক্তদের ভিড় রয়েছে, সতর্ক থাকুন",
            advisoryNoticeEn = "Pedestrian devotee crowds, navigate carefully"
        )

        // Step 3: Local precinct lane turn
        val step3Dist = (totalMeters * 0.20).roundToInt()
        val step3 = NavStep(
            stepIndex = 2,
            instructionBn = "${destination.locality} প্রবেশদ্বারে সামান্য বামে গিয়ে পদব্রজে এগিয়ে যান",
            instructionEn = "Slight left at ${destination.locality} archway into the temple lane",
            streetNameBn = "${destination.locality} লেন",
            streetNameEn = "${destination.locality} Lane",
            distanceMeters = step3Dist,
            maneuver = NavManeuver.SLIGHT_LEFT,
            latitude = midLat2,
            longitude = midLng2,
            advisoryNoticeBn = destination.accessibilityNotesBn,
            advisoryNoticeEn = destination.accessibilityNotesEn
        )

        // Step 4: Final arrival
        val step4 = NavStep(
            stepIndex = 3,
            instructionBn = "আপনি পৌঁছে গেছেন: ${destination.nameBn}! গন্তব্য আপনার সামনে।",
            instructionEn = "You have arrived at: ${destination.nameEn}! Destination is in front of you.",
            streetNameBn = destination.address,
            streetNameEn = destination.address,
            distanceMeters = 0,
            maneuver = NavManeuver.DESTINATION,
            latitude = destination.latitude,
            longitude = destination.longitude,
            advisoryNoticeBn = "দর্শনের সময়: ${destination.darshanTimings}",
            advisoryNoticeEn = "Darshan Timings: ${destination.darshanTimings}"
        )

        val isRestricted = destination.isRashParticipant || destination.isKaliSpecial

        return NavRoute(
            originNameBn = originNameBn,
            originNameEn = originNameEn,
            destinationNameBn = destination.nameBn,
            destinationNameEn = destination.nameEn,
            destinationPlaceId = destination.id,
            mode = mode,
            totalDistanceMeters = totalMeters,
            estimatedMinutes = estimatedMinutes,
            steps = listOf(step1, step2, step3, step4),
            coordinates = polyline,
            hasFestivalRestrictions = isRestricted,
            festivalNoticeBn = if (isRestricted) "শান্তিপুর ট্রাফিক পুলিশ সতর্কতা: উৎসব চলাকালীন সুত্রাগড় মোড় থেকে একমুখী রুট কার্যকর রয়েছে।" else null
        )
    }
}
