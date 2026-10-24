package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Place
import com.example.data.model.PlaceCategory
import com.example.data.repository.SantipurRepository
import com.example.ui.components.AlpanaDivider
import com.example.ui.components.SantipurCanvasMap
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SantipurViewModel

enum class HeritageViewMode(val labelBn: String, val labelEn: String) {
    LIST("তালিকা", "List"),
    MAP("মানচিত্র", "Map"),
    TIMELINE("ঐতিহাসিক সময়রেখা", "Timeline")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RashHeritageDirectoryScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val savedIds by viewModel.savedPlaceIds.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI

    var viewMode by remember { mutableStateOf(HeritageViewMode.LIST) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("সব স্থান") }

    val filteredPlaces = remember(searchQuery, selectedCategoryFilter) {
        viewModel.getHeritagePlacesFiltered(searchQuery, selectedCategoryFilter)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBn) "গোস্বামী বাড়ি ও মন্দির ডিরেক্টরি" else "Goswami Bari & Temple Directory",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Gold100
                        )
                        Text(
                            text = "শান্তিপুর রাস উৎসব ২০২৬",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gold300
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("heritage_dir_back_btn")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Gold100
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ShaktiDark
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ShaktiCream)
                .testTag("rash_heritage_directory_screen")
        ) {
            // 1. Heritage Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(ShaktiDark, ShaktiDarkSurface, ShaktiRedDark)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Column {
                    Surface(
                        color = ShaktiGold.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isBn) "ঐতিহাসিক অনুসন্ধান ও গাইড" else "VERIFIED HERITAGE DIRECTORY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = ShaktiGoldLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "শান্তিপুর রাস উৎসব: গোস্বামী বাড়ি ও মন্দির",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = ShaktiGold,
                        fontSize = 22.sp
                    )

                    Text(
                        text = "“রাসের ঐতিহ্য, মন্দিরের ইতিহাস, শান্তিপুরের গর্ব”",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gold200,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bilingual Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = if (isBn) "বাড়ি, মন্দির বা দেবতার নামে খুঁজুন…" else "Search by house, temple or deity…",
                                fontSize = 13.sp,
                                color = ShaktiDark.copy(alpha = 0.6f)
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = ShaktiRed)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = ShaktiRed)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = ShaktiGold,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = ShaktiDark,
                            unfocusedTextColor = ShaktiDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("heritage_search_bar")
                    )
                }
            }

            // 2. View Mode Tabs (List, Map, Timeline)
            Surface(
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    HeritageViewMode.values().forEach { mode ->
                        val isSelected = viewMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewMode = mode },
                            label = {
                                Text(
                                    text = if (isBn) mode.labelBn else mode.labelEn,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                val icon = when (mode) {
                                    HeritageViewMode.LIST -> Icons.Default.List
                                    HeritageViewMode.MAP -> Icons.Default.Map
                                    HeritageViewMode.TIMELINE -> Icons.Default.Timeline
                                }
                                Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ShaktiRed,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White,
                                containerColor = ShaktiCreamDark,
                                labelColor = ShaktiDark
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // 3. Category Filter Chips (when in List or Map view)
            if (viewMode != HeritageViewMode.TIMELINE) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categories = listOf("সব স্থান", "গোস্বামী বাড়ি", "ঐতিহ্যবাহী মন্দির")
                    categories.forEach { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        Surface(
                            color = if (isSelected) ShaktiDark else Color.White,
                            shape = RoundedCornerShape(20.dp),
                            shadowElevation = if (isSelected) 2.dp else 1.dp,
                            modifier = Modifier
                                .clickable { selectedCategoryFilter = cat }
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) ShaktiGoldLight else ShaktiDark,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // 4. Main Body based on View Mode
            when (viewMode) {
                HeritageViewMode.LIST -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("heritage_list_view"),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBn) "ঐতিহাসিক গবেষণা তালিকা (${filteredPlaces.size}টি স্থান)" else "Directory Listings (${filteredPlaces.size})",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = ShaktiDark
                                )
                                Text(
                                    text = "Google Maps লিঙ্ক সংরক্ষিত",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ShaktiRed
                                )
                            }
                        }

                        items(filteredPlaces) { place ->
                            HeritagePlaceCard(
                                place = place,
                                isSaved = savedIds.contains(place.id),
                                isBn = isBn,
                                onDetailClick = { viewModel.openPlaceDetail(place.id) },
                                onSaveToggle = { viewModel.toggleSavePlace(place.id) },
                                onInAppNavClick = { viewModel.startInAppNavigation(place.id) },
                                onGoogleMapsClick = {
                                    place.googleMapsUrl?.let { url ->
                                        openExternalUrl(context, url)
                                    }
                                },
                                onDirectionsClick = {
                                    place.googleMapsUrl?.let { url ->
                                        // Open direction in Google Maps
                                        openExternalUrl(context, "$url&dir_action=navigate")
                                    }
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                HeritageViewMode.MAP -> {
                    val selectedPlace = filteredPlaces.find { it.id == uiState.selectedPlaceId }
                    Box(modifier = Modifier.fillMaxSize()) {
                        SantipurCanvasMap(
                            places = filteredPlaces,
                            selectedPlace = selectedPlace,
                            onPlaceSelect = { p ->
                                viewModel.navigateTo(AppScreen.PLACE_DETAIL, p.id)
                            },
                            userLat = uiState.userLat,
                            userLng = uiState.userLng,
                            language = uiState.language,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Floating Info Card on bottom
                        Surface(
                            color = ShaktiDark.copy(alpha = 0.92f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isBn) "১০টি ঐতিহাসিক স্থানের অবস্থান" else "10 Heritage Landmarks",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ShaktiGold
                                    )
                                    Text(
                                        text = if (isBn) "যেকোনো পিনে ট্যাপ করে বিস্তারিত ও অভ্যন্তরীণ নেভিগেশন খুলুন" else "Tap any pin to view details or start turn-by-turn navigation",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ShaktiCream
                                    )
                                }
                            }
                        }
                    }
                }

                HeritageViewMode.TIMELINE -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("heritage_timeline_view"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Surface(
                                color = ShaktiGoldLight.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = ShaktiRed)
                                    Text(
                                        text = if (isBn)
                                            "শান্তিপুরের রাস ও মন্দিরের ঐতিহাসিক সময়রেখা — সুপ্রতিষ্ঠিত তথ্য ও প্রামাণ্য স্থানীয় ধারাক্রম।"
                                        else
                                            "Historical Milestones of Santipur Rash Utsav and its sacred temple architecture.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ShaktiDark
                                    )
                                }
                            }
                        }

                        val timelineMilestones = listOf(
                            TimelineItem(
                                era = "১৪৮৪ খ্রিষ্টাব্দ (১৫শ শতাব্দী)",
                                titleBn = "শ্রী অদ্বৈত মহাপ্রভুর আবির্ভাব ও রাসোৎসব প্রবর্তন",
                                descriptionBn = "শ্রী শ্রী রাধারমণ জিউ-এর বিগ্রহের মাধ্যমে শান্তিপুরে রাস উৎসবের ঐতিহাসিক সূচনা। ভক্তি আন্দোলনের শ্রেষ্ঠ কেন্দ্র হিসেবে শান্তিপুরের উত্থান।",
                                placeId = "boro_goswami"
                            ),
                            TimelineItem(
                                era = "১৬শ শতাব্দী (১৫৫৭ খ্রি.)",
                                titleBn = "পটেশ্বরী মন্দিরের সুপ্রাচীন লোকঐতিহ্য ও মথুরেশ গোস্বামীর ধারা",
                                descriptionBn = "কাপড়ের পটে দেবী আরাধনার বিরল লোকশিল্প এবং গোস্বামী পরিবারের বংশপরম্পরায় শ্রী শ্রী রাধা মদনগোপাল জিউ-এর সেবাধাম সম্প্রসারণ।",
                                placeId = "poteswari"
                            ),
                            TimelineItem(
                                era = "১৭২৬ খ্রিষ্টাব্দ (১৮শ শতাব্দী)",
                                titleBn = "বিশাল আটচালা স্থাপত্যে শ্যামচাঁদ মন্দির নির্মাণ",
                                descriptionBn = "শান্তিপুরের তাঁতি ও ভক্ত সম্প্রদায়ের সহযোগিতায় নির্মিত বাংলার সর্ববৃহৎ ৬০ ফুট উচ্চ আটচালা টেরাকোটা মন্দির।",
                                placeId = "shyamchand"
                            ),
                            TimelineItem(
                                era = "১৮শ শতাব্দী",
                                titleBn = "চাকফেরা, বাঁশবুনিয়া ও পাগলা গোস্বামী পরিবারের সেবা",
                                descriptionBn = "গোস্বামী পরিবারের বিভিন্ন ধারায় পারিবারিক রাস, ঐতিহ্যবাহী কাষ্ঠ রথে ভাঙা রাস ও অন্নকূটের বহুল বিস্তার।",
                                placeId = "chakphera_goswami"
                            ),
                            TimelineItem(
                                era = "১৮৪১ খ্রিষ্টাব্দ (১৯শ শতাব্দী)",
                                titleBn = "প্রভু বিজয়কৃষ্ণ গোস্বামীর আবির্ভাব ও সংস্কার আন্দোলন",
                                descriptionBn = "শান্তিপুরের আধ্যাত্মিক নবজাগরণ এবং বৈষ্ণব সাধনার নতুন দিগন্ত উন্মোচন।",
                                placeId = "bijoykrishna_goswami"
                            ),
                            TimelineItem(
                                era = "বর্তমান কাল (২০২৬)",
                                titleBn = "বিশ্বখ্যাত রাসমেলা ও ডিজিটাল ঐতিহ্য সংরক্ষণ",
                                descriptionBn = "শান্তিপুর রাসযাত্রাকে বিশ্বজনীন ঐতিহ্য হিসেবে তুলে ধরা এবং দর্শনার্থীদের জন্য আধুনিক নেভিগেশন সুবিধা।",
                                placeId = "boro_goswami"
                            )
                        )

                        items(timelineMilestones) { item ->
                            TimelineCard(
                                item = item,
                                isBn = isBn,
                                onOpenPlace = { viewModel.openPlaceDetail(item.placeId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeritagePlaceCard(
    place: Place,
    isSaved: Boolean,
    isBn: Boolean,
    onDetailClick: () -> Unit,
    onSaveToggle: () -> Unit,
    onInAppNavClick: () -> Unit,
    onGoogleMapsClick: () -> Unit,
    onDirectionsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("heritage_card_${place.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Category & Verification Badge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = ShaktiRed.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isBn) place.category.titleBn else place.category.titleEn,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ShaktiRed,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = StatusGreen.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isBn) "মানচিত্র লিঙ্ক সংরক্ষিত" else "Map Link Provided",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusGreen
                            )
                        }
                    }

                    IconButton(
                        onClick = onSaveToggle,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (isSaved) ShaktiRed else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bengali Name (Primary) & English Name (Secondary)
            Text(
                text = place.nameBn,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = ShaktiDark
            )

            Text(
                text = place.nameEn,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Associated Deity
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.SelfImprovement, contentDescription = null, tint = ShaktiGoldDark, modifier = Modifier.size(16.dp))
                Text(
                    text = "বিগ্রহ: ${place.deityBn}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = ShaktiRedDark
                )
            }

            // Locality & Era
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Icon(Icons.Default.Place, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Text(
                    text = "${place.locality} • ${place.establishedYear}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row: Google Maps, Directions, In-App Nav, Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Google Maps-এ দেখুন
                OutlinedButton(
                    onClick = onGoogleMapsClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ShaktiDark),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBn) "Maps-এ" else "Maps",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // রাস্তা দেখুন
                Button(
                    onClick = onDirectionsClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShaktiGold,
                        contentColor = ShaktiDark
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBn) "রাস্তা দেখুন" else "Route",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // ইন-অ্যাপ নেভিগেশন
                Button(
                    onClick = onInAppNavClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShaktiRed,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBn) "নেভিগেশন" else "Navigate",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // বিস্তারিত দেখুন
                IconButton(
                    onClick = onDetailClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Details", tint = ShaktiDark)
                }
            }
        }
    }
}

data class TimelineItem(
    val era: String,
    val titleBn: String,
    val descriptionBn: String,
    val placeId: String
)

@Composable
private fun TimelineCard(
    item: TimelineItem,
    isBn: Boolean,
    onOpenPlace: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Surface(
                    color = ShaktiRed,
                    shape = CircleShape,
                    modifier = Modifier.size(12.dp)
                ) {}
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                        .background(ShaktiGoldLight)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = ShaktiGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.era,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = ShaktiRedDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.titleBn,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ShaktiDark
                )

                Text(
                    text = item.descriptionBn,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onOpenPlace,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Text(
                        text = if (isBn) "সম্পর্কিত মন্দির বিস্তারিত দেখুন →" else "View Related Temple →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShaktiRed
                    )
                }
            }
        }
    }
}

private fun openExternalUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback
    }
}
