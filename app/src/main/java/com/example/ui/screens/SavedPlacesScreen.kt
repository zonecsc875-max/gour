package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.repository.SantipurRepository
import com.example.ui.components.PlaceCard
import com.example.ui.theme.Maroon900
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SantipurViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPlacesScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val savedIds by viewModel.savedPlaceIds.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI

    val allPlaces = remember { SantipurRepository.getAllPlaces() }
    val savedPlaces = remember(savedIds) {
        allPlaces.filter { savedIds.contains(it.id) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "সংরক্ষিত স্থান ও পুজো (${savedPlaces.size})" else "Saved Places (${savedPlaces.size})",
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
        if (savedPlaces.isEmpty()) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("saved_places_empty"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        Icons.Default.BookmarkRemove,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isBn) "আপনার সংরক্ষিত তালিকা বর্তমানে খালি" else "No saved places yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isBn)
                            "হোম বা ডিরেক্টরি থেকে বুকমার্ক আইকনে ট্যাপ করে আপনার প্রিয় মন্দির ও প্যান্ডেল সংরক্ষণ করুন।"
                        else
                            "Bookmark temples or pandals from Home or Directory for quick offline access.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.navigateTo(AppScreen.DIRECTORY) }) {
                        Text(if (isBn) "ডিরেক্টরি অন্বেষণ করুন" else "Explore Directory")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("saved_places_screen"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(savedPlaces) { place ->
                    val dist = viewModel.getDistanceToPlace(place)
                    PlaceCard(
                        place = place,
                        distanceKm = dist,
                        isSaved = true,
                        language = uiState.language,
                        onCardClick = { viewModel.openPlaceDetail(place.id) },
                        onSaveToggle = { viewModel.toggleSavePlace(place.id) },
                        onNavigateClick = { viewModel.startInAppNavigation(place.id) }
                    )
                }
            }
        }
    }
}
