package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CrowdStatus
import com.example.data.repository.SantipurRepository
import com.example.ui.components.AlpanaDivider
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SantipurViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val savedIds by viewModel.savedPlaceIds.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI
    val context = LocalContext.current

    val placeId = uiState.selectedPlaceId ?: "boro_goswami"
    val place = remember(placeId) {
        SantipurRepository.getAllPlaces().find { it.id == placeId }
            ?: SantipurRepository.getAllPlaces().first()
    }

    val isSaved = savedIds.contains(place.id)
    var showCrowdDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) place.nameBn else place.nameEn,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold,
                        color = Maroon900
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Maroon900
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleSavePlace(place.id) }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isSaved) Gold700 else Maroon900
                        )
                    }
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "${place.nameBn} (${place.nameEn})\n${place.address}\nGPS: ${place.latitude}, ${place.longitude}\nhttps://maps.google.com/?q=${place.latitude},${place.longitude}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Place"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Maroon900)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("place_detail_screen"),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Hero Header Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Maroon900, Maroon800, Maroon700)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Gold500,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isBn) place.category.titleBn else place.category.titleEn,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Maroon900,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            if (place.isVerified) {
                                Surface(
                                    color = StatusGreen.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.Verified, contentDescription = null, tint = Gold200, modifier = Modifier.size(14.dp))
                                        Text(
                                            text = if (isBn) "যাচাইকৃত (${place.lastVerifiedDate})" else "Verified (${place.lastVerifiedDate})",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Gold100,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isBn) place.nameBn else place.nameEn,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Gold100
                        )

                        Text(
                            text = if (isBn) place.nameEn else place.nameBn,
                            style = MaterialTheme.typography.titleSmall,
                            color = Gold300
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Gold400, modifier = Modifier.size(16.dp))
                            Text(
                                text = place.address,
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmCream
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        AlpanaDivider(color = Gold400.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // In-App Turn-by-Turn Navigation Button
                        Button(
                            onClick = { viewModel.startInAppNavigation(place.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Gold500,
                                contentColor = Maroon900
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("detail_in_app_navigate_button")
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBn) "অ্যাপে লাইভ জিপিএস পথনির্দেশ শুরু করুন" else "Start In-App Live Navigation",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                val uri = Uri.parse("geo:${place.latitude},${place.longitude}?q=${place.latitude},${place.longitude}(${Uri.encode(place.nameEn)})")
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold200),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp), tint = Gold200)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBn) "গুগল ম্যাপে ব্যাকআপ খুলুন" else "Open in Google Maps",
                                fontSize = 12.sp,
                                color = Gold200
                            )
                        }
                    }
                }
            }

            // 2. Quick Key Stats (Deity, Timings, Era, Distance)
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.TempleHindu, contentDescription = null, tint = Maroon700, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(if (isBn) "প্রধান বিগ্রহ" else "Deity", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(
                                text = if (isBn) place.deityBn else place.deityEn,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )
                        }

                        VerticalDivider(modifier = Modifier.height(40.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = Maroon700, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(if (isBn) "প্রতিষ্ঠাকাল" else "Established", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(
                                text = place.establishedYear,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )
                        }

                        VerticalDivider(modifier = Modifier.height(40.dp))

                        val dist = viewModel.getDistanceToPlace(place)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = Gold700, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(if (isBn) "দূরত্ব" else "Distance", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(
                                text = if (isBn) "${dist} কিমি" else "${dist} km",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Gold900
                            )
                        }
                    }
                }
            }

            // 3. Cultural & Historical Significance
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.HistoryEdu, contentDescription = null, tint = Maroon700)
                            Text(
                                text = if (isBn) "ঐতিহাসিক পটভূমি ও সাংস্কৃতিক গুরুত্ব" else "Historical Significance & Heritage",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isBn) place.descriptionBn else place.descriptionEn,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // 4. Visiting Hours & Darshan
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = Gold700)
                            Text(
                                text = if (isBn) "দর্শনের সময়সূচী ও প্রবেশ তথ্য" else "Visiting Hours & Entry Info",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isBn) "দর্শনের সময়:" else "Darshan Timings:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Maroon800
                        )
                        Text(
                            text = place.darshanTimings,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isBn) "প্রবেশ মূল্য:" else "Entry Fee:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Maroon800
                        )
                        Text(
                            text = if (isBn) "বিনামূল্যে সার্বজনীন দর্শন (Free Entry for All)" else "Free Public Darshan for Everyone",
                            style = MaterialTheme.typography.bodyMedium,
                            color = StatusGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 5. Parking, Transport & Accessibility Guidance
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Maroon700)
                            Text(
                                text = if (isBn) "যাতায়াত, পার্কিং ও সুগম্যতা" else "Transit, Parking & Accessibility",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Parking
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.LocalParking, contentDescription = null, tint = Gold900, modifier = Modifier.size(18.dp))
                            Column {
                                Text(
                                    text = if (isBn) "নিকটতম পার্কিং:" else "Nearest Parking:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isBn) place.nearestParkingBn else place.nearestParkingEn,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Transport
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = Maroon700, modifier = Modifier.size(18.dp))
                            Column {
                                Text(
                                    text = if (isBn) "গণপরিবহন ও টোটো:" else "Transit & Toto:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isBn) place.nearestTransportBn else place.nearestTransportEn,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Accessibility
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Accessible, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(18.dp))
                            Column {
                                Text(
                                    text = if (isBn) "সুগম্যতা ও প্রবীণবান্ধব ব্যবস্থা:" else "Accessibility & Senior Friendly:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isBn) place.accessibilityNotesBn else place.accessibilityNotesEn,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // 6. Action Bar: Add to Custom Itinerary, Report Crowd, Submit Correction
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = { viewModel.addToCustomItinerary(place.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AddRoad, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBn) "আমার ভ্রমণ পরিক্রমায় যোগ করুন" else "Add to My Tour Itinerary")
                    }

                    OutlinedButton(
                        onClick = { showCrowdDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.GroupAdd, contentDescription = null, tint = StatusAmber)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBn) "বর্তমান ভিড় সংক্রান্ত রিপোর্ট জমা দিন" else "Submit Live Crowd Report")
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.CORRECTION_FORM) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBn) "কোনো তথ্য সংশোধন থাকলে জানান" else "Submit a Correction or Update")
                    }
                }
            }
        }
    }

    // Live Crowd Report Submission Modal Dialog
    if (showCrowdDialog) {
        var selectedStatus by remember { mutableStateOf(CrowdStatus.MODERATE) }
        var waitTime by remember { mutableStateOf("15") }
        var notes by remember { mutableStateOf("") }
        var author by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCrowdDialog = false },
            title = {
                Text(
                    text = if (isBn) "ভিড় সংক্রান্ত তথ্য জমা দিন" else "Report Live Crowd Level",
                    fontWeight = FontWeight.Bold,
                    color = Maroon900
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBn) place.nameBn else place.nameEn,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Maroon800
                    )

                    Text(
                        text = if (isBn) "ভিড়ের বর্তমান মাত্রা নির্বাচন করুন:" else "Select current crowd status:",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(CrowdStatus.LOW, CrowdStatus.MODERATE, CrowdStatus.HIGH).forEach { st ->
                            FilterChip(
                                selected = selectedStatus == st,
                                onClick = { selectedStatus = st },
                                label = { Text(if (isBn) st.labelBn else st.labelEn, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = waitTime,
                        onValueChange = { waitTime = it },
                        label = { Text(if (isBn) "আনুমানিক অপেক্ষার সময় (মিনিট)" else "Wait time (minutes)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text(if (isBn) "মন্তব্য (যেমন: লাইন স্বাভাবিক)" else "Notes (e.g. queue moving)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text(if (isBn) "আপনার নাম / পরিচয় (ঐচ্ছিক)" else "Your name (optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val waitInt = waitTime.toIntOrNull() ?: 10
                        viewModel.submitCrowdReport(place.id, selectedStatus, waitInt, notes, author)
                        showCrowdDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Maroon700)
                ) {
                    Text(if (isBn) "জমা দিন" else "Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCrowdDialog = false }) {
                    Text(if (isBn) "বাতিল" else "Cancel")
                }
            }
        )
    }
}
