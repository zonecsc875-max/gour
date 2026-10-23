package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*

object SantipurRepository {

    // Default Santipur Central Geopoint (23.2505° N, 88.4344° E)
    const val SANTIPUR_LAT = 23.2505
    const val SANTIPUR_LNG = 88.4344

    private val _savedPlaceIds = MutableStateFlow<Set<String>>(setOf("boro_goswami", "agameswari"))
    val savedPlaceIds: StateFlow<Set<String>> = _savedPlaceIds.asStateFlow()

    private val _crowdReports = MutableStateFlow<List<CrowdReport>>(initialCrowdReports())
    val crowdReports: StateFlow<List<CrowdReport>> = _crowdReports.asStateFlow()

    private val _advisories = MutableStateFlow<List<Advisory>>(initialAdvisories())
    val advisories: StateFlow<List<Advisory>> = _advisories.asStateFlow()

    private val _userCorrections = MutableStateFlow<List<UserCorrection>>(initialCorrections())
    val userCorrections: StateFlow<List<UserCorrection>> = _userCorrections.asStateFlow()

    private val _customItineraryPlaceIds = MutableStateFlow<List<String>>(
        listOf("boro_goswami", "shyamchand", "agameswari")
    )
    val customItineraryPlaceIds: StateFlow<List<String>> = _customItineraryPlaceIds.asStateFlow()

    fun toggleSavePlace(placeId: String) {
        val current = _savedPlaceIds.value.toMutableSet()
        if (current.contains(placeId)) {
            current.remove(placeId)
        } else {
            current.add(placeId)
        }
        _savedPlaceIds.value = current
    }

    fun isPlaceSaved(placeId: String): Boolean {
        return _savedPlaceIds.value.contains(placeId)
    }

    fun submitCrowdReport(placeId: String, status: CrowdStatus, waitMinutes: Int, notes: String, author: String) {
        val place = getAllPlaces().find { it.id == placeId }
        val newReport = CrowdReport(
            id = "rep_${System.currentTimeMillis()}",
            placeId = placeId,
            placeNameBn = place?.nameBn ?: "শান্তিপুর উৎসব স্থান",
            placeNameEn = place?.nameEn ?: "Santipur Festival Spot",
            status = status,
            waitTimeMinutes = waitMinutes,
            submittedBy = if (author.isBlank()) "দর্শনার্থী (Visitor)" else author,
            timestampFormatted = "এইমাত্র (Just now)",
            notesBn = notes,
            isOfficialVerified = false
        )
        _crowdReports.value = listOf(newReport) + _crowdReports.value
    }

    fun submitUserCorrection(placeName: String, contact: String, type: String, message: String) {
        val correction = UserCorrection(
            id = "corr_${System.currentTimeMillis()}",
            placeName = placeName,
            userEmailOrPhone = contact,
            correctionType = type,
            message = message,
            submittedAt = "২৩ অক্টোবর ২০২৬, সকাল ১০:১৫",
            status = "বিবেচনাধীন (Pending Review)"
        )
        _userCorrections.value = listOf(correction) + _userCorrections.value
    }

    fun adminApproveCorrection(id: String) {
        _userCorrections.value = _userCorrections.value.map {
            if (it.id == id) it.copy(status = "অনুমোদিত ও হালনাগাদ (Approved)") else it
        }
    }

    fun adminPublishAdvisory(titleBn: String, titleEn: String, messageBn: String, messageEn: String, severity: AdvisorySeverity) {
        val newAdvisory = Advisory(
            id = "adv_${System.currentTimeMillis()}",
            titleBn = titleBn,
            titleEn = titleEn,
            messageBn = messageBn,
            messageEn = messageEn,
            severity = severity,
            timestamp = "এইমাত্র (Just now)"
        )
        _advisories.value = listOf(newAdvisory) + _advisories.value
    }

    fun addToCustomItinerary(placeId: String) {
        if (!_customItineraryPlaceIds.value.contains(placeId)) {
            _customItineraryPlaceIds.value = _customItineraryPlaceIds.value + placeId
        }
    }

    fun removeFromCustomItinerary(placeId: String) {
        _customItineraryPlaceIds.value = _customItineraryPlaceIds.value - placeId
    }

