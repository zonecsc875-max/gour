package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Place
import com.example.data.model.PlaceCategory
import com.example.data.repository.SantipurRepository
import com.example.ui.components.SantipurCanvasMap
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.SantipurViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractiveMapScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI
    val context = LocalContext.current

    var selectedMapCategory by remember { mutableStateOf<PlaceCategory?>(null) }
    var selectedMarkerPlace by remember {
        mutableStateOf<Place?>(
            SantipurRepository.getAllPlaces().find { it.id == uiState.selectedPlaceId }
                ?: SantipurRepository.getAllPlaces().first()
        )
    }

    val allPlaces = remember { SantipurRepository.getAllPlaces() }
    val displayPlaces = remember(selectedMapCategory) {
        if (selectedMapCategory == null) allPlaces
        else allPlaces.filter { it.category == selectedMapCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBn) "শান্তিপুর উৎসব ও পথনির্দেশ ম্যাপ" else "Santipur Interactive Map",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Maroon900
                        )
                        Text(
                            text = if (isBn) "২৩.২৫০৫° N, ৮৮.৪৩৪৪° E • জিপিএস সক্রিয়" else "23.2505° N, 88.4344° E • GPS Ready",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Maroon900)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val uri = Uri.parse("geo:${SantipurRepository.SANTIPUR_LAT},${SantipurRepository.SANTIPUR_LNG}?q=Santipur,+Nadia")
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }) {
                        Icon(Icons.Default.OpenInNew, contentDescription = "Open External Map", tint = Maroon900)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("interactive_map_screen")
        ) {
            // Category Filter Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedMapCategory == null,
                        onClick = { selectedMapCategory = null },
                        label = { Text(if (isBn) "সকল স্থান (${allPlaces.size})" else "All (${allPlaces.size})") }
                    )
                }
                items(
                    listOf(
                        PlaceCategory.RASH_YATRA,
                        PlaceCategory.KALI_PUJA,
                        PlaceCategory.HERITAGE_TEMPLE,
                        PlaceCategory.TRANSPORT,
                        PlaceCategory.FACILITY
                    )
                ) { cat ->
                    FilterChip(
                        selected = selectedMapCategory == cat,
                        onClick = {
                            selectedMapCategory = if (selectedMapCategory == cat) null else cat
                        },
                        label = { Text(if (isBn) cat.titleBn else cat.titleEn) }
                    )
                }
            }

            // Interactive Canvas Map
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                SantipurCanvasMap(
                    places = displayPlaces,
                    selectedPlace = selectedMarkerPlace,
                    onPlaceSelect = { selectedMarkerPlace = it },
                    userLat = uiState.userLat,
                    userLng = uiState.userLng,
                    language = uiState.language,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Map Legend and Selected Detail Card
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Quick Legend Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendItem(color = Gold700, label = if (isBn) "রাসযাত্রা" else "Rash")
                        LegendItem(color = VermilionRed, label = if (isBn) "কালীপুজো" else "Kali")
                        LegendItem(color = Terracotta, label = if (isBn) "ঐতিহ্য মন্দির" else "Temple")
                        LegendItem(color = Color(0xFF1976D2), label = if (isBn) "স্টেশন/বাস" else "Transit")
                        LegendItem(color = Color(0xFF388E3C), label = if (isBn) "চিকিৎসা/পার্কিং" else "Facility")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val place = selectedMarkerPlace
                    if (place != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBn) place.nameBn else place.nameEn,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Maroon900
                                )
                                Text(
                                    text = "${place.locality} • ${place.address}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilledTonalButton(
                                    onClick = { viewModel.openPlaceDetail(place.id) },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text(if (isBn) "তথ্য" else "Info", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        viewModel.startInAppNavigation(place.id)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Maroon700),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isBn) "নেভিগেশন" else "Navigate", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}
