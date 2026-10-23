package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SantipurRepository
import com.example.ui.components.AlpanaDivider
import com.example.ui.components.PlaceCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SantipurViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaliPujaScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val savedIds by viewModel.savedPlaceIds.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI

    val kaliPlaces = remember {
        SantipurRepository.getAllPlaces().filter { it.isKaliSpecial }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "শান্তিপুর কালীপুজো বিশেষ" else "Santipur Kali Puja Special",
                        fontWeight = FontWeight.Bold,
                        color = Gold100
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Gold100
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF380812)
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("kali_puja_screen"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Vermilion & Deep Maroon Night Atmosphere Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF380812), Color(0xFF5A0B18), VermilionDark)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Surface(
                            color = VermilionLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isBn) "মহাশাক্ত সাধনা ও আলোর উৎসব" else "SHAKTI & DIWALI FESTIVAL",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "শান্তিপুরের কালীপুজো — আগমেশ্বরী ও বামাকালী",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Gold200,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isBn)
                                "মহাতন্ত্রসাধক সর্বানন্দ ও কৃষ্ণানন্দ আগমবাগীশের পরম্পরায় ধন্য শান্তিপুরের শাক্ত ঐতিহ্যের মূল ধারা। দীপান্বিতা অমাবস্যার নিশুতি রাতে হাজার হাজার মাটির প্রদীপের মায়াবী আলোয় সমগ্র শান্তিপুর আলোকিত হয়ে ওঠে।"
                            else
                                "Sanctified by Tantric master Krishnananda Agambagish, Santipur harbors an extraordinary nocturnal Shakti tradition. On Diwali night, thousands of clay lamps illume sacred temples during midnight worship.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarmCream.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        AlpanaDivider(color = Gold400.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Key Deities Highlights
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = Color.Black.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = if (isBn) "মা আগমেশ্বরী" else "Maa Agameswari",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Gold300
                                    )
                                    Text(
                                        text = if (isBn) "সুত্রাগড় আগমেশ্বরী তলা • বিশালাকার মৃণ্ময়ী রূপ" else "Sutragarh • Monumental Clay Vigraha",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = WarmCream.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            Surface(
                                color = Color.Black.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = if (isBn) "মা বামাকালী" else "Maa Bamakali",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Gold300
                                    )
                                    Text(
                                        text = if (isBn) "বামাকালী তলা • ঐতিহ্যবাহী রক্তজবা আরতি" else "Bamakali Tala • Historic Night Arati",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = WarmCream.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Safe Night Walking & Crowd Guidance
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
                            Icon(Icons.Default.NightlightRound, contentDescription = null, tint = VermilionRed)
                            Text(
                                text = if (isBn) "নিশীথ পরিক্রমা ও ভিড় এড়ানোর টিপস" else "Night Hopping & Crowd Avoidance Tips",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val tips = if (isBn) listOf(
                            "আগমেশ্বরী মন্দিরে রাত ১০:৩০ থেকে রাত ১:০০ টা পর্যন্ত সর্বাধিক ভিড় থাকে; শান্ত দর্শনের জন্য রাত ১:৩০ এর পর অথবা ভোর ৫:০০ - সকাল ৭:০০ টা নির্বাচন করুন।",
                            "বামাকালী মন্দির থেকে শ্যামচাঁদ মোড় পর্যন্ত পর্যাপ্ত আলোর ব্যবস্থা ও পুলিশ পিকেট মোতায়েন থাকবে।",
                            "পরিবার ও শিশুদের নিয়ে ঘোরার জন্য সুত্রাগড় হাই স্কুল মাঠে গাড়ি পার্ক করে টোটো বা পদব্রজে যাওয়া নিরাপদ ও সময়সাশ্রয়ী।"
                        ) else listOf(
                            "Agameswari Mandir experiences peak lines between 10:30 PM and 1:00 AM; visit after 1:30 AM or between 5:00 AM and 7:00 AM for peaceful darshan.",
                            "The illuminated pedestrian zone between Bamakali and Shyamchand is monitored with CCTV and police helpdesks.",
                            "Park private vehicles at Sutragarh High School ground and proceed on foot or by local Toto to avoid narrow lane bottlenecks."
                        )

                        for (tip in tips) {
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("•", fontWeight = FontWeight.Bold, color = VermilionRed)
                                Text(
                                    text = tip,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.navigateTo(AppScreen.ROUTE_PLANNER) },
                            colors = ButtonDefaults.buttonColors(containerColor = VermilionDark),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Route, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBn) "কালীপুজো নিশীথ পরিক্রমা পথ দেখুন" else "View Midnight Kali Trail", fontSize = 12.sp)
                        }
                    }
                }
            }

            // 3. Participating Shrines & Mandir Directory
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "কালীপুজোর প্রধান শাক্ত মন্দিরসমূহ" else "Key Kali Puja Temples & Shrines",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Maroon900
                    )
                    Text(
                        text = "${kaliPlaces.size} ${if (isBn) "টি স্থান" else "places"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = VermilionRed
                    )
                }
            }

            items(kaliPlaces) { place ->
                val dist = viewModel.getDistanceToPlace(place)
                PlaceCard(
                    place = place,
                    distanceKm = dist,
                    isSaved = savedIds.contains(place.id),
                    language = uiState.language,
                    onCardClick = { viewModel.openPlaceDetail(place.id) },
                    onSaveToggle = { viewModel.toggleSavePlace(place.id) },
                    onNavigateClick = { viewModel.startInAppNavigation(place.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}
