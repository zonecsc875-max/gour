package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
                    containerColor = ShaktiDark
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ShaktiCream)
                .testTag("kali_puja_screen"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Featured Event Card: মা বামাকালীর বিখ্যাত নৃত্য (৯ নভেম্বর ২০২৬, বেজপাড়া মাঠ)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { viewModel.navigateTo(AppScreen.BAMAKALI_DANCE_EVENT) }
                        .testTag("featured_bamakali_dance_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ShaktiDark),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(ShaktiDark, ShaktiDarkSurface, ShaktiRedDark)
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = ShaktiRed,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (isBn) "বিশেষ বার্ষিক অনুষ্ঠান" else "SPECIAL ANNUAL EVENT",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Surface(
                                    color = ShaktiGold,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (isBn) "স্থানান্তরিত: বেজপাড়া মাঠ" else "New Venue: Bejpara",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ShaktiDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "মা বামাকালীর বিখ্যাত নৃত্য",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = ShaktiGold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Event, contentDescription = null, tint = Gold300, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "৯ নভেম্বর ২০২৬, সোমবার • রাত ৮:০০ টা",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Gold100
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Gold400, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "বেজপাড়া মাঠ, শান্তিপুর, নদীয়া (পূর্বের স্থান: বড়বাজার মোড়)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ShaktiCream
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "বিস্তারিত ও ঘোষণা দেখতে ট্যাপ করুন →",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ShaktiGoldLight
                                )

                                Button(
                                    onClick = { viewModel.navigateTo(AppScreen.BAMAKALI_DANCE_EVENT) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ShaktiGold,
                                        contentColor = ShaktiDark
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text(
                                        text = if (isBn) "অনুষ্ঠান দেখুন" else "View Event",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Vermilion & Deep Maroon Night Atmosphere Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(ShaktiDark, Color(0xFF4A0A14), ShaktiRedDark)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Surface(
                            color = ShaktiRed,
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
                            text = "শান্তিপুরের কালীপুজো — আগমেশ্বরী ও ঐতিহ্যবাহী কালীসমূহ",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Gold200,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isBn)
                                "মহাতন্ত্রসাধক কৃষ্ণানন্দ আগমবাগীশের পরম্পরায় ধন্য শান্তিপুরের শাক্ত ঐতিহ্যের মূল ধারা। দীপান্বিতা অমাবস্যার নিশুতি রাতে হাজার হাজার মাটির প্রদীপের মায়াবী আলোয় সমগ্র শান্তিপুর আলোকিত হয়ে ওঠে।"
                            else
                                "Sanctified by Tantric master Krishnananda Agambagish, Santipur harbors an extraordinary nocturnal Shakti tradition. On Diwali night, thousands of clay lamps illume sacred temples during midnight worship.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ShaktiCream.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        AlpanaDivider(color = ShaktiGold.copy(alpha = 0.6f))
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
                                        text = if (isBn) "বেজপাড়া মাঠ • বিখ্যাত নৃত্য ও তান্ত্রিক আরতি" else "Bejpara Math • Famous Dance & Night Arati",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = WarmCream.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. User Requested Official Disclaimer Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ShaktiGoldLight.copy(alpha = 0.45f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = ShaktiRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "শান্তিপুরে প্রায় ৪০০ টির বেশি কালী পূজা হয় , সব পূজার বিবরণ আমাদের পক্ষে এখন দেয়া সম্ভব হচ্ছে না । লোকমুখে প্রসিদ্ধ এবং বিখ্যাত কালী পূজা গুলি app এ দেওয়া আছে ধন্যবাদ ----",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = ShaktiDark,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // 4. Safe Night Walking & Crowd Guidance
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.NightlightRound, contentDescription = null, tint = ShaktiRed)
                            Text(
                                text = if (isBn) "নিশীথ পরিক্রমা ও ভিড় এড়ানোর টিপস" else "Night Hopping & Crowd Avoidance Tips",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ShaktiDark
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val tips = if (isBn) listOf(
                            "আগমেশ্বরী মন্দিরে রাত ১০:৩০ থেকে রাত ১:০০ টা পর্যন্ত সর্বাধিক ভিড় থাকে; শান্ত দর্শনের জন্য রাত ১:৩০ এর পর অথবা ভোর ৫:০০ - সকাল ৭:০০ টা নির্বাচন করুন।",
                            "বামাকালী মাতার বিখ্যাত নৃত্য ৯ নভেম্বর রাত ৮টায় বেজপাড়া মাঠে অনুষ্ঠিত হবে। নির্ধারিত সময়ের পূর্বেই পৌঁছানো বাঞ্ছনীয়।",
                            "পরিবার ও শিশুদের নিয়ে ঘোরার জন্য সুত্রাগড় হাই স্কুল মাঠে গাড়ি পার্ক করে টোটো বা পদব্রজে যাওয়া নিরাপদ ও সময়সাশ্রয়ী।"
                        ) else listOf(
                            "Agameswari Mandir experiences peak lines between 10:30 PM and 1:00 AM; visit after 1:30 AM or early morning for peaceful darshan.",
                            "Maa Bamakali's legendary dance takes place at Bejpara Math on 9 November at 8:00 PM; arrive early for convenient viewing.",
                            "Park private vehicles at Sutragarh High School ground and proceed on foot or by local Toto."
                        )

                        for (tip in tips) {
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("•", fontWeight = FontWeight.Bold, color = ShaktiRed)
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
                            colors = ButtonDefaults.buttonColors(containerColor = ShaktiRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Route, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBn) "কালীপুজো নিশীথ পরিক্রমা পথ দেখুন" else "View Midnight Kali Trail", fontSize = 12.sp)
                        }
                    }
                }
            }

            // 5. 19 Famous Historic Kali Pujas in Santipur
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBn) "শান্তিপুরের বিখ্যাত ১৯টি কালী পূজা" else "19 Famous Historic Kali Pujas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ShaktiDark
                        )
                        Text(
                            text = if (isBn) "লোকমুখে প্রসিদ্ধ ও ঐতিহ্যবাহী পূজা তালিকা" else "Renowned traditional shrines in Santipur",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }

                    Surface(
                        color = ShaktiRed.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${kaliPlaces.size} ${if (isBn) "টি স্থান" else "places"}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = ShaktiRed,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
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
