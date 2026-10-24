package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AlpanaDivider
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.SantipurViewModel
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BamakaliDanceScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI
    val scrollState = rememberScrollState()

    val eventTitle = if (isBn) "মা বামাকালীর বিখ্যাত নৃত্য" else "Famous Dance of Maa Bamakali"
    val eventVenue = if (isBn) "বেজপাড়া মাঠ, শান্তিপুর, নদীয়া" else "Bejpara Math, Santipur, Nadia"
    val eventDateStr = if (isBn) "৯ নভেম্বর ২০২৬, সোমবার" else "9 November 2026, Monday"
    val eventTimeStr = if (isBn) "রাত ৮:০০ টা" else "8:00 PM IST"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "মা বামাকালী নৃত্য ২০২৬" else "Maa Bamakali Dance 2026",
                        fontWeight = FontWeight.Bold,
                        color = Gold100
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("bamakali_back_btn")
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
                .testTag("bamakali_dance_screen")
        ) {
            // 1. Main Event Hero Image (Actual uploaded asset, dark background, fully preserved)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .background(Color.Black)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.maa_bamakali_hero),
                    contentDescription = "শান্তিপুরের মা বামাকালীর ঐতিহ্যবাহী নৃত্য ও অগ্নিশিখার দৃশ্য।",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("bamakali_hero_image")
                )

                // Subtle dark gradient overlay at the bottom for title legibility
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                )

                // Upcoming badge & Status
                Surface(
                    color = ShaktiRed,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (isBn) "আসন্ন বিশেষ উৎসব (Upcoming)" else "UPCOMING EVENT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Title overlay at the bottom of hero
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "মা বামাকালীর বিখ্যাত নৃত্য",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Gold100
                    )
                    Text(
                        text = "শান্তিপুর কালীপুজো ২০২৬ • ঐতিহ্যবাহী ধর্মীয় নৃত্য",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gold300
                    )
                }
            }

            // 2. Date, Time & Venue Key Highlight Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Date & Time Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = ShaktiRed.copy(alpha = 0.12f),
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Event,
                                    contentDescription = null,
                                    tint = ShaktiRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = eventDateStr,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ShaktiDark
                            )
                            Text(
                                text = "সময়: $eventTimeStr (সন্ধ্যা/রাত্রি)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = ShaktiRedDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = ShaktiGoldLight.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // New Venue Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            color = ShaktiGold.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = ShaktiGoldDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isBn) "নতুন নির্ধারিত স্থান" else "New Official Venue",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ShaktiRed,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = ShaktiGoldLight,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "স্থানান্তরিত",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ShaktiDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = eventVenue,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ShaktiDark
                            )

                            Text(
                                text = "পূর্বে বড়বাজারের মোড়ে ভিড় ও সংকীর্ণতার কারণে সবার সুবিধার্থে বেজপাড়া মাঠে স্থানান্তরিত হয়েছে।",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            // 3. Official Bengali Event Announcement Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ShaktiDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "🔱 জয় মা বামাকালী 🔱",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = ShaktiGold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    AlpanaDivider(color = ShaktiGold.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "শান্তিপুরের মা বামাকালীর বিখ্যাত নৃত্য অনুষ্ঠানের স্থান পরিবর্তন করা হয়েছে। পূর্বে বড়বাজারের মোড়ে ভিড় ও জায়গার সংকীর্ণতার কারণে সবার সুবিধা ও নিরাপত্তার কথা বিবেচনা করে, পুজো কমিটি ও পুলিশ প্রশাসনের সহযোগিতায় এবারের অনুষ্ঠান অনুষ্ঠিত হবে বেজপাড়া মাঠ, শান্তিপুর, নদীয়া-তে।\n\n" +
                                "• তারিখ: ৯ নভেম্বর ২০২৬\n" +
                                "• সময়: রাত ৮টা\n" +
                                "• স্থান: বেজপাড়া মাঠ, শান্তিপুর, নদীয়া\n\n" +
                                "সকল দর্শনার্থীকে নির্ধারিত স্থানে এসে অনুষ্ঠান উপভোগ করার এবং পুলিশ প্রশাসন ও আয়োজকদের নির্দেশ মেনে চলার অনুরোধ জানানো হচ্ছে।\n\n" +
                                "বিশেষ অনুরোধ: এ স্থান পরিবর্তন নিয়ে কোনও পেজে বা পোস্টে মন্তব্য বা কমেন্ট না করার জন্য অনুরোধ জানানো হয়েছে।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ShaktiCream,
                        lineHeight = 24.sp
                    )
                }
            }

            // 4. Action Buttons (Google Maps Search, In-App Navigation, Calendar Reminder, Share)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Google Maps Search Button
                Button(
                    onClick = {
                        val mapsSearchUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=Bejpara+Math%2C+Santipur%2C+Nadia%2C+West+Bengal")
                        val intent = Intent(Intent.ACTION_VIEW, mapsSearchUri)
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShaktiRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("bamakali_maps_search_btn")
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBn) "Google Maps-এ স্থান খুঁজুন" else "Search Venue on Google Maps",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // In-App Turn-by-Turn Navigation
                FilledTonalButton(
                    onClick = {
                        viewModel.startInAppNavigation("bejpara_math")
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = ShaktiGold,
                        contentColor = ShaktiDark
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("bamakali_inapp_nav_btn")
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBn) "অ্যাপের ভেতরে রাস্তা দেখুন (In-App GPS)" else "In-App Turn-by-Turn Navigation",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Set Reminder Button (Calendar Contract)
                    OutlinedButton(
                        onClick = {
                            setEventReminder(context, isBn)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ShaktiDark),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("bamakali_reminder_btn")
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) "রিমাইন্ডার সেট করুন" else "Set Reminder",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Share Event Info Button
                    OutlinedButton(
                        onClick = {
                            shareEventInfo(context, isBn)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ShaktiDark),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("bamakali_share_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) "তথ্য শেয়ার করুন" else "Share Event",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 5. Crowd & Visitor Safety Guidelines
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = ShaktiRed)
                        Text(
                            text = if (isBn) "দর্শনার্থীদের নিরাপত্তা ও শৃঙ্খলা নির্দেশিকা" else "Crowd & Visitor Safety Guidance",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ShaktiDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val safetyGuidelines = if (isBn) listOf(
                        "পুলিশ প্রশাসন ও পুজো কমিটির নির্দেশিকা ও প্রবেশপথের নিয়মাবলী কঠোরভাবে মেনে চলুন।",
                        "ধাক্কাধাক্কি পরিহার করুন এবং জরুরি বহির্গমন ও অ্যাম্বুলেন্স পথ সর্বদা মুক্ত রাখুন।",
                        "শিশুদের সঙ্গে প্রাপ্তবয়স্ক অভিভাবক রাখুন এবং তাদের হাত শক্ত করে ধরে রাখুন।",
                        "পবিত্র পূজার হোমাগ্নি ও নৃত্য এলাকার নির্দিষ্ট ব্যারিকেডের বাইরে থেকে দর্শন করুন।",
                        "শুধুমাত্র নির্ভরযোগ্য ও যাচাইকৃত তথ্য অনুসরণ করুন।"
                    ) else listOf(
                        "Follow police and event-organiser instructions at designated entry and exit points.",
                        "Avoid pushing and always keep emergency ambulance routes clear.",
                        "Keep children accompanied by a responsible adult at all times.",
                        "View the sacred fire and dance ritual from behind the security barricades.",
                        "Rely only on verified route and venue announcements."
                    )

                    for (rule in safetyGuidelines) {
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("•", fontWeight = FontWeight.Bold, color = ShaktiRed)
                            Text(
                                text = rule,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun setEventReminder(context: Context, isBn: Boolean) {
    val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata")).apply {
        set(Calendar.YEAR, 2026)
        set(Calendar.MONTH, Calendar.NOVEMBER)
        set(Calendar.DAY_OF_MONTH, 9)
        set(Calendar.HOUR_OF_DAY, 20)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, "মা বামাকালীর বিখ্যাত নৃত্য (শান্তিপুর কালীপুজো ২০২৬)")
        putExtra(CalendarContract.Events.EVENT_LOCATION, "বেজপাড়া মাঠ, শান্তিপুর, নদীয়া")
        putExtra(CalendarContract.Events.DESCRIPTION, "শান্তিপুরের ঐতিহ্যবাহী মা বামাকালীর নৃত্য অনুষ্ঠান। স্থান: বেজপাড়া মাঠ।")
        putExtra(CalendarContract.EXTRA_CUSTOM_APP_URI, "Asia/Kolkata")
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.timeInMillis)
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.timeInMillis + (3 * 60 * 60 * 1000))
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback
    }
}

private fun shareEventInfo(context: Context, isBn: Boolean) {
    val shareText = """
        🔱 মা বামাকালীর বিখ্যাত নৃত্য — শান্তিপুর কালীপুজো ২০২৬ 🔱
        
        📅 তারিখ: ৯ নভেম্বর ২০২৬, সোমবার
        ⏰ সময়: রাত ৮:০০ টা
        📍 নতুন নির্ধারিত স্থান: বেজপাড়া মাঠ, শান্তিপুর, নদীয়া
        
        পূর্বে বড়বাজারের মোড়ে ভিড় ও সংকীর্ণতার কারণে সবার সুবিধা ও নিরাপত্তার স্বার্থে এবারের অনুষ্ঠান বেজপাড়া মাঠে অনুষ্ঠিত হবে।
        
        (উৎসবের শহর শান্তিপুর অ্যাপ থেকে সংগৃহীত)
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "মা বামাকালীর বিখ্যাত নৃত্য ২০২৬")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    try {
        context.startActivity(Intent.createChooser(intent, "অনুষ্ঠানের তথ্য শেয়ার করুন"))
    } catch (e: Exception) {
        // Fallback
    }
}
