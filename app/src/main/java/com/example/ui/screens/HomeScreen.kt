package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdvisorySeverity
import com.example.data.model.PlaceCategory
import com.example.data.repository.SantipurRepository
import com.example.ui.components.AlpanaDivider
import com.example.ui.components.PlaceCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SantipurViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val savedIds by viewModel.savedPlaceIds.collectAsState()
    val advisories by viewModel.advisories.collectAsState()

    val isBn = uiState.language == AppLanguage.BENGALI
    val allPlaces = remember { SantipurRepository.getAllPlaces() }
    val famousPlaces = remember { allPlaces.filter { it.isFamousHighlight } }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_column"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Bengali Cultural Hero Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Maroon900,
                                Maroon800,
                                Maroon700
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    // Top App Bar row with Language Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Gold500.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isBn) "শান্তিপুর • নদীয়া" else "SANTIPUR • NADIA",
                                style = MaterialTheme.typography.labelSmall,
                                color = Gold200,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        // Language Toggle Button
                        FilledTonalButton(
                            onClick = { viewModel.toggleLanguage() },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Gold500.copy(alpha = 0.25f),
                                contentColor = Gold100
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("language_toggle_button")
                        ) {
                            Icon(
                                Icons.Default.Language,
                                contentDescription = "Language",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBn) "English" else "বাংলা",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Grand Bengali Hero Heading
                    Text(
                        text = "উৎসবের শহর শান্তিপুর",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Gold200,
                        fontSize = 30.sp,
                        letterSpacing = 0.5.sp
                    )

                    Text(
                        text = "SANTIPUR FESTIVAL & HERITAGE GUIDE",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Gold400,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isBn)
                            "“শান্তিপুরের প্রতিটি উৎসব, এবার আপনার হাতের মুঠোয়”"
                        else
                            "“Every festival and temple of historic Santipur at your fingertips”",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmCream.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    AlpanaDivider(color = Gold400.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Prominent Search Bar
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = {
                            viewModel.setSearchQuery(it)
                            if (it.isNotBlank() && uiState.currentScreen != AppScreen.DIRECTORY) {
                                viewModel.navigateTo(AppScreen.DIRECTORY)
                            }
                        },
                        placeholder = {
                            Text(
                                text = if (isBn) "পুজো, মন্দির বা প্যান্ডেল খুঁজুন…" else "Search pujas, temples or pandals…",
                                color = Maroon900.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Maroon700
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Maroon700)
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = IvoryWhite,
                            unfocusedContainerColor = IvoryWhite,
                            focusedBorderColor = Gold500,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Maroon900,
                            unfocusedTextColor = Maroon900
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // GPS Location & Nearest Landmark Indicator
                    Surface(
                        color = Color.Black.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.MyLocation,
                                    contentDescription = null,
                                    tint = Gold400,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isBn) "অবস্থান: শান্তিপুর কেন্দ্র (২৩.২৫০৫° N)" else "Location: Santipur Central (23.2505° N)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarmCream
                                )
                            }

                            TextButton(
                                onClick = { viewModel.navigateTo(AppScreen.INTERACTIVE_MAP) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text(
                                    text = if (isBn) "ম্যাপে দেখুন" else "View Map",
                                    color = Gold300,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Active Road & Crowd Advisory Ticker
        if (advisories.isNotEmpty()) {
            item {
                val latest = advisories.first()
                val isRestr = latest.severity == AdvisorySeverity.RESTRICTION
                val bg = if (isRestr) VermilionRed.copy(alpha = 0.1f) else Gold100
                val border = if (isRestr) VermilionRed else Gold500

                Surface(
                    color = bg,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isRestr) Icons.Default.Warning else Icons.Default.Campaign,
                            contentDescription = "Alert",
                            tint = if (isRestr) VermilionRed else Gold900,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBn) latest.titleBn else latest.titleEn,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isRestr) VermilionDark else Gold900
                            )
                            Text(
                                text = if (isBn) latest.messageBn else latest.messageEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 3. Signature Featured Festival Banners (Rash Yatra & Kali Puja)
        item {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                PaddingValues(horizontal = 16.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "বিশেষ উৎসব আকর্ষণ" else "Featured Festival Specials",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Maroon900
                    )
                }

                // Rash Yatra Signature Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { viewModel.navigateTo(AppScreen.RASH_YATRA_SPECIAL) }
                        .testTag("featured_rash_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Maroon800)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Maroon900, Maroon800, Color(0xFF6B1728))
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
                                    color = Gold500,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (isBn) "স্বাক্ষরিত শ্রেষ্ঠ উৎসব" else "SIGNATURE FESTIVAL",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Maroon900,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Surface(
                                    color = Color.Black.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = if (isBn) "কার্তিক পূর্ণিমা তিথি" else "Kartik Purnima",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Gold200,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "শান্তিপুর রাসযাত্রা ও ভাঙা রাস",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Gold100
                            )

                            Text(
                                text = "“শান্তিপুরের রাস — ঐতিহ্য, ভক্তি ও বাংলার গর্ব”",
                                style = MaterialTheme.typography.bodySmall,
                                color = Gold400,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (isBn)
                                    "৫০০ বছরের সুপ্রাচীন বৈষ্ণব ঐতিহ্য, রাধারমণ জিউ-এর রাসলীলা এবং ঐতিহাসিক কাষ্ঠনির্মিত রথে ভাঙা রাস পরিক্রমা।"
                                else
                                    "500-year Vaishnava legacy, Radharaman Jiu's Rasalila, and the world-famous Bhanga Rash wooden chariot procession.",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmCream.copy(alpha = 0.85f),
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBn) "১৮টি ঐতিহ্যবাহী মন্দির ও রথপথ" else "18 verified temples & chariot routes",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gold200
                                )

                                Button(
                                    onClick = { viewModel.navigateTo(AppScreen.RASH_YATRA_SPECIAL) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Gold500,
                                        contentColor = Maroon900
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text(
                                        text = if (isBn) "রাস গাইড দেখুন" else "Explore Rash",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Kali Puja Feature Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { viewModel.navigateTo(AppScreen.KALI_PUJA_SPECIAL) }
                        .testTag("featured_kali_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = VermilionDark)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF4A0A10), VermilionDark, Color(0xFF6A0C16))
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
                                    color = VermilionLight,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (isBn) "মহাশাক্ত সাধনা" else "SHAKTI TRADITION",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Surface(
                                    color = Color.Black.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = if (isBn) "দীপান্বিতা অমাবস্যা" else "Diwali Amavasya",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Gold200,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "শান্তিপুরের কালীপুজো (আগমেশ্বরী ও বামাকালী)",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Gold100
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isBn)
                                    "আগমেশ্বরী ও বামাকালী মাতার নিশীথ আরাধনা, তান্ত্রিক আচার ও হাজারো প্রদীপের আলোয় নৈশ প্যান্ডেল পরিক্রমা।"
                                else
                                    "Midnight Tantric worship of Maa Agameswari and Bamakali, accompanied by thousands of earthen lamps.",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmCream.copy(alpha = 0.85f),
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBn) "২৪টি জাগ্রত শাক্ত মন্দির" else "24 verified Shakti shrines",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gold200
                                )

                                Button(
                                    onClick = { viewModel.navigateTo(AppScreen.KALI_PUJA_SPECIAL) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Gold400,
                                        contentColor = Maroon900
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text(
                                        text = if (isBn) "কালীপুজো গাইড" else "Explore Kali Puja",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Quick Action Hub (Navigation, Calendar, Routes, Crowd, Helplines)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = if (isBn) "উৎসব সহায়ক সেবা ও গাইড" else "Festival Navigation & Services",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Maroon900
                )

                Spacer(modifier = Modifier.height(12.dp))

                val hubActions = listOf(
                    HubItem(
                        icon = Icons.Default.Map,
                        titleBn = "ইন্টারেক্টিভ ম্যাপ",
                        titleEn = "Interactive Map",
                        descBn = "শহরের জিপিএস ম্যাপ",
                        descEn = "GPS Map of Santipur",
                        color = Color(0xFF1976D2),
                        screen = AppScreen.INTERACTIVE_MAP
                    ),
                    HubItem(
                        icon = Icons.Default.Groups,
                        titleBn = "ভিড় এড়িয়ে ভ্রমণ",
                        titleEn = "Crowd Avoidance",
                        descBn = "শান্ত সময়ে দর্শন",
                        descEn = "Peaceful visit times",
                        color = StatusGreen,
                        screen = AppScreen.CROWD_AVOIDANCE
                    ),
                    HubItem(
                        icon = Icons.Default.Route,
                        titleBn = "পরিক্রমা রুট",
                        titleEn = "Tour Routes",
                        descBn = "সেরা পরিক্রমা পথ",
                        descEn = "Curated Circuits",
                        color = Terracotta,
                        screen = AppScreen.ROUTE_PLANNER
                    ),
                    HubItem(
                        icon = Icons.Default.CalendarMonth,
                        titleBn = "উৎসব পঞ্জিকা",
                        titleEn = "Festival Calendar",
                        descBn = "তিথি ও সময়সূচী",
                        descEn = "Dates & Rituals",
                        color = Gold700,
                        screen = AppScreen.CALENDAR
                    ),
                    HubItem(
                        icon = Icons.Default.DirectionsBus,
                        titleBn = "পরিবহন ও পার্কিং",
                        titleEn = "Transit & Parking",
                        descBn = "ট্রেন, টোটো ও সুবিধা",
                        descEn = "Train, Toto, Parking",
                        color = Maroon700,
                        screen = AppScreen.TRANSPORT_FACILITIES
                    ),
                    HubItem(
                        icon = Icons.Default.MenuBook,
                        titleBn = "ঐতিহ্যের প্রবন্ধ",
                        titleEn = "Heritage Stories",
                        descBn = "শান্তিপুরের ইতিহাস",
                        descEn = "History & Handloom",
                        color = Color(0xFF5D4037),
                        screen = AppScreen.HERITAGE_ARTICLES
                    ),
                    HubItem(
                        icon = Icons.Default.Bookmark,
                        titleBn = "সংরক্ষিত স্থান",
                        titleEn = "Saved Places",
                        descBn = "আপনার প্রিয় পুজো",
                        descEn = "Favorite Pandals",
                        color = Gold900,
                        screen = AppScreen.SAVED_PLACES
                    ),
                    HubItem(
                        icon = Icons.Default.EditNote,
                        titleBn = "তথ্য সংশোধন",
                        titleEn = "Corrections",
                        descBn = "নতুন তথ্য জানান",
                        descEn = "Submit updates",
                        color = Color(0xFF00796B),
                        screen = AppScreen.CORRECTION_FORM
                    )
                )

                // 2-column Grid for Hub items
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in hubActions.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HubActionCard(
                                item = hubActions[i],
                                isBn = isBn,
                                onClick = { viewModel.navigateTo(hubActions[i].screen) },
                                modifier = Modifier.weight(1f)
                            )
                            if (i + 1 < hubActions.size) {
                                HubActionCard(
                                    item = hubActions[i + 1],
                                    isBn = isBn,
                                    onClick = { viewModel.navigateTo(hubActions[i + 1].screen) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. 14 Festival Categories Horizontal Explorer
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "উৎসব বিভাগসমূহ (১৪টি উৎসব)" else "Festival Categories (14 Categories)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Maroon900
                    )
                    TextButton(onClick = { viewModel.navigateTo(AppScreen.FESTIVALS_EXPLORER) }) {
                        Text(
                            text = if (isBn) "সবগুলি দেখুন" else "View All",
                            color = Maroon700,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                val allCategories = remember { SantipurRepository.getAllFestivals() }
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(allCategories) { fest ->
                        Card(
                            modifier = Modifier
                                .width(160.dp)
                                .clickable { viewModel.navigateTo(AppScreen.FESTIVALS_EXPLORER) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Surface(
                                    color = Maroon100,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Celebration,
                                        contentDescription = null,
                                        tint = Maroon700,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isBn) fest.nameBn else fest.nameEn,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Maroon900,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = fest.bengaliSeason,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. Famous Puja Pandals & Temples
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBn) "বিখ্যাত পুজো ও প্রাচীন ঐতিহ্যবাহী মন্দির" else "Famous Temples & Pandals",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Maroon900
                        )
                        Text(
                            text = if (isBn) "শান্তিপুরের যাচাইকৃত শ্রেষ্ঠ তীর্থ ও দর্শনীয় স্থান" else "Verified top heritage sites of Santipur",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(onClick = { viewModel.navigateTo(AppScreen.DIRECTORY) }) {
                        Text(text = if (isBn) "সম্পূর্ণ তালিকা" else "A-Z List", color = Maroon700, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Place Cards List
        items(famousPlaces) { place ->
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

        // 7. Footer: Admin & Emergency Quick Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Surface(
                    color = SoftSand,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.PhoneInTalk, contentDescription = null, tint = VermilionRed)
                                Text(
                                    text = if (isBn) "জরুরি সহায়তা ডিরেক্টরি" else "Emergency Helplines",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Maroon900
                                )
                            }
                            FilledTonalButton(
                                onClick = { viewModel.navigateTo(AppScreen.TRANSPORT_FACILITIES) },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text(if (isBn) "নম্বরসমূহ" else "Call", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isBn)
                                "শান্তিপুর থানা: ০৩৪৭৩-২৭৮০২৩ • হাসপাতাল জরুরি: ০৩৪৭৩-২৭৮০২৭ • সর্বভারতীয় জরুরি: ১১২"
                            else
                                "Police: 03473-278023 • Hospital: 03473-278027 • Emergency: 112",
                            style = MaterialTheme.typography.bodySmall,
                            color = Maroon800
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { viewModel.navigateTo(AppScreen.ADMIN_PANEL) }) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp), tint = Maroon700)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBn) "প্রশাসক প্যানেল (Admin)" else "Admin Panel", fontSize = 12.sp, color = Maroon700)
                    }
                    Text("•", color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                    TextButton(onClick = { viewModel.navigateTo(AppScreen.ABOUT_SANTIPUR) }) {
                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp), tint = Maroon700)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBn) "উদ্যোগ সম্পর্কে" else "About Project", fontSize = 12.sp, color = Maroon700)
                    }
                }
            }
        }
    }
}

private data class HubItem(
    val icon: ImageVector,
    val titleBn: String,
    val titleEn: String,
    val descBn: String,
    val descEn: String,
    val color: Color,
    val screen: AppScreen
)

@Composable
private fun HubActionCard(
    item: HubItem,
    isBn: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("hub_item_${item.screen.name}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                color = item.color.copy(alpha = 0.12f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.color,
                    modifier = Modifier.padding(9.dp)
                )
            }
            Column {
                Text(
                    text = if (isBn) item.titleBn else item.titleEn,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Maroon900,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isBn) item.descBn else item.descEn,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
