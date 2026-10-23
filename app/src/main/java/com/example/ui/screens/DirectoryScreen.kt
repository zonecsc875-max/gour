package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.CrowdStatus
import com.example.data.model.PlaceCategory
import com.example.ui.components.PlaceCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.SantipurViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val savedIds by viewModel.savedPlaceIds.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI

    val filteredPlaces = viewModel.getFilteredPlaces()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "পুজো ও মন্দির ডিরেক্টরি (A-Z)" else "Pandal & Temple Directory",
                        fontWeight = FontWeight.Bold,
                        color = Maroon900
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Maroon900
                        )
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
                .testTag("directory_screen"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Search Box
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = {
                            Text(
                                text = if (isBn) "নাম, এলাকা বা দেবতার নাম খুঁজুন…" else "Search name, area or deity…",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Maroon700)
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("directory_search_input"),
                        singleLine = true
                    )
                }
            }

            // 2. Category Filter Chips Row
            item {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = uiState.selectedCategoryFilter == null,
                                onClick = { viewModel.setCategoryFilter(null) },
                                label = { Text(if (isBn) "সকল পুজো ও মন্দির" else "All Listings") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Maroon100,
                                    selectedLabelColor = Maroon900
                                )
                            )
                        }

                        items(PlaceCategory.entries) { cat ->
                            FilterChip(
                                selected = uiState.selectedCategoryFilter == cat,
                                onClick = {
                                    viewModel.setCategoryFilter(if (uiState.selectedCategoryFilter == cat) null else cat)
                                },
                                label = { Text(if (isBn) cat.titleBn else cat.titleEn) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Maroon100,
                                    selectedLabelColor = Maroon900
                                )
                            )
                        }
                    }
                }
            }

            // 3. Crowd Level Filter Chips Row
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = uiState.selectedCrowdFilter == null,
                                onClick = { viewModel.setCrowdFilter(null) },
                                label = { Text(if (isBn) "সব ভিড় মাত্রা" else "All Crowd Levels") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Gold100,
                                    selectedLabelColor = Gold900
                                )
                            )
                        }

                        items(CrowdStatus.entries.filter { it != CrowdStatus.UNKNOWN }) { status ->
                            FilterChip(
                                selected = uiState.selectedCrowdFilter == status,
                                onClick = {
                                    viewModel.setCrowdFilter(if (uiState.selectedCrowdFilter == status) null else status)
                                },
                                label = { Text(if (isBn) status.labelBn else status.labelEn) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Gold100,
                                    selectedLabelColor = Gold900
                                )
                            )
                        }
                    }
                }
            }

            // 4. Data Verification Policy Disclaimer Note
            item {
                Surface(
                    color = SoftSand,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = StatusGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isBn)
                                "সবুজ টিকযুক্ত স্থানগুলি গবেষক ও শান্তিপুর পৌরসভা তথ্যসূত্রে যাচাইকৃত। কোনো তথ্যের পরিবর্তন বা নতুন পুজোর জন্য সংশোধন বাটনে ট্যাপ করুন।"
                            else
                                "Listings with green badges are verified against official records. For changes, please submit a correction.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Maroon900,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // 5. Results Counter
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "খুঁজে পাওয়া ফলাফল: ${filteredPlaces.size} টি" else "Showing ${filteredPlaces.size} locations",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Maroon800
                    )
                }
            }

            // 6. List of Places
            if (filteredPlaces.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isBn) "কোনো পুজো বা মন্দির খুঁজে পাওয়া যায়নি" else "No matching locations found",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = {
                            viewModel.setSearchQuery("")
                            viewModel.setCategoryFilter(null)
                            viewModel.setCrowdFilter(null)
                        }) {
                            Text(if (isBn) "ফিল্টার মুছুন" else "Clear Filters")
                        }
                    }
                }
            } else {
                items(filteredPlaces) { place ->
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
}
