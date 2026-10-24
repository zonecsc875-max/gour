package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AlpanaDivider
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.SantipurViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaiRajaStoryScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI
    val scrollState = rememberScrollState()

    var activeChapter by remember { mutableIntStateOf(1) }
    val totalChapters = 4

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "রাই রাজা কাহিনী" else "The Story of Rai Raja",
                        fontWeight = FontWeight.Bold,
                        color = Gold100
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("rai_raja_back_btn")
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
                .verticalScroll(scrollState)
                .testTag("rai_raja_story_screen")
        ) {
            // 1. Royal Bengali Story Hero Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(ShaktiDark, ShaktiDarkSurface, ShaktiRedDark)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        color = ShaktiGold.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("👑", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBn) "শান্তিপুরের রাসের অনন্য ঐতিহ্য" else "Heritage Lore of Santipur",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = ShaktiGoldLight
                            )
                        }
                    }

                    Text(
                        text = "রাই রাজা",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = ShaktiGold,
                        fontSize = 36.sp,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "শান্তিপুরের রাসের এক অনন্য ঐতিহ্য",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gold200,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    AlpanaDivider(color = ShaktiGold.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "“রাসের আলোয়, ভক্তির আবহে—জীবন্ত হয়ে ওঠে শান্তিপুরের ঐতিহ্যের এক বিশেষ অধ্যায়।”",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ShaktiCream,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp
                    )
                }
            }

            // 2. Reading Progress Indicator & Chapter Selector
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBn) "অধ্যায় প্রগতি (Chapter Progress)" else "Chapter Progress",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ShaktiDark
                        )
                        Text(
                            text = "$activeChapter / $totalChapters",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = ShaktiRed
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { activeChapter.toFloat() / totalChapters.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = ShaktiRed,
                        trackColor = ShaktiGoldLight.copy(alpha = 0.4f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (ch in 1..totalChapters) {
                            val isSelected = ch == activeChapter
                            OutlinedButton(
                                onClick = { activeChapter = ch },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) ShaktiRed else Color.Transparent,
                                    contentColor = if (isSelected) Color.White else ShaktiDark
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("ch_btn_$ch")
                            ) {
                                Text(
                                    text = if (isBn) "অধ্যায় $ch" else "Ch $ch",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // 3. Cultural Disclaimer Box
            Surface(
                color = ShaktiGoldLight.copy(alpha = 0.35f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Research Note",
                        tint = ShaktiRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isBn)
                            "সম্পাদনা-সংক্রান্ত নোট: এই রীতির সুনির্দিষ্ট বিবরণ, ইতিহাস ও উৎপত্তি নির্ভরযোগ্য স্থানীয় সূত্র থেকে যাচাই না হওয়া পর্যন্ত একে স্থানীয়ভাবে প্রচলিত বর্ণনা হিসেবেই মর্যাদা দেওয়া হচ্ছে।"
                        else
                            "Editorial Note: Until precise origins and historical accounts are confirmed by scholarly local archives, this tradition is respectfully presented as verified local oral heritage.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ShaktiDark,
                        lineHeight = 18.sp
                    )
                }
            }

            // 4. Chapter Content Cards
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    when (activeChapter) {
                        1 -> {
                            Text(
                                text = "অধ্যায় ১ — রাসের শহরে এক বিশেষ পরম্পরা",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = ShaktiRedDark
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "শান্তিপুরের রাসযাত্রা শুধু দেবদেবীর আরাধনা নয়; এটি বহু প্রজন্ম ধরে বয়ে চলা এক সাংস্কৃতিক ঐতিহ্য। এই উৎসবের নানা আচার, শোভাযাত্রা ও ভক্তির আবহের মধ্যে ‘রাই রাজা’ নামের একটি বিশেষ পরম্পরার কথাও শোনা যায়।\n\nকার্তিক পূর্ণিমার পুণ্যলগ্নে নদীয়া জেলার গঙ্গাতীরবর্তী প্রাচীন শান্তিপুর নগরীতে বৈষ্ণব ভক্তির জোয়ার আসে। শতাব্দীর পর শতাব্দী ধরে গোস্বামী বাড়িগুলির নাটমন্দির, ঠাকুরদালান এবং সাধারণ মানুষের আন্তরিক মেলবন্ধনে তৈরি হয়েছে এক সমৃদ্ধ লোকসংস্কৃতি。",
                                style = MaterialTheme.typography.bodyLarge,
                                color = ShaktiDark,
                                lineHeight = 28.sp
                            )
                        }
                        2 -> {
                            Text(
                                text = "অধ্যায় ২ — রাই রাজার পরিচয়",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = ShaktiRedDark
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "স্থানীয়ভাবে প্রচলিত বর্ণনা অনুযায়ী, রাধার প্রতীকী রূপে এক কিশোরীকে সাজিয়ে বিশেষ সম্মানের সঙ্গে উপস্থাপন করার রীতি ‘রাই রাজা’ নামে পরিচিত। এই আচারকে ঘিরে রয়েছে ভক্তি, সাজসজ্জা এবং উৎসবের আবেগ।\n\nকিশোরীকে সনাতন রাজকীয় পোশাকে সজ্জিত করে পুষ্পভূষিত আসনে বসিয়ে ভক্তিভরে শ্রদ্ধা জানানো হয়, যা ভক্তদের দৃষ্টিতে রাধারাণীর মূর্ত প্রতীক হিসেবে অনুভূত হয়।\n\n*সম্পাদনা-সংক্রান্ত নোট: এই রীতির সুনির্দিষ্ট বিবরণ, ইতিহাস ও উৎপত্তি নির্ভরযোগ্য স্থানীয় সূত্র থেকে যাচাই না হওয়া পর্যন্ত একে স্থানীয়ভাবে প্রচলিত বর্ণনা হিসেবেই দেখাতে হবে।*",
                                style = MaterialTheme.typography.bodyLarge,
                                color = ShaktiDark,
                                lineHeight = 28.sp
                            )
                        }
                        3 -> {
                            Text(
                                text = "অধ্যায় ৩ — শোভাযাত্রা ও উৎসবের আবহ",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = ShaktiRedDark
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "সাজসজ্জা, আলো এবং ভক্তদের সমাগমের মধ্য দিয়ে রাসের শহর শান্তিপুরে তৈরি হয় এক বিশেষ উৎসবের পরিবেশ। রাই রাজার পরম্পরার নির্দিষ্ট রীতি ও শোভাযাত্রার বিবরণ স্থানীয়ভাবে যাচাই করে এই অধ্যায়ে পরম শ্রদ্ধার সঙ্গে তুলে ধরা হয়।\n\nহাজার হাজার ভক্তের কোলাহলে যখন মৃদঙ্গ ও করতাল বেজে ওঠে, তখন শান্তিপুরের অলিগলিতে ছড়িয়ে পড়ে অনুপম আধ্যাত্মিক সুবাস।",
                                style = MaterialTheme.typography.bodyLarge,
                                color = ShaktiDark,
                                lineHeight = 28.sp
                            )
                        }
                        4 -> {
                            Text(
                                text = "অধ্যায় ৪ — ঐতিহ্যকে জানি, সম্মান করি",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = ShaktiRedDark
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "ঐতিহ্যকে বাঁচিয়ে রাখার অর্থ শুধু তার ছবি তুলে রাখা নয়—তার ইতিহাস জানা, মানুষের অভিজ্ঞতা শোনা এবং পরবর্তী প্রজন্মের কাছে তার নির্ভুল পরিচয় পৌঁছে দেওয়া।\n\nশান্তিপুরের প্রতিটি প্রাচীন রীতির পেছনে রয়েছে শ্রদ্ধা ও সামাজিক সম্প্রীতির গভীর বাণী।",
                                style = MaterialTheme.typography.bodyLarge,
                                color = ShaktiDark,
                                lineHeight = 28.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Surface(
                                color = ShaktiRed.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "সমাপ্তি বার্তা",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = ShaktiRed
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "“শান্তিপুরের রাস—শুধু উৎসব নয়, প্রজন্ম থেকে প্রজন্মে বয়ে চলা এক সাংস্কৃতিক স্মৃতি।”",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ShaktiDark,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    AlpanaDivider(color = ShaktiGold.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Chapter Navigation Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (activeChapter > 1) {
                            OutlinedButton(
                                onClick = { activeChapter -= 1 },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ShaktiDark),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("পূর্ববর্তী অধ্যায়")
                            }
                        } else {
                            OutlinedButton(
                                onClick = { activeChapter = 1 },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ShaktiDark),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("গল্প শুরু করুন")
                            }
                        }

                        if (activeChapter < totalChapters) {
                            Button(
                                onClick = { activeChapter += 1 },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ShaktiRed,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("পরের অধ্যায়")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        } else {
                            Button(
                                onClick = { activeChapter = 1 },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ShaktiGold,
                                    contentColor = ShaktiDark
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("শুরুতে ফিরে যান", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
