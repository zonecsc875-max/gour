package com.example.data.model

data class Festival(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val bengaliSeason: String,
    val approximateDate: String,
    val historyBn: String,
    val historyEn: String,
    val significanceBn: String,
    val significanceEn: String,
    val keyRitualsBn: List<String>,
    val participatingPlacesCount: Int,
    val isPrimaryFeature: Boolean = false,
    val accentTag: String = "ঐতিহ্যবাহী উৎসব"
)

data class Advisory(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val messageBn: String,
    val messageEn: String,
    val severity: AdvisorySeverity,
    val timestamp: String,
    val sourceBn: String = "শান্তিপুর ট্রাফিক পুলিশ ও পৌরসভা",
    val sourceEn: String = "Santipur Traffic Police & Municipality"
)

enum class AdvisorySeverity {
    INFO,
    WARNING,
    RESTRICTION
}

data class CrowdReport(
    val id: String,
    val placeId: String,
    val placeNameBn: String,
    val placeNameEn: String,
    val status: CrowdStatus,
    val waitTimeMinutes: Int,
    val submittedBy: String,
    val timestampFormatted: String,
    val notesBn: String,
    val isOfficialVerified: Boolean = false
)

data class RoutePlan(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val subtitleBn: String,
    val subtitleEn: String,
    val estimatedMinutes: Int,
    val totalDistanceKm: Double,
    val suitabilityTagBn: String,
    val suitabilityTagEn: String,
    val placeIds: List<String>,
    val isCustom: Boolean = false
)

data class Article(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val authorBn: String,
    val authorEn: String,
    val readTimeMinutes: Int,
    val summaryBn: String,
    val summaryEn: String,
    val contentBn: String,
    val contentEn: String,
    val publishedDate: String
)

data class UserCorrection(
    val id: String,
    val placeName: String,
    val userEmailOrPhone: String,
    val correctionType: String,
    val message: String,
    val submittedAt: String,
    var status: String = "বিবেচনাধীন (Pending Review)"
)
