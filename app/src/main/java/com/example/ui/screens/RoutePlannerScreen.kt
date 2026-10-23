package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.data.model.RoutePlan
import com.example.data.repository.SantipurRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SantipurViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutePlannerScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val customPlaceIds by viewModel.customItineraryPlaceIds.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI
    val context = LocalContext.current

    val curatedRoutes = remember { SantipurRepository.getCuratedRoutes() }
    val allPlaces = remember { SantipurRepository.getAllPlaces() }
    var selectedTab by remember { mutableStateOf(0) } // 0: Curated, 1: Custom

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "পরিক্রমা ও ভ্রমণ রুট" else "Tour Itinerary & Circuits",
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("route_planner_screen")
        ) {
            // Tabs: Curated vs My Custom Route
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Maroon700
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(if (isBn) "বাছাইকৃত সেরা রুট" else "Curated Circuits", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = if (isBn) "আমার নিজস্ব তালিকা (${customPlaceIds.size})" else "My Itinerary (${customPlaceIds.size})",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }

            if (selectedTab == 0) {
                // Curated Routes List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(curatedRoutes) { route ->
                        CuratedRouteCard(
                            route = route,
                            isBn = isBn,
                            onExploreStops = {
                                viewModel.navigateTo(AppScreen.INTERACTIVE_MAP)
                            },
                            onOpenMapDirections = {
                                val stopPlaces = route.placeIds.mapNotNull { id -> allPlaces.find { it.id == id } }
                                if (stopPlaces.isNotEmpty()) {
                                    val destination = stopPlaces.first()
                                    viewModel.startInAppNavigation(destination.id)
                                }
                            }
                        )
                    }
                }
            } else {
                // Custom User Itinerary
                val customPlaces = customPlaceIds.mapNotNull { id -> allPlaces.find { it.id == id } }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Surface(
                            color = Maroon100,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (isBn) "মোট নির্বাচিত স্টপ: ${customPlaces.size} টি" else "Selected stops: ${customPlaces.size}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Maroon900
                                    )
                                    Text(
                                        text = if (isBn) "আনুমানিক হাঁটার সময়: ~${customPlaces.size * 25} মিনিট" else "Est. walking duration: ~${customPlaces.size * 25} mins",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Maroon800
                                    )
                                }

                                if (customPlaces.isNotEmpty()) {
                                    Button(
                                        onClick = {
                                            val first = customPlaces.first()
                                            viewModel.startInAppNavigation(first.id)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Maroon700),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isBn) "লাইভ নেভিগেশন" else "Start Nav", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }

                    if (customPlaces.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.AddRoad, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (isBn) "আপনার তালিকায় কোনো পুজো বা মন্দির যোগ করা নেই" else "No stops in your itinerary yet",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isBn) "হোম বা ডিরেক্টরি থেকে 'আমার ভ্রমণ পরিক্রমায় যোগ করুন' চাপুন" else "Add places from Home or Directory",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        itemsIndexed(customPlaces) { index, place ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        color = Gold500,
                                        shape = CircleShape,
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${index + 1}",
                                                fontWeight = FontWeight.Bold,
                                                color = Maroon900,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isBn) place.nameBn else place.nameEn,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Maroon900
                                        )
                                        Text(
                                            text = "${place.locality} • ${place.darshanTimings}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }

                                    IconButton(onClick = { viewModel.removeFromCustomItinerary(place.id) }) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = VermilionRed)
                                    }
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
private fun CuratedRouteCard(
    route: RoutePlan,
    isBn: Boolean,
    onExploreStops: () -> Unit,
    onOpenMapDirections: () -> Unit
) {
    val allPlaces = remember { SantipurRepository.getAllPlaces() }
    val stopPlaces = route.placeIds.mapNotNull { id -> allPlaces.find { it.id == id } }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
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
                    color = Gold100,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isBn) route.suitabilityTagBn else route.suitabilityTagEn,
                        style = MaterialTheme.typography.labelSmall,
                        color = Gold900,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = Maroon700, modifier = Modifier.size(16.dp))
                    Text(
                        text = "~${route.estimatedMinutes} মিনিট • ${route.totalDistanceKm} কিমি",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Maroon700
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isBn) route.titleBn else route.titleEn,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Maroon900
            )

            Text(
                text = if (isBn) route.subtitleBn else route.subtitleEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Stops ordered pills
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                stopPlaces.forEachIndexed { idx, p ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Maroon700,
                            shape = CircleShape,
                            modifier = Modifier.size(18.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${idx + 1}",
                                    fontSize = 10.sp,
                                    color = Gold100,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = if (isBn) p.nameBn else p.nameEn,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Maroon900
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onExploreStops,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBn) "ম্যাপে রুট" else "Map Route", fontSize = 12.sp)
                }

                Button(
                    onClick = onOpenMapDirections,
                    colors = ButtonDefaults.buttonColors(containerColor = Maroon700),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBn) "ইন-অ্যাপ নেভিগেশন" else "Start Nav", fontSize = 12.sp)
                }
            }
        }
    }
}
