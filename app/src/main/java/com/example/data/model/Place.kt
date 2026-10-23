package com.example.data.model

enum class PlaceCategory(
    val titleBn: String,
    val titleEn: String,
    val iconName: String
) {
    RASH_YATRA("রাসযাত্রা", "Rash Yatra", "temple_hindu"),
    KALI_PUJA("কালীপুজো", "Kali Puja", "local_fire_department"),
    GOPAL_PUJA("গোপালপুজো", "Gopal Puja", "child_care"),
    JAGADHATRI_PUJA("জগদ্ধাত্রীপুজো", "Jagadhatri Puja", "festival"),
    DURGA_PUJA("দুর্গাপুজো", "Durga Puja", "celebration"),
    RATH_YATRA("রথযাত্রা", "Rath Yatra", "directions_car"),
    HERITAGE_TEMPLE("ঐতিহ্যবাহী মন্দির", "Heritage Temple", "account_balance"),
    FAMILY_BAROWARI("বারোয়ারি ও পারিবারিক", "Barowari & Family Puja", "groups"),
    TRANSPORT("পরিবহন কেন্দ্র", "Transport Hub", "directions_bus"),
    FACILITY("জরুরি ও জনসুবিধা", "Public Facility & Emergency", "local_hospital")
}

data class Place(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val category: PlaceCategory,
    val locality: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val descriptionBn: String,
    val descriptionEn: String,
    val deityBn: String,
    val deityEn: String,
    val establishedYear: String,
    val darshanTimings: String,
    val isVerified: Boolean = true,
    val lastVerifiedDate: String = "অক্টোবর ২০২৬",
    val accessibilityNotesBn: String = "হাঁটার উপযোগী মসৃণ পথ ও প্রবেশদ্বার",
    val accessibilityNotesEn: String = "Smooth walking path and accessible entrance",
    val nearestParkingBn: String = "নিকটস্থ সুত্রাগড় মাঠ পার্কিং (৩০০ মি)",
    val nearestParkingEn: String = "Near Sutragarh Ground Parking (300m)",
    val nearestTransportBn: String = "শান্তিপুর রেলওয়ে স্টেশন থেকে টোটোযোগে ১০ মিনিট",
    val nearestTransportEn: String = "10 mins by Toto (E-rickshaw) from Santipur Railway Station",
    val isFamousHighlight: Boolean = false,
    val isRashParticipant: Boolean = false,
    val isKaliSpecial: Boolean = false,
    val crowdStatus: CrowdStatus = CrowdStatus.MODERATE
)

enum class CrowdStatus(val labelBn: String, val labelEn: String) {
    LOW("কম ভিড় (শান্ত)", "Low (Peaceful)"),
    MODERATE("মাঝারি ভিড়", "Moderate"),
    HIGH("অত্যধিক ভিড়", "High (Peak Crowd)"),
    UNKNOWN("তথ্য সংগ্রহ চলছে", "Unknown")
}