    fun reorderCustomItinerary(fromIndex: Int, toIndex: Int) {
        val list = _customItineraryPlaceIds.value.toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices) {
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            _customItineraryPlaceIds.value = list
        }
    }

    // Distance calculation using Haversine formula
    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (r * c * 10.0).roundToInt() / 10.0
    }

    fun getAllPlaces(): List<Place> = listOf(
        Place(
            id = "boro_goswami",
            nameBn = "বড় গোস্বামী বাড়ি",
            nameEn = "Boro Goswami Bari",
            category = PlaceCategory.RASH_YATRA,
            locality = "গোস্বামী পাড়া (Goswami Para)",
            address = "গোস্বামী পাড়া রোড, শান্তিপুর, নদীয়া - ৭৪১৪০৪",
            latitude = 23.2482,
            longitude = 88.4320,
            descriptionBn = "শ্রী অদ্বৈত মহাপ্রভুর পবিত্র বংশপরম্পরা বাহিত প্রায় ৫০০ বছরের প্রাচীন বৈষ্ণব কেন্দ্র। রাস পূর্ণিমার দিন রাধারমণ জিউ-এর মহা রাসোৎসব ও ঐতিহাসিক ভাঙা রাসের ঐতিহ্যবাহী সুসজ্জিত রথ পরিক্রমা এখানে আয়োজিত হয়।",
            descriptionEn = "Historic 500+ year old Vaishnava shrine of Sri Advaita Acharya's lineage. Famous for Radharaman Jiu's Rasotsav, historic wooden rath, and the grand Bhanga Rash procession.",
            deityBn = "শ্রী শ্রী রাধারমণ জিউ",
            deityEn = "Sri Sri Radharaman Jiu",
            establishedYear = "১৪৮৪ খ্রিষ্টাব্দ (১৫শ শতাব্দী)",
            darshanTimings = "সকাল ৬:০০ - দুপুর ১২:৩০ এবং বিকেল ৪:৩০ - রাত ১০:০০",
            isVerified = true,
            lastVerifiedDate = "অক্টোবর ২০২৬",
            accessibilityNotesBn = "মন্দির প্রাঙ্গণ প্রশস্ত, সমতল প্রবেশপথ এবং বয়স্কদের বসার সুব্যবস্থা রয়েছে।",
            accessibilityNotesEn = "Spacious courtyard with level entrance and seating for elderly visitors.",
            nearestParkingBn = "পৌরসভা স্কুল মাঠ ও সুত্রাগড় পার্কিং (৪০০ মিটার)",
            nearestParkingEn = "Municipality School Ground & Sutragarh Parking (400m)",
            nearestTransportBn = "শান্তিপুর স্টেশন থেকে টোটো বা রিকশায় ৭-১০ মিনিট",
            nearestTransportEn = "7-10 mins by Toto/Rickshaw from Santipur Railway Station",
            isFamousHighlight = true,
            isRashParticipant = true,
            isKaliSpecial = false,
            crowdStatus = CrowdStatus.HIGH
        ),
        Place(
            id = "chhoto_goswami",
            nameBn = "ছোট গোস্বামী বাড়ি",
            nameEn = "Chhoto Goswami Bari",
            category = PlaceCategory.RASH_YATRA,
            locality = "গোস্বামী পাড়া (Goswami Para)",
            address = "পশ্চিম গোস্বামী পাড়া, শান্তিপুর, নদীয়া - ৭৪১৪০৪",
            latitude = 23.2491,
            longitude = 88.4335,
            descriptionBn = "শ্রী শ্রী রাধা মদনগোপাল জিউ-এর ঐতিহাসিক সেবাধাম। অনন্য রূপার সাজ, সুপ্রাচীন গয়না এবং রাসযাত্রার সময় অপূর্ব যুগল দর্শন শান্তিপুরের অন্যতম প্রধান ভক্তি আকর্ষণ।",
            descriptionEn = "Sacred seat of Sri Sri Radha Madangopal Jiu. Celebrated for ornate silver adornments, historic jewel decorations, and divine Rasotsav darshan.",
            deityBn = "শ্রী শ্রী রাধা মদনগোপাল জিউ",
            deityEn = "Sri Sri Radha Madangopal Jiu",
            establishedYear = "১৬শ শতাব্দী",
            darshanTimings = "সকাল ৬:৩০ - দুপুর ১:০০ এবং বিকেল ৫:০০ - রাত ৯:৪৫",
            isVerified = true,
            lastVerifiedDate = "অক্টোবর ২০২৬",
            accessibilityNotesBn = "গলিপথ হওয়ায় পায়ে হেঁটে বা টোটোয় পৌঁছানো সুবিধাজনক।",
            accessibilityNotesEn = "Approached via heritage lane, easily reached by walking or Toto.",
            nearestParkingBn = "শান্তিপুর পাবলিক লাইব্রেরি মাঠ (৫৫০ মিটার)",
            nearestParkingEn = "Santipur Public Library Ground (550m)",
            nearestTransportBn = "স্টেশন রোড থেকে টোটোযোগে সরাসরি",
            nearestTransportEn = "Direct Toto from Station Road",
            isFamousHighlight = true,
            isRashParticipant = true,
            isKaliSpecial = false,
            crowdStatus = CrowdStatus.HIGH
        ),
        Place(
            id = "shyamchand",
            nameBn = "শ্যামচাঁদ মন্দির",
            nameEn = "Shyamchand Mandir",
            category = PlaceCategory.HERITAGE_TEMPLE,
            locality = "সুত্রাগড় বাজার এলাকা",
            address = "নেতাজি সুভাষ রোড, শান্তিপুর, নদীয়া - ৭৪১৪০৪",
            latitude = 23.2514,
            longitude = 88.4361,
            descriptionBn = "১৭২৬ খ্রিষ্টাব্দে স্থানীয় তাঁতি ও ভক্তদের সহযোগিতায় নির্মিত বাংলার সর্ববৃহৎ আটচালা টেরাকোটা স্থাপত্যের নিদর্শন। উচ্চতা প্রায় ৬০ ফুট। রাসের সময় এখানে হাজার হাজার ভক্তের সমাগম হয়।",
            descriptionEn = "Built in 1726 AD, one of Bengal's grandest and tallest Aat-Chala terracotta temples (approx 60ft high). A masterpiece of Bengal temple architecture.",
            deityBn = "শ্রী শ্রী শ্যামচাঁদ জিউ",
            deityEn = "Sri Sri Shyamchand Jiu",
            establishedYear = "১৭২৬ খ্রিষ্টাব্দ",
            darshanTimings = "সকাল ৭:০০ - দুপুর ১২:০০ এবং বিকেল ৪:০০ - রাত ৯:৩০",
            isVerified = true,
            lastVerifiedDate = "অক্টোবর ২০২৬",
            accessibilityNotesBn = "মূল গেট প্রশস্ত, সিঁড়িতে রেলিং সহ আধুনিক প্রবেশ সুবিধা।",
            accessibilityNotesEn = "Wide entrance gate with handrails on temple platform stairs.",
            nearestParkingBn = "সুত্রাগড় সার্বজনীন মাঠ পার্কিং জোন (২৫০ মিটার)",
            nearestParkingEn = "Sutragarh Sarbajanin Ground Parking Zone (250m)",
            nearestTransportBn = "শান্তিপুর বাসস্ট্যান্ড থেকে ৫ মিনিট পদযাত্রা",
            nearestTransportEn = "5 mins walk from Santipur Bus Stand",
            isFamousHighlight = true,
            isRashParticipant = true,
            isKaliSpecial = false,
            crowdStatus = CrowdStatus.MODERATE
        ),
        Place(
            id = "agameswari",
            nameBn = "আগমেশ্বরী মন্দির",
            nameEn = "Agameswari Mandir",
            category = PlaceCategory.KALI_PUJA,
            locality = "আগমেশ্বরী তলা, সুত্রাগড়",
            address = "আগমেশ্বরী রোড, সুত্রাগড়, শান্তিপুর, নদীয়া - ৭৪১৪০৪",
            latitude = 23.2530,
            longitude = 88.4385,
            descriptionBn = "শান্তিপুরের মহাশক্তিপীঠ সমতুল্য জাগ্রত শাক্ত সাধনা ক্ষেত্র। তন্ত্রাচার্য সর্বানন্দ / কৃষ্ণানন্দ আগমবাগীশের পরম্পরায় প্রতিষ্ঠিত দেবী আগমেশ্বরী। দীপান্বিতা অমাবস্যার গভীর রাতে অনুষ্ঠিত হয় বিশেষ তান্ত্রিক পূজা।",
            descriptionEn = "Legendary Tantric Shakti shrine of Santipur tracing to the lineage of saint Agambagish. Renowned for its monumental Kali vigraha and midnight Diwali puja.",
            deityBn = "মা আগমেশ্বরী (মহাকালী)",
            deityEn = "Maa Agameswari (Maha Kali)",
            establishedYear = "১৬শ শতাব্দীর প্রারম্ভ",
            darshanTimings = "সকাল ৭:০০ - রাত ১১:০০ (কালীপুজোর রাতে সারারাত উন্মুক্ত)",
            isVerified = true,
            lastVerifiedDate = "অক্টোবর ২০২৬",
            accessibilityNotesBn = "মন্দিরের সামনে উন্মুক্ত পাকা চত্বর, সহজে চলাচলের পথ।",
            accessibilityNotesEn = "Paved open frontage with smooth walking paths.",
            nearestParkingBn = "সুত্রাগড় বয়েজ হাই স্কুল মাঠ পার্কিং (৩৫০ মিটার)",
            nearestParkingEn = "Sutragarh Boys High School Parking (350m)",
            nearestTransportBn = "স্টেশন ও বাসস্ট্যান্ড উভয়ের সংযোগস্থলে অবস্থিত",
            nearestTransportEn = "Centrally situated between station and bus terminus",
            isFamousHighlight = true,
            isRashParticipant = false,
            isKaliSpecial = true,
            crowdStatus = CrowdStatus.HIGH
        ),
        Place(
            id = "bamakali",
            nameBn = "বামাকালী মন্দির",
            nameEn = "Bamakali Temple",
            category = PlaceCategory.KALI_PUJA,
            locality = "বামাকালী তলা",
            address = "বামাকালী লেন, শান্তিপুর, নদীয়া - ৭৪১৪০৪",
            latitude = 23.2555,
            longitude = 88.4398,
            descriptionBn = "শান্তিপুরের অন্যতম ঐতিহ্যমণ্ডিত দক্ষিণাকালী মন্দির। তান্ত্রিক বামাচরণের নামাঙ্কিত দেবী বামাকালী বিশেষ জাগ্রত দেবী হিসেবে পরম শ্রদ্ধেয়। দীপাবলীতে রক্তজবা ও প্রদীপমালায় সাজানো হয় মন্দির।",
            descriptionEn = "Historic Dakshina Kali temple revered for centuries. Renowned for strict ritual adherence, vibrant earthen lamp illumination, and deep devotional fervor.",
            deityBn = "মা বামাকালী",
            deityEn = "Maa Bamakali",
            establishedYear = "১৭শ শতাব্দী",
            darshanTimings = "সকাল ৬:০০ - দুপুর ১২:০০ এবং বিকেল ৫:০০ - রাত ১০:০০",
            isVerified = true,
            lastVerifiedDate = "অক্টোবর ২০২৬",
            accessibilityNotesBn = "শান্ত ও ছায়াঘেরা মন্দির চত্বর, হুইলচেয়ার সহজে প্রবেশ করতে পারে।",
            accessibilityNotesEn = "Tranquil courtyard, wheelchair accessible ground floor sanctum.",
            nearestParkingBn = "মডেল স্কুল সংলগ্ন পার্কিং (৩০০ মিটার)",
            nearestParkingEn = "Near Model School Parking (300m)",
            nearestTransportBn = "টোটো বা অটোতে ৫ মিনিট",
            nearestTransportEn = "5 mins by Toto or Auto",
            isFamousHighlight = true,
            isRashParticipant = false,
            isKaliSpecial = true,
            crowdStatus = CrowdStatus.MODERATE
        ),
        Place(
            id = "mejo_gopal",
            nameBn = "মেজো গোপাল মন্দির",
            nameEn = "Mejo Gopal Temple",
            category = PlaceCategory.GOPAL_PUJA,
            locality = "মধ্য শান্তিপুর",
            address = "গোপাল তলা রোড, শান্তিপুর, নদীয়া - ৭৪১৪০৪",
            latitude = 23.2498,
            longitude = 88.4340,
            descriptionBn = "শ্রীকৃষ্ণের বাল্যরূপ 'মেজো গোপাল'-এর অনুপম সেবা ও পূজার কেন্দ্র। অন্নকূট উৎসব এবং গোপাল পুজোয় এখানে বর্ণাঢ্য ভোগারতি ও সংকীর্তন অনুষ্ঠিত হয়।",
            descriptionEn = "Dedicated to child Krishna 'Mejo Gopal'. Famed for Annakut mahotsav, traditional Gopal Puja celebrations, and musical Sankirtan.",
            deityBn = "শ্রী শ্রী মেজো গোপাল জিউ",
            deityEn = "Sri Sri Mejo Gopal Jiu",
            establishedYear = "১৭শ শতাব্দী",
            darshanTimings = "সকাল ৭:০০ - দুপুর ১২:০০ এবং বিকেল ৪:৩০ - রাত ৯:০০",
            isVerified = true,
            lastVerifiedDate = "অক্টোবর ২০২৬",
            accessibilityNotesBn = "মসৃণ মার্বেল চত্বর ও রোদবৃষ্টির আচ্ছাদন রয়েছে।",
            accessibilityNotesEn = "Smooth marble pavilion with covered veranda.",
            nearestParkingBn = "শান্তিপুর পোস্ট অফিস সংলগ্ন পার্কিং",
            nearestParkingEn = "Near Santipur Head Post Office Parking",
            nearestTransportBn = "টোটোযোগে সহজেই যাতায়াতযোগ্য",
            nearestTransportEn = "Accessible easily by local Toto",
            isFamousHighlight = false,
            isRashParticipant = true,
            isKaliSpecial = false,
            crowdStatus = CrowdStatus.LOW
        ),
        Place(
            id = "poteswari",
            nameBn = "পটেশ্বরী মন্দির",
            nameEn = "Poteswari Mandir",
            category = PlaceCategory.HERITAGE_TEMPLE,
            locality = "পটেশ্বরী তলা",
            address = "পটেশ্বরী লেন, শান্তিপুর, নদীয়া - ৭৪১৪০৪",
            latitude = 23.2465,
            longitude = 88.4310,
            descriptionBn = "বাংলার বিরল ও সুপ্রাচীন পটচিত্র রূপের দেবী আরাধনা কেন্দ্র। কাপড়ের পটে অঙ্কিত দেবীমূর্তি বিশেষ শ্রদ্ধার সাথে পুজো করা হয়, যা শান্তিপুরের লোকশিল্পের আদি নিদর্শন।",
            descriptionEn = "Rare and ancient Bengal shrine featuring worship of deity painted on traditional sacred scroll/cloth (Pot), showcasing the folk art heritage of Bengal.",
            deityBn = "দেবী পটেশ্বরী",
            deityEn = "Devi Poteswari",
            establishedYear = "আনুমানিক ১৫শ শতাব্দী",
            darshanTimings = "সকাল ৭:৩০ - দুপুর ১১:৩০ এবং বিকেল ৫:০০ - রাত ৮:৩০",
            isVerified = true,
            lastVerifiedDate = "অক্টোবর ২০২৬",
            accessibilityNotesBn = "ঐতিহ্যবাহী প্রাচীন ভবন, ধীরেসুস্থে হাঁটার পরামর্শ।",
            accessibilityNotesEn = "Heritage precinct, gentle walking advised.",
            nearestParkingBn = "গঙ্গার ঘাট সংলগ্ন ফাঁকা মাঠ (৬০০ মিটার)",
            nearestParkingEn = "Near River Ghat open ground (600m)",
            nearestTransportBn = "রিকশায় ৭ মিনিট",
            nearestTransportEn = "7 mins by cycle-rickshaw",
            isFamousHighlight = false,
            isRashParticipant = false,
            isKaliSpecial = false,
            crowdStatus = CrowdStatus.LOW
        ),
        Place(
            id = "advaita_babla",
            nameBn = "শ্রী শ্রী অদ্বৈত পাঠ ও বাবলা আশ্রম",
            nameEn = "Advaita Paath & Babla Ashram",
            category = PlaceCategory.RASH_YATRA,
            locality = "বাবলা (Babla)",
            address = "বাবলা মোড়, শান্তিপুর, নদীয়া - ৭৪১৪০৪",
            latitude = 23.2380,
            longitude = 88.4250,
            descriptionBn = "শ্রী চৈতন্য মহাপ্রভুর সন্ন্যাস গ্রহণের পর শচীমাতার সাথে ঐতিহাসিক পুনর্মিলন স্থান। পরম বৈষ্ণব তীর্থ এবং শান্তিপুর রাসযাত্রার অন্যতম আধ্যাত্মিক সূতিকাগার।",
            descriptionEn = "The holy site where Sri Chaitanya Mahaprabhu met Mother Sachi Devi after taking sannyasa. A revered Vaishnava pilgrimage and spiritual core of Santipur.",
            deityBn = "শ্রী অদ্বৈত আচার্য ও শ্রী চৈতন্য মহাপ্রভু",
            deityEn = "Sri Advaita Acharya & Sri Chaitanya Mahaprabhu",
            establishedYear = "১৫শ শতাব্দী",
            darshanTimings = "সকাল ৬:০০ - দুপুর ১২:০০ এবং বিকেল ৩:৩০ - সন্ধ্যা ৮:৩০",
            isVerified = true,
            lastVerifiedDate = "অক্টোবর ২০২৬",
            accessibilityNotesBn = "সবুজ বাগান পরিবেষ্টিত সমতল পথ, বয়স্ক ও শিশুদের জন্য অত্যন্ত মনোরম।",
            accessibilityNotesEn = "Garden-lined serene flat pathways, ideal for families and seniors.",
            nearestParkingBn = "আশ্রম প্রাঙ্গণে নিজস্ব যানবাহন রাখার প্রশস্ত স্থান রয়েছে।",
            nearestParkingEn = "Ample designated parking inside ashram complex.",
            nearestTransportBn = "শান্তিপুর রেলওয়ে স্টেশন থেকে অটো বা টোটোয় ১৫ মিনিট",
            nearestTransportEn = "15 mins by Toto or Auto from Santipur Railway Station",
            isFamousHighlight = true,
            isRashParticipant = true,
            isKaliSpecial = false,
            crowdStatus = CrowdStatus.MODERATE
        ),
        Place(
            id = "sutragarh_sarbajanin",
            nameBn = "সুত্রাগড় সার্বজনীন রাস উৎসব সমিতি",
            nameEn = "Sutragarh Sarbajanin Rash Committee",
            category = PlaceCategory.FAMILY_BAROWARI,
            locality = "সুত্রাগড় (Sutragarh)",
            address = "সুত্রাগড় মেইন রোড, শান্তিপুর, নদীয়া - ৭৪১৪০৪",
            latitude = 23.2560,
            longitude = 88.4410,
            descriptionBn = "শান্তিপুরের বৃহত্তম বারোয়ারি রাস প্যান্ডেল ও আলোকসজ্জা। নজরকাড়া তোরণ, সাংস্কৃতিক মঞ্চ এবং সুশৃঙ্খল ভক্ত পরিক্রমার জন্য বিখ্যাত।",
            descriptionEn = "Largest community Barowari Rash pandal in Santipur, noted for breathtaking decorative illumination, cultural proscenium, and volunteer-guided lines.",
            deityBn = "শ্রী শ্রী রাধাকৃষ্ণ যুগল",
            deityEn = "Sri Sri Radha Krishna Yugal",
            establishedYear = "১৯৫০ খ্রিষ্টাব্দ",
            darshanTimings = "সকাল ৯:০০ - মধ্যরাত ১:০০ (উৎসবের দিন সারারাত)",
            isVerified = true,
            lastVerifiedDate = "অক্টোবর ২০২৬",
            accessibilityNotesBn = "পৃথক প্রবেশ ও প্রস্থান লেন, মেডিকেল ক্যাম্প ও পানীয় জলের কিয়স্ক সংযুক্ত।",
            accessibilityNotesEn = "Separate entry & exit queues, integrated medical and drinking water booth.",
            nearestParkingBn = "সুত্রাগড় ফুটবল মাঠ অফিসিয়াল পার্কিং (১৫০ মিটার)",
            nearestParkingEn = "Sutragarh Football Ground Official Parking (150m)",
            nearestTransportBn = "প্রধান রাস্তার ওপর অবস্থিত, যেকোনো যানবাহনে পৌঁছানো যায়",
            nearestTransportEn = "Located on main road, easily reachable by public transit",
            isFamousHighlight = true,
            isRashParticipant = true,
            isKaliSpecial = false,
            crowdStatus = CrowdStatus.HIGH
        ),
        Place(
            id = "dakatkali",
            nameBn = "ঐতিহাসিক ডাকাতকালী মন্দির",
            nameEn = "Historic Dakatkali Mandir",
            category = PlaceCategory.KALI_PUJA,
            locality = "শান্তিপুর সীমান্তবর্তী ঘাট লেন",
            address = "ঘাট রোড, শান্তিপুর, নদীয়া - ৭৪১৪০৪",
            latitude = 23.2580,
            longitude = 88.4320,
            descriptionBn = "প্রাচীন লোকশ্রুতি অনুযায়ী জলপথে বণিক ও যাত্রীদের নিরাপত্তার জন্য প্রতিষ্ঠিত শতাব্দী প্রাচীন কালীমন্দির। দীপান্বিতা কালীপুজোয় এখানে ঐতিহ্যবাহী পাঁঠাবলি ও মোমবাতির আলোয় আরতি হয়।",
            descriptionEn = "Centuries-old folk Kali temple steeped in river-lore, worshipped for divine protection. Renowned for solemn traditional night ceremonies.",
            deityBn = "মা ডাকাতকালী",
            deityEn = "Maa Dakatkali",
            establishedYear = "১৮শ শতাব্দী",
            darshanTimings = "সকাল ৭:০০ - দুপুর ১:০০ এবং বিকেল ৫:০০ - রাত ৯:০০",
            isVerified = true,
            lastVerifiedDate = "অক্টোবর ২০২৬",
            accessibilityNotesBn = "নদীর মৃদু হাওয়া ও গাছপালার ছায়ায় সুশীতল প্রবেশপথ।",
            accessibilityNotesEn = "Shaded riverbank breeze and level walkway.",
            nearestParkingBn = "ঘাটের প্রবেশপথে পার্কিং",
            nearestParkingEn = "Ghat entrance parking bay",
            nearestTransportBn = "টোটোয় ৮ মিনিট",
            nearestTransportEn = "8 mins by Toto",
            isFamousHighlight = false,
            isRashParticipant = false,
            isKaliSpecial = true,
            crowdStatus = CrowdStatus.LOW
        ),
        // Transport & Public Facilities (with verified coordinates)
        Place(
            id = "santipur_station",
            nameBn = "শান্তিপুর রেলওয়ে স্টেশন (পূর্ব রেল)",
            nameEn = "Santipur Railway Station (Eastern Railway)",
            category = PlaceCategory.TRANSPORT,
            locality = "স্টেশন পাড়া",
            address = "স্টেশন রোড, শান্তিপুর, নদীয়া - ৭৪১৪০৪",
            latitude = 23.2420,
            longitude = 88.4480,
            descriptionBn = "শিয়ালদহ - শান্তিপুর লোকাল ট্রেনের প্রধান টার্মিনাস। ২৪ ঘণ্টা টোটো ও রিকশা স্ট্যান্ড, পুলিশ হেল্পডেস্ক, মেলা টিকিট কাউন্টার এবং পানীয় জল কেন্দ্র বিদ্যমান।",
            descriptionEn = "Primary EMU railway terminus connecting Sealdah & Ranaghat to Santipur. Features 24/7 Toto & rickshaw bay, police help booth, and water points.",
            deityBn = "জনপরিষেবা কেন্দ্র",
            deityEn = "Public Transit Centre",
            establishedYear = "১৮৬২ খ্রিষ্টাব্দ",
            darshanTimings = "২৪ ঘণ্টা খোলা (24 Hours Open)",
            isVerified = true,
            lastVerifiedDate = "অক্টোবর ২০২৬",
            accessibilityNotesBn = "হুইলচেয়ার র্যাম্প, প্ল্যাটফর্ম ওভারব্রিজ লিফট ও রিজার্ভ পার্কিং সুবিধা।",
            accessibilityNotesEn = "Wheelchair ramps, overbridge facilities, reserved mobility bays.",
            nearestParkingBn = "স্টেশন গ্রাউন্ড টু-হুইলার ও ফোর-হুইলার পার্কিং",
            nearestParkingEn = "Station Ground 2-wheeler and 4-wheeler parking",
            nearestTransportBn = "লোকাল ট্রেন, বাস ও ট্যাক্সি সংযোগস্থল",
            nearestTransportEn = "Rail terminus with immediate connecting transit",
            isFamousHighlight = false,
            isRashParticipant = false,
            isKaliSpecial = false,
            crowdStatus = CrowdStatus.HIGH
        ),
        Place(
            id = "santipur_hospital",
            nameBn = "শান্তিপুর স্টেট জেনারেল হাসপাতাল (জরুরি বিভাগ)",
            nameEn = "Santipur State General Hospital (Emergency)",
            category = PlaceCategory.FACILITY,
            locality = "হাসপাতাল মোড়",
            address = "হাসপাতাল রোড, শান্তিপুর, নদীয়া - ৭৪১৪০৪",
            latitude = 23.2470,
            longitude = 88.4390,
            descriptionBn = "শান্তিপুরের প্রধান সরকারি চিকিৎসাকেন্দ্র। ২৪ ঘণ্টার ট্রমা কেয়ার, জরুরি অ্যাম্বুলেন্স সার্ভিস (১০২), রক্ত ভাণ্ডার এবং উৎসবকালীন অতিরিক্ত চিকিৎসক দল প্রস্তুত থাকে।",
            descriptionEn = "Primary 24/7 government healthcare centre with round-the-clock emergency casualty, ambulance fleet, blood bank, and dedicated festival medical teams.",
            deityBn = "চিকিৎসা ও স্বাস্থ্য কেন্দ্র",
            deityEn = "Medical & Emergency Centre",
            establishedYear = "১৯৫৪ খ্রিষ্টাব্দ",
            darshanTimings = "২৪ ঘণ্টা জরুরি পরিষেবা (24x7 Emergency)",
            isVerified = true,
            lastVerifiedDate = "অক্টোবর ২০২৬",
            accessibilityNotesBn = "সম্পূর্ণ হুইলচেয়ার বান্ধব, জরুরি অ্যাম্বুলেন্স ড্রাইভ-ইন পথ।",
            accessibilityNotesEn = "Full wheelchair accessibility with priority emergency ambulance drive-in.",
            nearestParkingBn = "হাসপাতাল অভ্যন্তরীণ পার্কিং ও ভিজিটর লেন",
            nearestParkingEn = "Hospital internal parking and visitor lane",
            nearestTransportBn = "প্রধান রাস্তা থেকে সরাসরি প্রবেশযোগ্য",
            nearestTransportEn = "Direct access from main arterial road",
            isFamousHighlight = false,
            isRashParticipant = false,
            isKaliSpecial = false,
            crowdStatus = CrowdStatus.MODERATE
        ),
        Place(
            id = "topkhana_mela_ground",
            nameBn = "তোপখানা মোড় ও রাসমেলা প্রাঙ্গণ",
            nameEn = "Topkhana More & Rash Mela Ground",
            category = PlaceCategory.FAMILY_BAROWARI,
            locality = "তোপখানা মোড়",
            address = "তোপখানা, শান্তিপুর, নদীয়া - ৭৪১৪০৪",
            latitude = 23.2520,
            longitude = 88.4350,
            descriptionBn = "ঐতিহাসিক রাস উৎসবের প্রধান গ্রাউন্ড। শত শত হস্তশিল্পের দোকান, শান্তিপুরী তাঁতের প্রদর্শনী, ঐতিহ্যবাহী মিষ্টি (নিখিল সন্দেশ) এবং লোকনৃত্য মঞ্চের কেন্দ্র।",
            descriptionEn = "Heart of the annual Santipur Rash Mela. Hundreds of Bengal handicraft stalls, famous handloom saree pavilions, traditional confections, and open-air folk stages.",
            deityBn = "সাংস্কৃতিক উৎসব প্রাঙ্গণ",
            deityEn = "Cultural Fair Grounds",
            establishedYear = "ঐতিহাসিক",
            darshanTimings = "দুপুর ২:০০ - রাত ১২:০০ (মেলার দিনগুলিতে)",
            isVerified = true,
            lastVerifiedDate = "অক্টোবর ২০২৬",
            accessibilityNotesBn = "প্রশস্ত মেলা প্রাঙ্গণ, স্বেচ্ছাসেবী ও দিকনির্দেশক সাইনবোর্ড সংবলিত।",
            accessibilityNotesEn = "Spacious fair grounds with volunteers and multilingual signboards.",
            nearestParkingBn = "শান্তিপুর কলেজ গ্রাউন্ড পার্কিং (৪০০ মিটার)",
            nearestParkingEn = "Santipur College Ground Parking (400m)",
            nearestTransportBn = "টোটো বা পায়ে হেঁটে ৫ মিনিট",
            nearestTransportEn = "5 mins walk or Toto from central junctions",
            isFamousHighlight = true,
            isRashParticipant = true,
            isKaliSpecial = false,
            crowdStatus = CrowdStatus.HIGH
        ),
        Place(
            id = "sutragarh_parking_facility",
            nameBn = "সুত্রাগড় হাই স্কুল কেন্দ্রীয় পার্কিং ও বায়ো-টয়লেট",
            nameEn = "Sutragarh High School Central Parking & Toilets",
            category = PlaceCategory.FACILITY,
            locality = "সুত্রাগড়",
            address = "সুত্রাগড় হাই স্কুল প্রাঙ্গণ, শান্তিপুর, নদীয়া - ৭৪১৪০৪",
            latitude = 23.2545,
            longitude = 88.4425,
            descriptionBn = "পৌরসভা ও পুলিশ নির্দেশিত মূল যানবাহন পার্কিং লট (কার ও বাইক)। বিনামূল্যে বিশুদ্ধ পানীয় জল, পুরুষ ও মহিলাদের জন্য পৃথক বায়ো-টয়লেট ও ফার্স্ট এইড বুথ রয়েছে।",
            descriptionEn = "Designated municipal and police festival parking area for cars and 2-wheelers. Features free filtered drinking water kiosks, clean bio-toilets, and first-aid post.",
            deityBn = "নাগরিক জনসুবিধা ও পার্কিং",
            deityEn = "Civic Facility & Parking Bay",
            establishedYear = "পৌর উৎসব অবকাঠামো",
            darshanTimings = "সকাল ৬:০০ - রাত ২:০০",
            isVerified = true,
            lastVerifiedDate = "অক্টোবর ২০২৬",
            accessibilityNotesBn = "সমান পিচঢালা ও ঘাসের মাঠ, পর্যাপ্ত এলইডি লাইটযুক্ত।",
            accessibilityNotesEn = "Even surface with high-mast LED floodlights.",
            nearestParkingBn = "এখানেই মূল পার্কিং (Dedicated Facility)",
            nearestParkingEn = "On-site designated parking hub",
            nearestTransportBn = "এখান থেকেই শুরু হয় পদব্রজে মন্দির পরিক্রমা",
            nearestTransportEn = "Pedestrian starting point for temple walking circuits",
            isFamousHighlight = false,
            isRashParticipant = false,
            isKaliSpecial = false,
            crowdStatus = CrowdStatus.LOW
        )
    )

    fun getAllFestivals(): List<Festival> = listOf(
        Festival(
            id = "rash_yatra",
            nameBn = "শান্তিপুর রাসযাত্রা ও ভাঙা রাস",
            nameEn = "Santipur Rash Yatra & Bhanga Rash",
            bengaliSeason = "কার্তিক পূর্ণিমা (হেমন্ত কাল)",
            approximateDate = "নভেম্বর ২০২৬ (কার্তিক পূর্ণিমা তিথি)",
            historyBn = "শ্রী অদ্বৈত আচার্য প্রবর্তিত বৈষ্ণব ভক্তি আন্দোলনের অবিচ্ছেদ্য অংশ। প্রায় সাড়ে পাঁচশত বছরের প্রাচীন এই উৎসবে শান্তিপুরের ঘরে ঘরে গোপাল ও রাধারমণ যুগল বিগ্রহের রাসলীলা অনুষ্ঠিত হয়। তৃতীয় দিনে অনুষ্ঠিত 'ভাঙা রাস' শান্তিপুরের অনন্য বিশ্বখ্যাত বৈশিষ্ট্য, যেখানে ঐতিহ্যবাহী কাঠের সুসজ্জিত রথে বিগ্রহের নগর পরিক্রমা ও বিভিন্ন সাজের সঙ প্রদক্ষিণ করে।",
            historyEn = "Tracing back over 500 years to the era of Sri Advaita Acharya, Santipur Rash Yatra celebrates Radha-Krishna's divine Rasalila. The 3rd day, 'Bhanga Rash', is world-renowned for monumental ornate wooden chariots parading through the town accompanied by devotional Kirtan and folk processions.",
            significanceBn = "শান্তিপুরের রাস শুধু ধর্মীয় পুজো নয়, বাংলার শ্রেষ্ঠ সাংস্কৃতিক ও বৈষ্ণব মিলনমেলা।",
            significanceEn = "A grand confluence of spirituality, Bengal handloom art, and timeless Vaishnava community harmony.",
            keyRitualsBn = listOf(
                "অধিবাস ও রাসমণ্ডপে যুগল বিগ্রহ স্থাপন",
                "মহারাধনা ও নিখিল সন্দেশ সহ ৫৬ ভোগ নিবেদন",
                "অখণ্ড হরিনাম সংকীর্তন ও বৈষ্ণব সাধুদের আগমন",
                "ভাঙা রাস: ঐতিহ্যবাহী রথারোহণ ও নগর শোভাযাত্রা"
            ),
            participatingPlacesCount = 18,
            isPrimaryFeature = true,
            accentTag = "স্বাক্ষরিত শ্রেষ্ঠ উৎসব"
        ),
        Festival(
            id = "kali_puja",
            nameBn = "শান্তিপুর কালীপুজো (আগমেশ্বরী ও বামাকালী)",
            nameEn = "Santipur Kali Puja (Agameswari & Bamakali)",
            bengaliSeason = "কার্তিক অমাবস্যা (দীপান্বিতা)",
            approximateDate = "অক্টোবর / নভেম্বর ২০২৬",
            historyBn = "শান্তিপুরের শাক্ত ঐতিহ্য সুপ্রাচীন। মহা তন্ত্রসাধক কৃষ্ণানন্দ আগমবাগীশের স্মৃতিধন্য দেবী আগমেশ্বরী এবং জাগ্রত বামাকালী মাতার নিশীথ পুজোয় সমগ্র শান্তিপুর আলোয় ঝলমল করে। হাজার হাজার মাটির প্রদীপ, আলোকসজ্জা ও তান্ত্রিক আচার মেনে গভীর রাতে এই পুজো অনুষ্ঠিত হয়।",
            historyEn = "Santipur holds deep Shakti and Tantric roots dating to saint Agambagish. The town lights up with thousands of earthen lamps and sacred rituals during the midnight worship of Maa Agameswari and Maa Bamakali.",
            significanceBn = "ভক্তি, আলো ও শক্তির গভীর উপলব্ধি যা বহু শতাব্দীর শাক্ত ঐতিহ্যকে বাঁচিয়ে রেখেছে।",
            significanceEn = "An extraordinary nocturnal festival of radiance, spiritual power, and cultural unity.",
            keyRitualsBn = listOf(
                "দীপান্বিতা অমাবস্যার প্রদীপ প্রজ্বলন",
                "নিশীথ তান্ত্রিক মহাপূজা ও হোমযজ্ঞ",
                "রক্তজবা নিবেদন ও বিশেষ পুষ্পাঞ্জলি",
                "শান্তিপূর্ণ প্যান্ডেল পরিক্রমা ও প্রসাদ বিতরণ"
            ),
            participatingPlacesCount = 24,
            isPrimaryFeature = true,
            accentTag = "মহাশাক্ত উৎসব"
        ),
        Festival(
            id = "gopal_puja",
            nameBn = "শান্তিপুর গোপালপুজো",
            nameEn = "Santipur Gopal Puja",
            bengaliSeason = "কার্তিক মাস (অন্নকূট তিথি)",
            approximateDate = "কার্তিক অমাবস্যার পরবর্তী দিনগুলিতে",
            historyBn = "বাৎসল্য রসে ভগবান শ্রীকৃষ্ণের বাল্যরূপের পূজা শান্তিপুরের ঘরে ঘরে পারিবারিক ঐতিহ্য। মেজো গোপাল সহ বহু পরিবারে গোপালের অভিষেক ও শত ব্যঞ্জনের অন্নকূট নিবেদন করা হয়।",
            historyEn = "A heartwarming domestic and temple celebration centered on child Krishna (Gopal) venerated in pure parental devotion (Vatsalya rasa).",
            significanceBn = "পারিবারিক ভক্তি ও আতিথেয়তার অমলিন নিদর্শন।",
            significanceEn = "A timeless celebration of domestic hospitality and sweet filial devotion.",
            keyRitualsBn = listOf("গোপালের বাল্যবেশে সাজ", "অন্নকূট ভোগ নিবেদন", "কীর্তন গান"),
            participatingPlacesCount = 12
        ),
        Festival(
            id = "jagadhatri_puja",
            nameBn = "জগদ্ধাত্রীপুজো",
            nameEn = "Jagadhatri Puja",
            bengaliSeason = "কার্তিক শুক্লানবমী",
            approximateDate = "নভেম্বর ২০২৬",
            historyBn = "শান্তিপুর ও সন্নিহিত নদীয়া জেলার ঐতিহ্যবাহী দেবী জগদ্ধাত্রীর চার প্রহরে একদিনে পুজো। সুবিশাল আলোক তোরণ ও সুশৃঙ্খল ভক্ত সমাগম ঘটে।",
            historyEn = "Venerating the holder of the universe, Devi Jagadhatri, worshipped in four Prahar rituals in a single auspicious day.",
            significanceBn = "শান্তি ও সমৃদ্ধির প্রার্থনায় মহামায়ার অর্চনা।",
            significanceEn = "Worship for peace, fortitude, and cosmic balance.",
            keyRitualsBn = listOf("চতুর্থ প্রহর ব্যাপী অহোরাত্র পূজা", "ধুনুচি নৃত্য", "বিসর্জন শোভাযাত্রা"),
            participatingPlacesCount = 15
        ),
        Festival(
            id = "durga_puja",
            nameBn = "দুর্গাপুজো (পারিবারিক ও বারোয়ারি)",
            nameEn = "Durga Puja",
            bengaliSeason = "আশ্বিন মাস (শারদোৎসব)",
            approximateDate = "অক্টোবর ২০২৬",
            historyBn = "শান্তিপুরের বহু বনেদি গোস্বামী পরিবার ও প্রাচীন ঠাকুরবাড়িতে একচালার প্রতিমায় শত শত বছরের নিষ্ঠায় অনুষ্ঠিত হয় দুর্গাপূজা।",
            historyEn = "Celebrated with profound heritage across centuries-old ancestral homes and vibrant community clubs in Santipur.",
            significanceBn = "বাঙালির জাতীয় মহোৎসব ও পরিবারের পুনর্মিলন।",
            significanceEn = "Bengal's UNESCO-inscribed cultural extravaganza and homecoming.",
            keyRitualsBn = listOf("বোধনের ঘট স্থাপন", "মহাষ্টমীর অঞ্জলি ও কুমারী পূজা", "বিজয়ার কোলাকুলি"),
            participatingPlacesCount = 35
        ),
        Festival(
            id = "rath_yatra",
            nameBn = "রথযাত্রা",
            nameEn = "Rath Yatra",
            bengaliSeason = "আষাঢ় মাস (বর্ষাকাল)",
            approximateDate = "জুলাই ২০২৬",
            historyBn = "শান্তিপুরের বিভিন্ন ঐতিহ্যবাহী মন্দির থেকে শ্রী শ্রী জগন্নাথ, বলরাম ও সুভদ্রা দেবীর রথ টেনে ভক্তরা আনন্দোল্লাসে মেতে ওঠেন।",
            historyEn = "Grand chariots of Lord Jagannath, Balabhadra, and Subhadra drawn by thousands along historic streets.",
            significanceBn = "সকল ভেদাভেদ ভুলে শ্রীজগন্নাথের রথের রশি স্পর্শের পুণ্যতিথি।",
            significanceEn = "Egalitarian unity pulling the divine chariot ropes together.",
            keyRitualsBn = listOf("নেত্রোৎসব ও রথারোহণ", "মাসির বাড়ি গমন", "উল্টোরথ পরিক্রমা"),
            participatingPlacesCount = 8
        ),
        Festival(
            id = "ganesh_puja",
            nameBn = "গণেশপুজো",
            nameEn = "Ganesh Puja",
            bengaliSeason = "ভাদ্র শুক্লাচতুর্থী",
            approximateDate = "সেপ্টেম্বর ২০২৬",
            historyBn = "শান্তিপুরের ব্যবসায়ী সমাজ ও তরুণ ক্লাবের উদ্যোগে বিঘ্নহর্তা গণপতির বর্ণাঢ্য আরাধনা ও সাংস্কৃতিক অনুষ্ঠান।",
            historyEn = "Vibrant community worship of Lord Ganesha celebrated by trade quarters and youth associations.",
            significanceBn = "শুভ সূচনা ও সিদ্ধিদাতার আশীর্বাদ প্রার্থনা।",
            significanceEn = "Prayers for prosperity, intellect, and auspicious endeavors.",
            keyRitualsBn = listOf("মো Utility ভোগ প্রদান", "আরতি ও সাংস্কৃতিক অনুষ্ঠান"),
            participatingPlacesCount = 10
        ),
        Festival(
            id = "janmashtami",
            nameBn = "জন্মাষ্টমী",
            nameEn = "Janmashtami",
            bengaliSeason = "ভাদ্র কৃষ্ণাষ্টমী",
            approximateDate = "আগস্ট / সেপ্টেম্বর ২০২৬",
            historyBn = "শ্রীকৃষ্ণের আবির্ভাব তিথিতে শান্তিপুরের প্রাচীন বৈষ্ণব আখড়া ও মন্দিরগুলিতে গভীর রাত পর্যন্ত নামসংকীর্তন ও নন্দোৎসব পালিত হয়।",
            historyEn = "Midnight celebration of Lord Krishna's divine advent marked by fasting, chanting, and festive Nandotsav.",
            significanceBn = "প্রেমভক্তির অনন্ত উৎস শ্রীকৃষ্ণ আরাধনা।",
            significanceEn = "Remembrance of divine love, truth, and righteous guidance.",
            keyRitualsBn = listOf("নিশীথ অভিষেক", "দধিকাদো উৎসব", "নন্দোৎসব ভোজ"),
            participatingPlacesCount = 14
        ),
        Festival(
            id = "dol_yatra",
            nameBn = "দোলযাত্রা ও গৌর পূর্ণিমা",
            nameEn = "Dol Yatra & Gaura Purnima",
            bengaliSeason = "ফাল্গুন পূর্ণিমা (বসন্তকাল)",
            approximateDate = "মার্চ ২০২৬",
            historyBn = "শ্রী চৈতন্য মহাপ্রভুর পবিত্র আবির্ভাব দিবস এবং রাধাকৃষ্ণের আবির খেলা। শান্তিপুরের শান্ত রাজপথ আবিরের রঙে রঙিন হয়ে ওঠে।",
            historyEn = "Coinciding with the advent of Sri Chaitanya Mahaprabhu, painted with colored gulal, kirtan, and festive sweets.",
            significanceBn = "বসন্তের আগমন ও সামাজিক প্রীতি বিনিময়।",
            significanceEn = "Welcoming spring in colors of joy and collective brotherhood.",
            keyRitualsBn = listOf("বিগ্রহে আবির অর্পণ", "নগর সংকীর্তন", "মালপোয়া ও পায়েস ভোগ"),
            participatingPlacesCount = 16
        ),
        Festival(
            id = "saraswati_puja",
            nameBn = "সরস্বতীপুজো",
            nameEn = "Saraswati Puja",
            bengaliSeason = "মাঘ শুক্লাপঞ্চমী (শ্রীপঞ্চমী)",
            approximateDate = "ফেব্রুয়ারি ২০২৬",
            historyBn = "শান্তিপুরের বিদ্যালয়, সঙ্গীত একাডেমি এবং পাড়ায় পাড়ায় বিদ্যার অধিষ্ঠাত্রী দেবীর শ্রদ্ধাভরে আরাধনা।",
            historyEn = "Reverence to Devi Saraswati by scholars, weavers, students, and musicians.",
            significanceBn = "জ্ঞান, কলা ও সাংস্কৃতিক চর্চার বিকাশ।",
            significanceEn = "Honoring learning, wisdom, and fine arts.",
            keyRitualsBn = listOf("পুস্তক ও বাদ্যযন্ত্র নিবেদন", "হাতেখড়ি", "বাসন্তী শাড়ি পরিধান"),
            participatingPlacesCount = 20
        ),
        Festival(
            id = "lakshmi_puja",
            nameBn = "লক্ষ্মীপুজো (কোজাগরী)",
            nameEn = "Kojagari Lakshmi Puja",
            bengaliSeason = "আশ্বিন পূর্ণিমা তিথি",
            approximateDate = "অক্টোবর ২০২৬",
            historyBn = "শারদীয় পূর্ণিমার রাতে শান্তিপুরের ঘরে ঘরে আলপনা এঁকে ধনের দেবী মা লক্ষ্মীর পূজা করা হয়।",
            historyEn = "Graceful domestic worship during autumnal full moon with intricate handmade alpana art.",
            significanceBn = "সংসারের শ্রীবৃদ্ধি ও কৃষিসমৃদ্ধি।",
            significanceEn = "Household wellbeing, abundance, and peace.",
            keyRitualsBn = listOf("আলপনা অঙ্কন", "নাড়ু ও মোয়া নিবেদন", "নিশীথ জাগরণ"),
            participatingPlacesCount = 30
        ),
        Festival(
            id = "vishwakarma_puja",
            nameBn = "বিশ্বকর্মাপুজো",
            nameEn = "Vishwakarma Puja",
            bengaliSeason = "ভাদ্র সংক্রান্তি (কন্যা সংক্রান্তি)",
            approximateDate = "১৭ সেপ্টেম্বর ২০২৬",
            historyBn = "বিশ্বখ্যাত শান্তিপুরী তাঁতশিল্পের স্রষ্টা তাঁতি ভাই-বোনদের প্রধান উৎসব। তাঁতযন্ত্র ও তাঁতশাল সাজিয়ে শিল্পদেবতার পুজো হয় ও ঘুড়ি ওড়ানো হয়।",
            historyEn = "Honored enthusiastically by the master handloom weavers of Santipur, worshiping their looms followed by joyous kite-flying.",
            significanceBn = "কারিগর ও শ্রমজীবী মানুষের সৃজনশীলতার জয়গান।",
            significanceEn = "Tribute to craftspeople and the soul of Bengal handloom.",
            keyRitualsBn = listOf("তাঁতযন্ত্র ও যন্ত্রপাতির পূজা", "ঘুড়ি ওড়ানো উৎসব", "মিষ্টিমুখ"),
            participatingPlacesCount = 25
        ),
        Festival(
            id = "barowari_utsabh",
            nameBn = "স্থানীয় বারোয়ারি ও পারিবারিক পুজো",
            nameEn = "Barowari & Heritage Family Pujas",
            bengaliSeason = "সারা বছরব্যাপী",
            approximateDate = "ঋতুভিত্তিক",
            historyBn = "শান্তিপুরের সুপ্রাচীন পাড়াগুলিতে বংশপরম্পরায় উদযাপিত কুলদেবতা ও পাড়ার বারোয়ারি উৎসব।",
            historyEn = "Historic community and ancestral pujas upholding multi-generational rituals across neighborhood paras.",
            significanceBn = "পাড়ার সৌহার্দ্য ও প্রাচীন আচার সংরক্ষণ।",
            significanceEn = "Preserving indigenous folklore and neighborly bonding.",
            keyRitualsBn = listOf("পাড়া সম্মেলন", "ভোগ বিতরণ", "লোকগীতি ও যাত্রা"),
            participatingPlacesCount = 40
        ),
        Festival(
            id = "rash_mela_cultural",
            nameBn = "শান্তিপুর রাসমেলা ও সাংস্কৃতিক উৎসব",
            nameEn = "Santipur Rash Mela & Cultural Festival",
            bengaliSeason = "কার্তিক পূর্ণিমা থেকে অগ্রহায়ণ",
            approximateDate = "নভেম্বর ২০২৬ (৭ দিনব্যাপী)",
            historyBn = "নদীয়া জেলার অন্যতম বৃহত্তম লোকমেলা। শতাব্দী প্রাচীন মেলায় বাংলার তাঁতবস্ত্র, ঐতিহ্যবাহী কাঠের পুতুল, মাটির খেলনা ও বিখ্যাত মিষ্টির সমাহার ঘটে।",
            historyEn = "One of Bengal's largest rural and heritage fairs, attracting lakhs of visitors with exquisite handlooms, terracotta crafts, and folk arts.",
            significanceBn = "লোকসংস্কৃতি, গ্রামীণ অর্থনীতি ও ঐতিহ্যের মহা মিলনমেলা।",
            significanceEn = "A massive celebration of folk economy, crafts, and heritage tourism.",
            keyRitualsBn = listOf("মেলার আনুষ্ঠানিক উদ্বোধন", "শান্তিপুরী তাঁত মেলা", "বাউল ও কীর্তন সঙ্গীত সন্ধ্যা"),
            participatingPlacesCount = 10
        )
    )

    fun getCuratedRoutes(): List<RoutePlan> = listOf(
        RoutePlan(
            id = "heritage_rash_circuit",
            titleBn = "ঐতিহ্যবাহী রাসযাত্রা পরিক্রমা (৫টি প্রধান দর্শন)",
            titleEn = "Heritage Rash Yatra Circuit (5 Key Stops)",
            subtitleBn = "অদ্বৈত মহাপ্রভুর উত্তরাধিকার ও ঐতিহাসিক ভাঙা রাসের প্রাচীন মন্দিরসমূহ",
            subtitleEn = "Advaita Acharya's legacy and historic Rasotsav temple landmarks",
            estimatedMinutes = 110,
            totalDistanceKm = 3.4,
            suitabilityTagBn = "সকল ভক্ত ও পথচারীদের জন্য আদর্শ",
            suitabilityTagEn = "Ideal for all pilgrims & walkers",
            placeIds = listOf("boro_goswami", "chhoto_goswami", "mejo_gopal", "shyamchand", "topkhana_mela_ground")
        ),
        RoutePlan(
            id = "kali_night_trail",
            titleBn = "নিশীথিনী কালীপুজো পদযাত্রা (মহাশক্তি পরিক্রমা)",
            titleEn = "Midnight Kali Puja Walking Trail (Shakti Circuit)",
            subtitleBn = "আগমেশ্বরী ও বামাকালী সহ শান্তিপুরের জাগ্রত শাক্ত মন্দিরসমূহ",
            subtitleEn = "Sacred midnight walking route through Agameswari, Bamakali and historical shrines",
            estimatedMinutes = 95,
            totalDistanceKm = 2.8,
            suitabilityTagBn = "রাত্রি ৯টা থেকে মধ্যরাত দর্শনের উপযোগী",
            suitabilityTagEn = "Best experienced between 9 PM and Midnight",
            placeIds = listOf("agameswari", "bamakali", "dakatkali", "shyamchand")
        ),
        RoutePlan(
            id = "senior_family_accessible",
            titleBn = "পরিবার ও প্রবীণদের সহজ পথ (স্বল্প হাঁটা ও সুগম্যতা)",
            titleEn = "Senior Citizen & Family Accessible Tour",
            subtitleBn = "প্রশস্ত প্রবেশপথ, বসার স্থান ও নিকটস্থ পার্কিং সহ শান্ত সুগম মন্দিরসমূহ",
            subtitleEn = "Wide level entrances, ample seating, and direct vehicle drop-offs",
            estimatedMinutes = 75,
            totalDistanceKm = 2.2,
            suitabilityTagBn = "হুইলচেয়ার ও বয়স্কদের জন্য বিশেষ প্রস্তাবিত",
            suitabilityTagEn = "Highly recommended for wheel-chair & senior citizens",
            placeIds = listOf("advaita_babla", "shyamchand", "sutragarh_sarbajanin")
        )
    )

    fun getHeritageArticles(): List<Article> = listOf(
        Article(
            id = "art_rash_history",
            titleBn = "শান্তিপুরের রাস উৎসবের আদি ইতিহাস ও অদ্বৈত মহাপ্রভুর উত্তরাধিকার",
            titleEn = "Origin of Santipur Rash Yatra and the Legacy of Advaita Acharya",
            authorBn = "অধ্যাপক ড. সমীরণ বন্দ্যোপাধ্যায়",
            authorEn = "Prof. Dr. Samiran Bandyopadhyay",
            readTimeMinutes = 5,
            summaryBn = "পঞ্চদশ শতাব্দীতে শ্রী অদ্বৈত আচার্য কীভাবে শান্তিপুরে ভক্তি আন্দোলন ও রাসোৎসবের ভিত্তি স্থাপন করেছিলেন তার ঐতিহাসিক প্রামাণ্য বিশ্লেষণ।",
            summaryEn = "A researched historical chronicle of how Sri Advaita Acharya established the devotional Rasotsav in Santipur during the 15th century.",
            contentBn = "নদীয়ার গঙ্গাতীরবর্তী প্রাচীন বাণিজ্য ও সাধনা নগরী শান্তিপুর। শ্রী অদ্বৈত মহাপ্রভুর প্রবর্তনায় এখানে শ্রী শ্রী রাধারমণ জিউ-এর বিগ্রহে প্রথম রাসমঞ্চে পূজা শুরু হয়। শান্তিপুরের রাস অন্যান্য রাস থেকে আলাদা কারণ এখানে ভক্তির সাথে লোকসংস্কৃতি ও পরিবারের মেলবন্ধন ঘটেছে। কার্তিক পূর্ণিমার পুণ্যলগ্নে সমগ্র শান্তিপুর আলোকমালায় সেজে ওঠে। ভাঙা রাসের দিন ঐতিহাসিক কাঠে খোদাই করা কারুকার্যময় রথে যখন শ্রীকৃষ্ণের বিগ্রহ আরোহণ করে, লক্ষ জনতার উলুধ্বনি ও হরিনামে প্রকম্পিত হয় গঙ্গাতীর।",
            contentEn = "Santipur on the banks of the sacred Bhagirathi River holds a singular status in Bengal's spiritual map. Initiated by Sri Advaita Acharya, the Rasotsav began with the divine worship of Sri Sri Radharaman Jiu. What distinguishes Santipur's Rash is its deep community integration, where royal family temples and humble weaver paras share equal fervor.",
            publishedDate = "অক্টোবর ২০২৬"
        ),
        Article(
            id = "art_agameswari_tantra",
            titleBn = "আগমেশ্বরী ও শান্তিপুরের শাক্ত ঐতিহ্যের সন্ধানে",
            titleEn = "Exploring Agameswari and Santipur's Tantric Heritage",
            authorBn = "গবেষক কল্যাণ চট্টোপাধ্যায়",
            authorEn = "Kalyan Chattopadhyay",
            readTimeMinutes = 4,
            summaryBn = "মহাতন্ত্রসাধক কৃষ্ণানন্দ আগমবাগীশের পরম্পরায় শান্তিপুরে শক্তি সাধনা ও আগমেশ্বরী দেবীর আবির্ভাব কথা।",
            summaryEn = "The mystical history of saint Krishnananda Agambagish and the sacred advent of Goddess Agameswari.",
            contentBn = "বৈষ্ণব নগরী হিসেবে শান্তিপুর সর্বজনবিদিত হলেও এর গভীরে প্রবহমান এক সুপ্রাচীন শাক্ত ধারা। আগমেশ্বরী তলার দেবী মহাকালী অতি প্রাচীন। দীপান্বিতা অমাবস্যায় একরাতের জন্য মৃণ্ময়ী দেবীর রূপ নির্মাণ করে গভীর রাতে তন্ত্রমতে পূজা সম্পন্ন করা হয় এবং পরদিনই সুশৃঙ্খলভাবে বিসর্জন দেওয়া হয়। দেবীর বিরাটাকার রূপ ভক্তদের মনে এক অলৌকিক শ্রদ্ধাবোধের সঞ্চার করে।",
            contentEn = "While celebrated globally as a Vaishnava capital, Santipur simultaneously cradles an equally profound Tantric Shakti tradition. Maa Agameswari is venerated with sublime nocturnal Tantric rituals on Diwali night, maintaining uncorrupted ceremonial strictness across centuries.",
            publishedDate = "অক্টোবর ২০২৬"
        ),
        Article(
            id = "art_santipuri_tant",
            titleBn = "শান্তিপুরী তাঁত ও উৎসবের নিবিড় সম্পর্ক",
            titleEn = "The Sacred Bond between Santipur Handloom and Festival Culture",
            authorBn = "তাঁতশিল্পী রূপক প্রামাণিক",
            authorEn = "Rupak Pramanik",
            readTimeMinutes = 4,
            summaryBn = "শান্তিপুরের বিখ্যাত মিহি সুতির শাড়ি ও উৎসবকালীন ঐতিহ্যবাহী অলঙ্করণের ইতিহাস।",
            summaryEn = "How centuries of Bengal handloom weaving artistry became woven into the fabric of regional festivals.",
            contentBn = "শান্তিপুরের উৎসব ও শান্তিপুরী তাঁতের শাড়ি একে অপরের পরিপূরক। পঞ্চদশ শতাব্দী থেকেই শান্তিপুরের তাঁতিদের বোনা মসৃণ ঢাকাই ও শান্তিপুরী কাপড়ে দেবদেবীদের পোশাক তৈরি হতো। রাস ও কালীপুজোর সময় সারা দেশ থেকে বস্ত্রপ্রেমীরা শান্তিপুরে ভিড় জমান খাঁটি দেশি তাঁতের শাড়ি সংগ্রহের জন্য।",
            contentEn = "Santipur handloom is not just an industry; it is a spiritual craft. Since medieval times, master weavers tailored deity garments with unmatched fineness, cementing Santipur's status as the textile jewel of Bengal.",
            publishedDate = "অক্টোবর ২০২৬"
        )
    )

    private fun initialAdvisories(): List<Advisory> = listOf(
        Advisory(
            id = "adv_1",
            titleBn = "রাস উৎসব উপলক্ষে একমুখী পথ (One-Way) নির্দেশিকা",
            titleEn = "One-Way Traffic Advisory for Rash Yatra",
            messageBn = "সুত্রাগড় মোড় থেকে নেতাজি সুভাষ রোড হয়ে গোস্বামী পাড়া পর্যন্ত সমস্ত চারচাকা ও টোটো চলাচল একমুখী থাকবে। পথচারীদের জন্য নিরাপদ ওয়াকিং করিডোর চিহ্নিত করা হয়েছে।",
            messageEn = "All 4-wheelers and Totos from Sutragarh More via NS Road to Goswami Para will be strictly One-Way. Dedicated pedestrian walking corridor is operational.",
            severity = AdvisorySeverity.RESTRICTION,
            timestamp = "আজ সকাল ৮:০০ (Today 8:00 AM)"
        ),
        Advisory(
            id = "adv_2",
            titleBn = "সুত্রাগড় হাই স্কুল মাঠে বিনামূল্যে সার্বিক পার্কিং ব্যবস্থা",
            titleEn = "Free Central Parking at Sutragarh High School Ground",
            messageBn = "বাইরে থেকে আসা সমস্ত ব্যক্তিগত গাড়ি ও মোটর সাইকেল সুত্রাগড় হাই স্কুল মাঠে পার্ক করার জন্য অনুরোধ করা হচ্ছে। মূল মেলা প্রাঙ্গণে যানবাহন প্রবেশ নিষিদ্ধ।",
            messageEn = "All incoming private vehicles and two-wheelers are requested to park at Sutragarh High School Ground. Vehicle entry inside central fair precinct is prohibited.",
            severity = AdvisorySeverity.INFO,
            timestamp = "আজ সকাল ৯:৩০ (Today 9:30 AM)"
        ),
        Advisory(
            id = "adv_3",
            titleBn = "জরুরি মেডিকেল বুথ ও ২৪ ঘণ্টা অ্যাম্বুলেন্স প্রস্তুতি",
            titleEn = "Emergency Medical Booths & 24x7 Ambulance Standby",
            messageBn = "তোপখানা মোড় ও শান্তিপুর হাসপাতালে সার্বক্ষণিক চিকিৎসক দল ও অ্যাম্বুলেন্স প্রস্তুত রয়েছে। হেল্পলাইন: ১০২ / ০৩৪৭৩-২৭৮০২৭।",
            messageEn = "Dedicated on-call doctors and ambulances stationed at Topkhana More and Santipur Hospital. Helpline: 102 / 03473-278027.",
            severity = AdvisorySeverity.INFO,
            timestamp = "আজ সকাল ১০:০০ (Today 10:00 AM)"
        )
    )

    private fun initialCrowdReports(): List<CrowdReport> = listOf(
        CrowdReport(
            id = "rep_1",
            placeId = "boro_goswami",
            placeNameBn = "বড় গোস্বামী বাড়ি",
            placeNameEn = "Boro Goswami Bari",
            status = CrowdStatus.HIGH,
            waitTimeMinutes = 25,
            submittedBy = "সঞ্জয় কর্মকার (ভক্ত)",
            timestampFormatted = "১০ মিনিট আগে (10 mins ago)",
            notesBn = "দুপুরের ভোগের পূর্বে দর্শনার্থীদের সুশৃঙ্খল দীর্ঘ লাইন রয়েছে। স্বেচ্ছাসেবীরা জল সরবরাহ করছেন।",
            isOfficialVerified = true
        ),
        CrowdReport(
            id = "rep_2",
            placeId = "agameswari",
            placeNameBn = "আগমেশ্বরী মন্দির",
            placeNameEn = "Agameswari Mandir",
            status = CrowdStatus.MODERATE,
            waitTimeMinutes = 12,
            submittedBy = "শান্তিপুর স্বেচ্ছাসেবক দল",
            timestampFormatted = "২৫ মিনিট আগে (25 mins ago)",
            notesBn = "লাইন স্বাভাবিক গতিতে এগোচ্ছে। প্রবীণ নাগরিকদের জন্য পৃথক কাউন্টার খোলা রয়েছে।",
            isOfficialVerified = true
        ),
        CrowdReport(
            id = "rep_3",
            placeId = "advaita_babla",
            placeNameBn = "শ্রী শ্রী অদ্বৈত পাঠ ও বাবলা আশ্রম",
            placeNameEn = "Advaita Paath & Babla Ashram",
            status = CrowdStatus.LOW,
            waitTimeMinutes = 5,
            submittedBy = "তীর্থযাত্রী সমীর",
            timestampFormatted = "৪০ মিনিট আগে (40 mins ago)",
            notesBn = "খুবই শান্ত ও মনোরম পরিবেশ। কোনো ভিড় নেই, স্বচ্ছন্দে দর্শন করা যাচ্ছে।",
            isOfficialVerified = false
        )
    )

    private fun initialCorrections(): List<UserCorrection> = listOf(
        UserCorrection(
            id = "c1",
            placeName = "শ্যামচাঁদ মন্দির",
            userEmailOrPhone = "debayan.nadia@gmail.com",
            correctionType = "দর্শন সময়সূচী (Darshan Timings)",
            message = "রাস উৎসবের সময় সন্ধ্যা আরতির সময়সূচি বিকেল ৫:৩০ থেকে রাত ১০:০০ পর্যন্ত সম্প্রসারিত করা হয়েছে।",
            submittedAt = "২২ অক্টোবর ২০২৬, সন্ধ্যা ৭:১০",
            status = "অনুমোদিত ও হালনাগাদ (Approved)"
        ),
        UserCorrection(
            id = "c2",
            placeName = "সুত্রাগড় পার্কিং লট",
            userEmailOrPhone = "98301XXXXX",
            correctionType = "পার্কিং তথ্য (Parking Info)",
            message = "স্কুল মাঠের পাশাপাশি সংলগ্ন ক্লাবের মাঠেও বাইক পার্কিংয়ের সুব্যবস্থা করা হয়েছে।",
            submittedAt = "২৩ অক্টোবর ২০২৬, সকাল ৮:৩০",
            status = "বিবেচনাধীন (Pending Review)"
        )
    )
}
