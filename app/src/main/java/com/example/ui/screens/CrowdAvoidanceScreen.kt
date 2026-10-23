package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CrowdReport
import com.example.data.model.CrowdStatus
import com.example.data.repository.SantipurRepository
import com.example.ui.components.AlpanaDivider
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SantipurViewModel

enum class VisitPreference(val labelBn: String, val labelEn: String, val descBn: String, val descEn: String) {
    PEACEFUL(
        "ভিড়মুক্ত শান্ত পরিবেশ",
        "Peaceful / Low Crowd",
        "ভোর ও দুপুরের নিরিবিলি সময়ে নির্বিঘ্নে দর্শন ও আরতি",
        "Serene morning and early afternoon visits without heavy lines"
    ),
    BALANCED(
        "ভারসাম্যপূর্ণ ভ্রমণ",
        "Balanced Experience",
        "বিকেল ৪:৩০ থেকে সন্ধ্যা ৭:০০ টার মধ্যে মধ্যম ভিড়ে উৎসবমুখর দর্শন",
        "Festive ambiance with manageable wait times"
    ),
    MAIN_PROCESSION(
        "প্রধান শোভাযাত্রার অংশ",
        "Main Procession Thrill",
        "ভাঙা রাস ও নিশীথ কালীপুজোর বর্ণাঢ্য জনজোয়ারের সরাসরি অংশ নিন",
        "Experience the full energy of grand chariots and midnight crowds"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrowdAvoidanceScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val crowdReports by viewModel.crowdReports.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI

    var selectedPreference by remember { mutableStateOf(VisitPreference.PEACEFUL) }
    var showReportDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "ভিড় এড়িয়ে উৎসব ঘুরুন" else "Smart Crowd Avoidance Guide",
                        fontWeight = FontWeight.Bold,
                        color = Maroon900
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Maroon900)
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
                .testTag("crowd_avoidance_screen"),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Interactive Preference Selector
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = Maroon700)
                            Text(
                                text = if (isBn) "আপনার ভ্রমণের পছন্দ নির্ধারণ করুন" else "Select Your Visit Preference",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        VisitPreference.entries.forEach { pref ->
                            val isSelected = selectedPreference == pref
                            Surface(
                                onClick = { selectedPreference = pref },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Maroon100 else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = if (isSelected) ButtonDefaults.outlinedButtonBorder else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedPreference = pref },
                                        colors = RadioButtonDefaults.colors(selectedColor = Maroon700)
                                    )
                                    Column {
                                        Text(
                                            text = if (isBn) pref.labelBn else pref.labelEn,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Maroon900
                                        )
                                        Text(
                                            text = if (isBn) pref.descBn else pref.descEn,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Schedule Recommendation for Selected Preference
            item {
                Surface(
                    color = when (selectedPreference) {
                        VisitPreference.PEACEFUL -> StatusGreen.copy(alpha = 0.1f)
                        VisitPreference.BALANCED -> Gold100
                        VisitPreference.MAIN_PROCESSION -> Maroon100
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = when (selectedPreference) {
                                    VisitPreference.PEACEFUL -> StatusGreen
                                    VisitPreference.BALANCED -> Gold900
                                    VisitPreference.MAIN_PROCESSION -> Maroon900
                                }
                            )
                            Text(
                                text = if (isBn) "প্রস্তাবিত দর্শন সময় ও পথপরামর্শ" else "Recommended Timetable & Routes",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val recommendationText = when (selectedPreference) {
                            VisitPreference.PEACEFUL -> if (isBn)
                                "★ সেরা সময়: সকাল ৬:০০ - ৯:০০ এবং দুপুর ২:০০ - বিকেল ৪:০০।\n★ শান্ত স্থানসমূহ: শ্রী শ্রী অদ্বৈত পাঠ ও বাবলা আশ্রম, পটেশ্বরী মন্দির, মেজো গোপাল মন্দির।\n★ সুবিধা: বয়স্ক ও শিশুদের নিয়ে কোনো ঠেলাঠেলি ছাড়া দেববিগ্রহের কাছাকাছি দর্শন সম্ভব।"
                            else
                                "★ Best Hours: 6:00 AM - 9:00 AM & 2:00 PM - 4:00 PM.\n★ Recommended spots: Advaita Paath Babla, Poteswari Mandir, Mejo Gopal.\n★ Advantage: Effortless darshan close to sanctums, ideal for seniors and families."

                            VisitPreference.BALANCED -> if (isBn)
                                "★ সেরা সময়: বিকেল ৪:৩০ - সন্ধ্যা ৭:০০।\n★ প্রস্তাবিত স্থান: শ্যামচাঁদ মন্দির ও বড় গোস্বামী বাড়ি।\n★ সুবিধা: সন্ধ্যার ভোগারতি, আলোকিত তোরণ ও মনোরম পদচারণা সহ মধ্যম ভিড়।"
                            else
                                "★ Best Hours: 4:30 PM - 7:00 PM.\n★ Recommended spots: Shyamchand Mandir & Boro Goswami Bari.\n★ Advantage: Evening Arati and temple illuminations with moderate waiting times."

                            VisitPreference.MAIN_PROCESSION -> if (isBn)
                                "★ সেরা সময়: রাত ৮:০০ - মধ্যরাত ১:০০ (ভাঙা রাস ও কালীপুজো)।\n★ প্রস্তাবিত স্থান: তোপখানা মোড় ও সুত্রাগড় সার্বজনীন গ্রাউন্ড।\n★ পরামর্শ: ব্যক্তিগত গাড়ি সুত্রাগড় হাই স্কুল মাঠে রেখে পদব্রজে এগিয়ে যান।"
                            else
                                "★ Best Hours: 8:00 PM - 1:00 AM.\n★ Recommended spots: Topkhana More & Sutragarh Sarbajanin.\n★ Advice: Park at Sutragarh High School and walk along pedestrian corridors."
                        }

                        Text(
                            text = recommendationText,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 20.sp,
                            color = Maroon900
                        )
                    }
                }
            }

            // 3. Peak Hours vs Quiet Hours Timetable
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isBn) "শান্তিপুর শহরের উৎসবকালীন ভিড়ের গতিবিধি" else "Crowd Density Timetable Across the Day",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Maroon900
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val timetable = listOf(
                            Triple("ভোর ৬টা - সকাল ৯টা (Morning)", CrowdStatus.LOW, "১০ মিনিট অপেক্ষা"),
                            Triple("সকাল ১০টা - দুপুর ১টা (Midday)", CrowdStatus.MODERATE, "২০-৩০ মিনিট অপেক্ষা"),
                            Triple("দুপুর ১টা - বিকেল ৪টা (Afternoon)", CrowdStatus.LOW, "স্বাভাবিক প্রবাহ"),
                            Triple("বিকেল ৪টা - রাত ৭টা (Evening)", CrowdStatus.MODERATE, "২৫-৩৫ মিনিট অপেক্ষা"),
                            Triple("রাত ৭টা - মধ্যরাত ১২টা (Night Peak)", CrowdStatus.HIGH, "৪০-৬০ মিনিট দীর্ঘ লাইন")
                        )

                        timetable.forEach { (time, status, wait) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = time,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )

                                val (color, bg) = when (status) {
                                    CrowdStatus.LOW -> StatusGreen to StatusGreen.copy(alpha = 0.15f)
                                    CrowdStatus.MODERATE -> StatusAmber to StatusAmber.copy(alpha = 0.15f)
                                    CrowdStatus.HIGH -> StatusRed to StatusRed.copy(alpha = 0.15f)
                                    else -> Color.Gray to Color.LightGray
                                }

                                Surface(
                                    color = bg,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (isBn) status.labelBn else status.labelEn,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = color,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            // 4. Live Community Crowd Reports
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBn) "লাইভ ভিড় পর্যবেক্ষণ ও দর্শনার্থী রিপোর্ট" else "Live Community Crowd Reports",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Maroon900
                        )
                        Text(
                            text = if (isBn) "স্বেচ্ছাসেবী ও ভক্তদের সাম্প্রতিক আপডেট" else "Updates from volunteers & visitors",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledTonalButton(
                        onClick = { showReportDialog = true },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBn) "রিপোর্ট দিন" else "Report", fontSize = 11.sp)
                    }
                }
            }

            items(crowdReports) { report ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBn) report.placeNameBn else report.placeNameEn,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )

                            val (color, bg) = when (report.status) {
                                CrowdStatus.LOW -> StatusGreen to StatusGreen.copy(alpha = 0.15f)
                                CrowdStatus.MODERATE -> StatusAmber to StatusAmber.copy(alpha = 0.15f)
                                CrowdStatus.HIGH -> StatusRed to StatusRed.copy(alpha = 0.15f)
                                else -> Color.Gray to Color.LightGray
                            }

                            Surface(color = bg, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = if (isBn) report.status.labelBn else report.status.labelEn,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = color,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = report.notesBn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "অপেক্ষা: ~${report.waitTimeMinutes} মিনিট",
                                style = MaterialTheme.typography.labelSmall,
                                color = Gold900,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${report.submittedBy} • ${report.timestampFormatted}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showReportDialog) {
        val allPlaces = remember { SantipurRepository.getAllPlaces() }
        var selectedPlaceId by remember { mutableStateOf(allPlaces.first().id) }
        var selectedStatus by remember { mutableStateOf(CrowdStatus.MODERATE) }
        var waitTime by remember { mutableStateOf("15") }
        var notes by remember { mutableStateOf("") }
        var author by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = {
                Text(
                    text = if (isBn) "ভিড় সংক্রান্ত তথ্য জমা দিন" else "Submit Crowd Report",
                    fontWeight = FontWeight.Bold,
                    color = Maroon900
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isBn) "স্থান নির্বাচন করুন:" else "Select Location:",
                        style = MaterialTheme.typography.bodySmall
                    )

                    var expanded by remember { mutableStateOf(false) }
                    val currentSelectedPlace = allPlaces.find { it.id == selectedPlaceId } ?: allPlaces.first()

                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isBn) currentSelectedPlace.nameBn else currentSelectedPlace.nameEn,
                                maxLines = 1
                            )
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            allPlaces.forEach { pl ->
                                DropdownMenuItem(
                                    text = { Text(if (isBn) pl.nameBn else pl.nameEn) },
                                    onClick = {
                                        selectedPlaceId = pl.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

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
                        label = { Text(if (isBn) "আনুমানিক অপেক্ষার মিনিট" else "Wait time (minutes)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text(if (isBn) "মন্তব্য (যেমন: শান্ত পরিবেশ)" else "Notes") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text(if (isBn) "আপনার নাম (ঐচ্ছিক)" else "Your name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val waitInt = waitTime.toIntOrNull() ?: 10
                        viewModel.submitCrowdReport(selectedPlaceId, selectedStatus, waitInt, notes, author)
                        showReportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Maroon700)
                ) {
                    Text(if (isBn) "জমা দিন" else "Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text(if (isBn) "বাতিল" else "Cancel")
                }
            }
        )
    }
}
