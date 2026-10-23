package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.SantipurViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportFacilitiesScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "পরিবহন, পার্কিং ও জরুরি সুবিধা" else "Transit, Parking & Facilities",
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
                .testTag("transport_facilities_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Emergency Helpline Direct Dialer Cards
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Maroon900)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.PhoneInTalk, contentDescription = null, tint = Gold400)
                            Text(
                                text = if (isBn) "জরুরি হেল্পলাইন নম্বর (২৪ ঘণ্টা সক্রিয়)" else "24x7 Emergency Helplines",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Gold100
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val helplines = listOf(
                            Triple("শান্তিপুর থানা (Police)", "03473278023", "০৩৪৭৩-২৭৮০২৩ / ১১২"),
                            Triple("স্টেট জেনারেল হাসপাতাল (Hospital)", "03473278027", "০৩৪৭৩-২৭৮০২৭"),
                            Triple("অ্যাম্বুলেন্স জরুরি পরিষেবা (Ambulance)", "102", "১০২"),
                            Triple("দমকল / ফায়ার ব্রিগেড (Fire)", "101", "১০১")
                        )

                        helplines.forEach { (name, phone, display) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(name, style = MaterialTheme.typography.bodySmall, color = WarmCream)
                                    Text(display, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Gold300)
                                }
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Gold500, contentColor = Maroon900),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isBn) "কল" else "Call", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 2. Train Timings (Sealdah - Santipur EMU Locals)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Train, contentDescription = null, tint = Maroon700)
                            Text(
                                text = if (isBn) "ট্রেন সময়সূচী: শিয়ালদহ - শান্তিপুর লোকাল" else "Train Schedule: Sealdah - Santipur Local",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isBn)
                                "শিয়ালদহ থেকে রানাঘাট হয়ে শান্তিপুর আসার জন্য প্রতি ৩০-৪০ মিনিট অন্তর ইএমইউ লোকাল ট্রেন চলাচল করে। রাসযাত্রার ৩ দিন রাতে বিশেষ ফেস্টিভ্যাল লোকাল ট্রেন চালানো হয়।"
                            else
                                "EMU locals depart every 30-40 mins between Sealdah and Santipur via Ranaghat. Special night festival trains operate during Rash Yatra.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val trainHighlights = if (isBn) listOf(
                            "প্রথম ট্রেন (শিয়ালদহ থেকে): ভোর ৩:৪৫",
                            "প্রধান সকালের ট্রেনসমূহ: ৬:২০, ৭:৫০, ৯:১৫, ১১:০৫",
                            "বিকেলের উৎসব ট্রেনসমূহ: ২:২০, ৩:৪০, ৫:১০, ৬:৩০",
                            "উৎসবের বিশেষ নিশীথ ট্রেন: রাত ১১:৪৫ ও রাত ১:২০ (ভাঙা রাস উপলক্ষে)"
                        ) else listOf(
                            "First Train from Sealdah: 3:45 AM",
                            "Key Morning Trains: 6:20 AM, 7:50 AM, 9:15 AM, 11:05 AM",
                            "Afternoon/Evening Trains: 2:20 PM, 3:40 PM, 5:10 PM, 6:30 PM",
                            "Special Midnight Festival Locals: 11:45 PM & 1:20 AM"
                        )

                        trainHighlights.forEach { item ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("•", color = Maroon700, fontWeight = FontWeight.Bold)
                                Text(item, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // 3. Local Toto (E-Rickshaw) & Auto Fares Guidance
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.ElectricRickshaw, contentDescription = null, tint = Terracotta)
                            Text(
                                text = if (isBn) "স্থানীয় টোটো ও অটোরিক্সা নির্দেশিকা" else "Local Toto (E-Rickshaw) Guidance",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val totoFares = if (isBn) listOf(
                            "শান্তিপুর রেলওয়ে স্টেশন ➔ বড় গোস্বামী বাড়ি: ₹১৫ (শেয়ার) / ₹৮০ (রিজার্ভ)",
                            "শান্তিপুর স্টেশন ➔ শ্যামচাঁদ মন্দির: ₹১৫ (শেয়ার) / ₹৮০ (রিজার্ভ)",
                            "শান্তিপুর স্টেশন ➔ আগমেশ্বরী মন্দির: ₹২০ (শেয়ার) / ₹১০০ (রিজার্ভ)",
                            "শান্তিপুর স্টেশন ➔ বাবলা অদ্বৈত পাঠ: ₹২৫ (শেয়ার) / ₹১৩০ (রিজার্ভ)"
                        ) else listOf(
                            "Station to Boro Goswami Bari: ₹15 (Share) / ₹80 (Reserve)",
                            "Station to Shyamchand Mandir: ₹15 (Share) / ₹80 (Reserve)",
                            "Station to Agameswari Mandir: ₹20 (Share) / ₹100 (Reserve)",
                            "Station to Babla Advaita Paath: ₹25 (Share) / ₹130 (Reserve)"
                        )

                        totoFares.forEach { item ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("•", color = Terracotta, fontWeight = FontWeight.Bold)
                                Text(item, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            // 4. Designated Parking Grounds
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.LocalParking, contentDescription = null, tint = Gold900)
                            Text(
                                text = if (isBn) "পৌরসভা ও পুলিশ অনুমোদিত মূল পার্কিং জোন" else "Designated Parking Zones",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val parkingList = listOf(
                            Pair(
                                "সুত্রাগড় হাই স্কুল গ্রাউন্ড (Sutragarh High School)",
                                "সর্ববৃহৎ সেন্ট্রাল পার্কিং • চারচাকা ও বাইক • বিনামূল্যে বায়ো-টয়লেট ও পানীয় জল"
                            ),
                            Pair(
                                "শান্তিপুর কলেজ গ্রাউন্ড (Santipur College)",
                                "বাইপাস সংযোগকারী পার্কিং • তোপখানা মোড় থেকে ৪০০ মিটার"
                            ),
                            Pair(
                                "রেলওয়ে স্টেশন পার্কিং এরিয়া (Station Ground)",
                                "টু-হুইলার ও স্থানীয় গাড়ির জন্য ২৪ ঘণ্টা নিরাপদ শেড"
                            )
                        )

                        parkingList.forEach { (zone, desc) ->
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(zone, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Maroon900)
                                Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // 5. Drinking Water, Bio-Toilets & Medical Posts
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF1976D2))
                            Text(
                                text = if (isBn) "পানীয় জল, বায়ো-টয়লেট ও ফার্স্ট এইড কিয়স্ক" else "Water, Bio-Toilets & First Aid",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Maroon900
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val civicFacilities = if (isBn) listOf(
                            "বিশুদ্ধ পানীয় জল কিয়স্ক: তোপখানা মোড়, শ্যামচাঁদ মন্দির গেট, সুত্রাগড় হাই স্কুল পার্কিং ও পাবলিক লাইব্রেরি মাঠ।",
                            "জনসাধারণের বায়ো-টয়লেট: মহিলা ও পুরুষদের জন্য পৃথক সুবিধা সহ সুত্রাগড় ফুটবল মাঠ ও স্টেশন প্রবেশদ্বারে উপলব্ধ।",
                            "রেড ক্রস ও সেন্ট জনস ফার্স্ট এইড ক্যাম্প: তোপখানা ও গোস্বামী পাড়ায় সার্বক্ষণিক প্যারামেডিক উপস্থিত।"
                        ) else listOf(
                            "Drinking water kiosks: Topkhana More, Shyamchand Gate, Sutragarh Parking, and Library Field.",
                            "Bio-toilets: Separate hygienic male/female restrooms at Sutragarh ground and Railway terminus.",
                            "First-aid posts: Paramedics available at Topkhana & Goswami Para continuously during festival days."
                        )

                        civicFacilities.forEach { f ->
                            Row(
                                modifier = Modifier.padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("•", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                                Text(f, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
