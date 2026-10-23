package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
fun RashYatraScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val savedIds by viewModel.savedPlaceIds.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI
    val context = LocalContext.current

    val rashPlaces = remember {
        SantipurRepository.getAllPlaces().filter { it.isRashParticipant }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "শান্তিপুর রাসযাত্রা বিশেষ" else "Santipur Rash Yatra Special",
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
                    containerColor = Maroon900
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("rash_yatra_screen"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Signature Hero Banner in Royal Gold & Maroon
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Maroon900, Maroon800, Color(0xFF5E0F20))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Surface(
                            color = Gold500,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isBn) "শান্তিপুরের রাস উৎসব ও মেলা" else "SANTIPUR RASOTSAV",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Maroon900,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "“শান্তিপুরের রাস — ঐতিহ্য, ভক্তি ও বাংলার গর্ব”",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Gold300,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isBn)
                                "পঞ্চদশ শতাব্দীতে শ্রী অদ্বৈত আচার্য প্রবর্তিত ৫৪০ বছরের ঐতিহ্যবাহী বৈষ্ণব প্রেমধর্মের মহা মিলনমেলা। শান্তিপুরের রাস পূর্ণিমা ও বিশ্বখ্যাত 'ভাঙা রাস' বাংলার আধ্যাত্মিক ও সাংস্কৃতিক অহঙ্কার।"
                            else
                                "Tracing over 540 years to Sri Advaita Acharya, Santipur Rash Yatra is Bengal's ultimate confluence of Radha-Krishna devotion, historic wooden chariots, and timeless weaving heritage.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarmCream.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        AlpanaDivider(color = Gold400.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // Key Timings and Countdown Info Card
                        Surface(
                            color = Color.Black.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (isBn) "রাস পূর্ণিমা" else "Rash Purnima",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Gold300
                                    )
                                    Text(
                                        text = if (isBn) "দিন ১: অধিবাস" else "Day 1: Adhibas",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                VerticalDivider(modifier = Modifier.height(30.dp), color = Gold500.copy(alpha = 0.3f))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (isBn) "ভাঙা রাস" else "Bhanga Rash",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Gold300
                                    )
                                    Text(
                                        text = if (isBn) "দিন ৩: রথ পরিক্রমা" else "Day 3: Chariot Parade",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                VerticalDivider(modifier = Modifier.height(30.dp), color = Gold500.copy(alpha = 0.3f))
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (isBn) "ঐতিহাসিক মেলা" else "Rash Mela",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Gold300
                                    )
                                    Text(
                                        text = if (isBn) "৭ দিনব্যাপী" else "7-Day Carnival",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. The Tradition of Bhanga Rash (ভাঙা রাস)
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
                            Icon(Icons.Default.Celebration, contentDescription = null, tint = Gold700)
                            Text(
                                text = if (isBn) "ভাঙা রাস: শান্তিপুরের অনন্য বিশ্ব ঐতিহ্য" else "Bhanga Rash: The Signature Phenomenon",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isBn)
                                "রাসবৈভবের তৃতীয় দিন অর্থাৎ 'ভাঙা রাস'-এ শান্তিপুরের বিভিন্ন মন্দির (যেমন বড় গোস্বামী বাড়ি, ছোট গোস্বামী বাড়ি, শ্রী শ্রী রাধারমণ, মদনগোপাল ও শ্যামচাঁদ) থেকে দেববিগ্রহসমূহ সুসজ্জিত কাষ্ঠনির্মিত রথে আরোহণ করে গোটা শান্তিপুর শহর পরিক্রমা করে। শোভাযাত্রার পুরোভাগে থাকে সনাতনী ঢাক-ঢোল, খোল-করতাল এবং লোকশিল্পের জীবন্ত রূপ 'সঙ' (Folk mime and tableau)。"
                            else
                                "On the 3rd day, known as 'Bhanga Rash', divine deities from premier Goswami houses and historic temples board monumental carved wooden chariots (Rath) parading along designated arterial pilgrim corridors. The procession is preceded by traditional Kirtan groups, dhak drummers, and the celebrated Santipuri 'Song' (living folk mime performances).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.navigateTo(AppScreen.ROUTE_PLANNER) },
                                colors = ButtonDefaults.buttonColors(containerColor = Maroon700),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Route, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBn) "শোভাযাত্রার রুট দেখুন" else "View Parade Route", fontSize = 12.sp)
                            }

                            FilledTonalButton(
                                onClick = { viewModel.navigateTo(AppScreen.INTERACTIVE_MAP) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBn) "ম্যাপে দর্শন করুন" else "Map View", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 3. Official Crowd & Spectator Advice
            item {
                Surface(
                    color = Gold100,
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
                            Icon(Icons.Default.Security, contentDescription = null, tint = Maroon900)
                            Text(
                                text = if (isBn) "ভাঙা রাস দর্শন ও নিরাপত্তা পরামর্শ" else "Spectator Guidelines & Crowd Advice",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val advicePoints = if (isBn) listOf(
                            "রথ শোভাযাত্রা বিকেলে শুরু হয়ে গভীর রাত পর্যন্ত চলে; বয়স্কদের জন্য তোপখানা মোড়ের পরিবর্তে গোস্বামী পাড়ায় বিকেল ৪:৩০-এ শান্তভাবে দর্শন সুপরামর্শযোগ্য।",
                            "সুত্রাগড় বাজার সংলগ্ন রাস্তায় তীব্র ভিড় হওয়ায় বিকল্প হাঁটা পথ (বাইপাস লেন) ব্যবহার করুন।",
                            "শান্তিপুর পাবলিক লাইব্রেরি ও পৌর স্কুল প্রাঙ্গণে ফার্স্ট এইড ও হারানো-প্রাপ্তি সহায়তা বুথ সক্রিয় রয়েছে।"
                        ) else listOf(
                            "Chariot procession runs from late afternoon until midnight; seniors are advised to visit Goswami Para between 4:00 PM and 5:30 PM for a serene darshan before peak hours.",
                            "Avoid bringing personal two-wheelers into Netaji Subhas Road; use designated parking at Sutragarh High School Ground.",
                            "First-aid and lost & found helplines are available at Santipur Public Library and Municipal Booths."
                        )

                        for (adv in advicePoints) {
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("•", fontWeight = FontWeight.Bold, color = Maroon700)
                                Text(
                                    text = adv,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Maroon900
                                )
                            }
                        }
                    }
                }
            }

            // 4. Participating Temples & Historic Shrines
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "রাসযাত্রায় অংশগ্রহণকারী প্রধান মন্দিরসমূহ" else "Participating Rash Temples & Shrines",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Maroon900
                    )
                    Text(
                        text = "${rashPlaces.size} ${if (isBn) "টি স্থান" else "places"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Gold900
                    )
                }
            }

            items(rashPlaces) { place ->
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
