package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AlpanaDivider
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.SantipurViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "শান্তিপুর ও এই উদ্যোগ সম্পর্কে" else "About Santipur Utsabh",
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
                .testTag("about_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Title Card
            item {
                Surface(
                    color = Maroon900,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = Gold500,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "ঐতিহ্য • সংস্কৃতি • ভক্তি",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "উৎসবের শহর শান্তিপুর",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Gold200
                        )

                        Text(
                            text = "SANTIPUR FESTIVAL & HERITAGE GUIDE",
                            style = MaterialTheme.typography.labelMedium,
                            color = Gold400,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "সংস্করণ ১.০ • নদীয়া, পশ্চিমবঙ্গ",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmCream.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        AlpanaDivider(color = Gold400.copy(alpha = 0.5f))
                    }
                }
            }

            // Project Vision
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isBn) "উদ্যোগের উদ্দেশ্য" else "Project Mission",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Maroon900
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isBn)
                                "এই ডিজিটাল গাইডটির মূল লক্ষ্য হলো শান্তিপুরের সুপ্রাচীন বৈষ্ণব ঐতিহ্য (বিশেষত সাড়ে পাঁচশত বছরের রাসযাত্রা ও ভাঙা রাস), মহাশাক্ত ঐতিহ্য (আগমেশ্বরী ও বামাকালী), ঐতিহ্যবাহী তাঁতশিল্প এবং শতাব্দী প্রাচীন মন্দিরগুলিকে বিশ্বব্যাপী ভক্ত, গবেষক, তীর্থযাত্রী ও পর্যটকদের কাছে নির্ভুলভাবে তুলে ধরা।"
                            else
                                "Our mission is to systematically document and provide accessible navigation for the extraordinary cultural, Vaishnava, Tantric, and handloom heritage of Santipur, Nadia, West Bengal.",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Data Quality & Ethics Policy
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = StatusGreen)
                            Text(
                                text = if (isBn) "তথ্য সত্যতা ও নীতিগত অঙ্গীকার" else "Data Integrity & Verification Policy",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val policies = if (isBn) listOf(
                            "কোনো অপ্রমাণিত বা কাল্পনিক লাইভ ডেটা দাবি করা হয় না; সরকারি নির্দেশনা, মন্দির সেবায়েত এবং অনুমোদিত গবেষকদের যাচাইকৃত তথ্যই প্রকাশ করা হয়।",
                            "ভিড় সংক্রান্ত আপডেট ব্যবহারকারী ও স্বেচ্ছাসেবীদের সাম্প্রতিক টাইমস্ট্যাম্প যুক্ত রিপোর্টের ওপর ভিত্তি করে প্রদর্শিত হয়।",
                            "জরুরি যোগাযোগ নম্বরসমূহ (থানা, হাসপাতাল, ফায়ার ব্রিগেড) সরাসরি স্থানীয় প্রশাসনিক নথির সাথে সঙ্গতিপূর্ণ।"
                        ) else listOf(
                            "We never fabricate artificial crowd figures; timings and heritage accounts are corroborated with temple records and published regional archives.",
                            "Crowd reports transparently disclose whether they originate from official municipal bulletins or verified visitor submissions.",
                            "Public emergency contact lines are sourced from Santipur Municipality and Nadia District Police."
                        )

                        policies.forEach { p ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("•", color = StatusGreen, fontWeight = FontWeight.Bold)
                                Text(p, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // Researchers & Contributors
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isBn) "গবেষণা ও কারিগরি দল" else "Research & Engineering Team",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Maroon900
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isBn)
                                "ঐতিহ্য ও সাংস্কৃতিক গবেষণা: নদীয়ার স্থানীয় বৈষ্ণব ও শাক্ত গবেষক সমাজ\nজিওস্প্যাশিয়াল ও সফটওয়্যার ডিজাইন: সিনিয়র ইউআই/ইউএক্স ও অ্যান্ড্রয়েড মোবাইল টিম\nসহযোগিতা: শান্তিপুর ঐতিহ্য সচেতন নাগরিকবৃন্দ ও মন্দির সেবায়েত পরিবারবর্গ"
                            else
                                "Cultural & Archival Research: Nadia Regional Folklore & Vaishnava Heritage Society\nGeospatial Architecture & Compose UI: Senior Mobile Full-Stack Team\nCooperation: Santipur Municipality & Temple Sevaits",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
