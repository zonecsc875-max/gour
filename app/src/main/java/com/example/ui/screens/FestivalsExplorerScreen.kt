package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlaceCategory
import com.example.data.repository.SantipurRepository
import com.example.ui.components.AlpanaDivider
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SantipurViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalsExplorerScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI
    val allFestivals = remember { SantipurRepository.getAllFestivals() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "শান্তিপুরের ১৪টি উৎসব বিভাগ" else "Santipur 14 Festival Categories",
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
                .testTag("festivals_explorer_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Surface(
                    color = Maroon900,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = if (isBn) "ঐতিহ্যের নদীয়া • উৎসবের শহর শান্তিপুর" else "Heritage Nadia • City of Festivals",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Gold300
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBn)
                                "প্রতিটি উৎসবের ঐতিহাসিক প্রেক্ষাপট, আনুমানিক তিথি, আচার এবং সংশ্লিষ্ট সক্রিয় পূজাস্থলগুলির পূর্ণ বিবরণ।"
                            else
                                "Explore historical roots, lunar dates, key rituals, and associated shrines across all 14 festival classifications.",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmCream
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        AlpanaDivider(color = Gold400.copy(alpha = 0.5f))
                    }
                }
            }

            items(allFestivals) { fest ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (fest.id == "rash_yatra") {
                                viewModel.navigateTo(AppScreen.RASH_YATRA_SPECIAL)
                            } else if (fest.id == "kali_puja") {
                                viewModel.navigateTo(AppScreen.KALI_PUJA_SPECIAL)
                            } else {
                                val targetCategory = when (fest.id) {
                                    "gopal_puja" -> PlaceCategory.GOPAL_PUJA
                                    "jagadhatri_puja" -> PlaceCategory.JAGADHATRI_PUJA
                                    "durga_puja" -> PlaceCategory.DURGA_PUJA
                                    "rath_yatra" -> PlaceCategory.RATH_YATRA
                                    "barowari_utsabh" -> PlaceCategory.FAMILY_BAROWARI
                                    else -> null
                                }
                                viewModel.setCategoryFilter(targetCategory)
                                viewModel.navigateTo(AppScreen.DIRECTORY)
                            }
                        },
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
                                    color = if (fest.isPrimaryFeature) Maroon900 else Maroon800,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "${fest.participatingPlacesCount} ${if (isBn) "টি স্থান" else "places"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Gold900,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isBn) fest.nameBn else fest.nameEn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Maroon900
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isBn) fest.historyBn else fest.historyEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = fest.approximateDate,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            FilledTonalButton(
                                onClick = {
                                    if (fest.id == "rash_yatra") {
                                        viewModel.navigateTo(AppScreen.RASH_YATRA_SPECIAL)
                                    } else if (fest.id == "kali_puja") {
                                        viewModel.navigateTo(AppScreen.KALI_PUJA_SPECIAL)
                                    } else {
                                        viewModel.navigateTo(AppScreen.DIRECTORY)
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = if (isBn) "স্থানগুলি দেখুন" else "View Places",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
