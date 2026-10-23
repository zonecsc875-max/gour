package com.example.ui.screens

import android.content.Intent
import android.provider.CalendarContract
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SantipurRepository
import com.example.ui.components.AlpanaDivider
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.SantipurViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI
    val context = LocalContext.current

    val festivals = remember { SantipurRepository.getAllFestivals() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "শান্তিপুর উৎসব পঞ্জিকা" else "Santipur Festival Calendar",
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
                .testTag("calendar_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Info Banner
            item {
                Surface(
                    color = Maroon900,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = if (isBn) "বাংলা সন ১৪৩৩ • বারো মাসে তেরো পার্বণ" else "Bengali Year 1433 • Eternal Festivals",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Gold300
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBn)
                                "কার্তিক পূর্ণিমার রাসযাত্রা ও দীপান্বিতা কালীপুজো থেকে শুরু করে শান্তিপুরের সমস্ত সনাতনী উৎসবের বার্ষিক সময়সূচী।"
                            else
                                "Annual schedule of all traditional festivals in Santipur, verified by temple sevaits and almanac timings.",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmCream
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        AlpanaDivider(color = Gold400.copy(alpha = 0.5f))
                    }
                }
            }

            items(festivals) { fest ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = if (fest.isPrimaryFeature) Gold500 else Maroon100,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = fest.bengaliSeason,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (fest.isPrimaryFeature) Maroon900 else Maroon800,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = fest.approximateDate,
                                style = MaterialTheme.typography.labelSmall,
                                color = Gold900,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isBn) fest.nameBn else fest.nameEn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Maroon900
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isBn) fest.historyBn else fest.historyEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Key Rituals
                        Text(
                            text = if (isBn) "মূল ধর্মীয় আচার ও তিথি:" else "Key Rituals:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Maroon800
                        )
                        fest.keyRitualsBn.forEach { ritual ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("•", color = Gold700, fontWeight = FontWeight.Bold)
                                Text(
                                    text = ritual,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${fest.participatingPlacesCount} ${if (isBn) "টি সক্রিয় স্থান" else "active locations"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Maroon700
                            )

                            FilledTonalButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_INSERT).apply {
                                        data = CalendarContract.Events.CONTENT_URI
                                        putExtra(CalendarContract.Events.TITLE, "${fest.nameBn} - শান্তিপুর")
                                        putExtra(CalendarContract.Events.DESCRIPTION, fest.significanceBn)
                                        putExtra(CalendarContract.Events.EVENT_LOCATION, "Santipur, Nadia, West Bengal")
                                    }
                                    context.startActivity(intent)
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isBn) "ক্যালেন্ডারে যোগ" else "Add Event", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
