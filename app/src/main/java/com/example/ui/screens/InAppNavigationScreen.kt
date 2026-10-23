package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.SantipurRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.SantipurViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InAppNavigationScreen(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBn = uiState.language == AppLanguage.BENGALI
    val context = LocalContext.current

    val route = uiState.activeNavRoute
    if (route == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isBn) "কোনো সক্রিয় পথনির্দেশ রুট নেই" else "No active navigation route",
                    style = MaterialTheme.typography.titleMedium,
                    color = Maroon900
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = { viewModel.navigateBack() }) {
                    Text(if (isBn) "ফিরে যান" else "Go Back")
                }
            }
        }
        return
    }

    val currentStep = route.steps.getOrElse(uiState.currentNavStepIndex) { route.steps.first() }
    var showStepListSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("in_app_navigation_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBn) "শান্তিপুর নিজস্ব জিপিএস নেভিগেশন" else "Santipur In-App Live Navigation",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Gold100
                        )
                        Text(
                            text = if (isBn) "গন্তব্য: ${route.destinationNameBn}" else "To: ${route.destinationNameEn}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gold300,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.stopInAppNavigation() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Gold100)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleVoiceGuidance() }) {
                        Icon(
                            imageVector = if (uiState.isVoiceGuidanceMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = "Voice Guidance",
                            tint = if (uiState.isVoiceGuidanceMuted) Color.LightGray else Gold400
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Maroon900)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Full Screen Interactive Route Canvas Map
            NavCanvasMap(
                route = route,
                currentStep = currentStep,
                stepIndex = uiState.currentNavStepIndex,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Floating Top Maneuver HUD Card
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(14.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Maroon900)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Maneuver Icon inside golden badge
                        Surface(
                            color = Gold500,
                            shape = CircleShape,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                val maneuverIcon = when (currentStep.maneuver) {
                                    NavManeuver.TURN_RIGHT -> Icons.Default.TurnRight
                                    NavManeuver.TURN_LEFT -> Icons.Default.TurnLeft
                                    NavManeuver.SLIGHT_RIGHT -> Icons.Default.TurnSlightRight
                                    NavManeuver.SLIGHT_LEFT -> Icons.Default.TurnSlightLeft
                                    NavManeuver.DESTINATION -> Icons.Default.Flag
                                    else -> Icons.Default.Straight
                                }
                                Icon(
                                    imageVector = maneuverIcon,
                                    contentDescription = null,
                                    tint = Maroon900,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            if (currentStep.distanceMeters > 0) {
                                Text(
                                    text = if (isBn) "${currentStep.distanceMeters} মিটার পর" else "In ${currentStep.distanceMeters}m",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Gold300
                                )
                            } else {
                                Text(
                                    text = if (isBn) "গন্তব্য সন্নিকটে" else "Arriving Now",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = StatusGreen
                                )
                            }

                            Text(
                                text = if (isBn) currentStep.instructionBn else currentStep.instructionEn,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = if (isBn) currentStep.streetNameBn else currentStep.streetNameEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmCream.copy(alpha = 0.8f),
                                maxLines = 1
                            )
                        }
                    }

                    // Optional road / festival restriction alert
                    if (currentStep.advisoryNoticeBn != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = VermilionRed.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Gold400,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (isBn) currentStep.advisoryNoticeBn else currentStep.advisoryNoticeEn ?: "",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gold200,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // 3. Floating Bottom Navigation Controller & Trip Metrics
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(14.dp)
                    .shadow(12.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Transport Mode Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        NavigationTransportMode.entries.forEach { mode ->
                            val isSelected = route.mode == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setNavTransportMode(mode) },
                                label = {
                                    Text(
                                        text = if (isBn) mode.titleBn else mode.titleEn,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = {
                                    val icon = when (mode) {
                                        NavigationTransportMode.WALKING -> Icons.Default.DirectionsWalk
                                        NavigationTransportMode.TOTO -> Icons.Default.ElectricRickshaw
                                        NavigationTransportMode.DRIVING -> Icons.Default.DirectionsCar
                                    }
                                    Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Maroon100,
                                    selectedLabelColor = Maroon900,
                                    selectedLeadingIconColor = Maroon700
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Trip Metrics: Distance, ETA, Step Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val kmStr = String.format("%.1f", route.totalDistanceMeters / 1000.0)
                            Text(
                                text = if (isBn) "${route.estimatedMinutes} মিনিট ($kmStr কিমি)" else "${route.estimatedMinutes} mins ($kmStr km)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = StatusGreen
                            )
                            Text(
                                text = if (isBn)
                                    "ধাপ ${uiState.currentNavStepIndex + 1}/${route.steps.size} • সুত্রাগড় করিডোর"
                                else
                                    "Step ${uiState.currentNavStepIndex + 1}/${route.steps.size} • Sutragarh Corridor",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Simulation / Step Forward Button
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = { viewModel.prevNavStep() },
                                enabled = uiState.currentNavStepIndex > 0,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Step")
                            }

                            FilledTonalButton(
                                onClick = { viewModel.toggleNavSimulation() },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (uiState.isNavSimulating) VermilionRed.copy(alpha = 0.15f) else Gold100,
                                    contentColor = if (uiState.isNavSimulating) VermilionDark else Gold900
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(
                                    imageVector = if (uiState.isNavSimulating) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (uiState.isNavSimulating) (if (isBn) "থামান" else "Pause") else (if (isBn) "সিমুলেট" else "Simulate"),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = { viewModel.nextNavStep() },
                                enabled = uiState.currentNavStepIndex < route.steps.size - 1,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.SkipNext, contentDescription = "Next Step")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Actions: View All Steps & End Navigation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showStepListSheet = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.FormatListBulleted, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBn) "সকল ধাপ দেখুন" else "Turn List", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.stopInAppNavigation() },
                            colors = ButtonDefaults.buttonColors(containerColor = VermilionDark),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBn) "যাত্রা শেষ করুন" else "Exit Nav", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Step-by-Step Directions Modal Sheet
    if (showStepListSheet) {
        ModalBottomSheet(
            onDismissRequest = { showStepListSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = if (isBn) "সম্পূর্ণ পথনির্দেশ তালিকা (${route.steps.size}টি ধাপ)" else "All Turn-by-Turn Steps",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Maroon900
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxHeight(0.6f)
                ) {
                    itemsIndexed(route.steps) { idx, step ->
                        val isCurrent = idx == uiState.currentNavStepIndex
                        Surface(
                            color = if (isCurrent) Maroon100 else SoftSand,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    color = if (isCurrent) Gold500 else Maroon700,
                                    shape = CircleShape,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${idx + 1}",
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrent) Maroon900 else Gold100,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isBn) step.instructionBn else step.instructionEn,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        color = Maroon900
                                    )
                                    Text(
                                        text = if (isBn) step.streetNameBn else step.streetNameEn,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (step.distanceMeters > 0) {
                                    Text(
                                        text = "${step.distanceMeters}m",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Gold900
                                    )
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
private fun NavCanvasMap(
    route: NavRoute,
    currentStep: NavStep,
    stepIndex: Int,
    modifier: Modifier = Modifier
) {
    // Geospatial bounds of Santipur
    val minLat = 23.2350
    val maxLat = 23.2620
    val minLng = 88.4200
    val maxLng = 88.4550

    fun project(lat: Double, lng: Double, width: Float, height: Float): Offset {
        val normX = (lng - minLng) / (maxLng - minLng)
        val normY = 1.0 - ((lat - minLat) / (maxLat - minLat))
        return Offset(
            x = (normX * width * 0.82f + width * 0.09f).toFloat(),
            y = (normY * height * 0.70f + height * 0.18f).toFloat()
        )
    }

    Box(
        modifier = modifier.background(Color(0xFFEDE6DA)) // Atmospheric map surface
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. River Ganges curve
            val riverPath = Path().apply {
                val start = project(23.2350, 88.4210, width, height)
                moveTo(start.x, start.y)
                val p1 = project(23.2420, 88.4230, width, height)
                val p2 = project(23.2500, 88.4215, width, height)
                val p3 = project(23.2620, 88.4240, width, height)
                cubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
            }
            drawPath(
                path = riverPath,
                color = Color(0xFF90CAF9),
                style = Stroke(width = 28.dp.toPx(), cap = StrokeCap.Round)
            )

            // 2. Base road network lines
            val mainRoadPath = Path().apply {
                val p1 = project(23.2420, 88.4480, width, height) // Station
                val p2 = project(23.2470, 88.4390, width, height) // Hospital
                val p3 = project(23.2514, 88.4361, width, height) // Shyamchand
                val p4 = project(23.2560, 88.4410, width, height) // Sutragarh
                moveTo(p1.x, p1.y)
                lineTo(p2.x, p2.y)
                lineTo(p3.x, p3.y)
                lineTo(p4.x, p4.y)
            }
            drawPath(
                path = mainRoadPath,
                color = Color(0xFFD7CCC8),
                style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // 3. Glowing Active Route Polyline
            if (route.coordinates.isNotEmpty()) {
                val polylinePath = Path().apply {
                    val first = route.coordinates.first()
                    val p0 = project(first.first, first.second, width, height)
                    moveTo(p0.x, p0.y)
                    for (i in 1 until route.coordinates.size) {
                        val pt = route.coordinates[i]
                        val proj = project(pt.first, pt.second, width, height)
                        lineTo(proj.x, proj.y)
                    }
                }

                // Outer route glow
                drawPath(
                    path = polylinePath,
                    color = Gold500.copy(alpha = 0.4f),
                    style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Core route line (Royal Maroon / Amber)
                drawPath(
                    path = polylinePath,
                    color = Maroon700,
                    style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Directional waypoint dots along route
                for (coord in route.coordinates) {
                    val pt = project(coord.first, coord.second, width, height)
                    drawCircle(color = Color.White, radius = 4.dp.toPx(), center = pt)
                    drawCircle(color = Gold500, radius = 2.5.dp.toPx(), center = pt)
                }
            }

            // 4. Current User Nav Location Pulse Pin
            val userPt = project(currentStep.latitude, currentStep.longitude, width, height)
            drawCircle(
                color = Color(0xFF2196F3).copy(alpha = 0.25f),
                radius = 24.dp.toPx(),
                center = userPt
            )
            drawCircle(
                color = Color.White,
                radius = 12.dp.toPx(),
                center = userPt
            )
            drawCircle(
                color = Color(0xFF1976D2),
                radius = 9.dp.toPx(),
                center = userPt
            )
            // Arrow indicator pointing upward
            val arrowPath = Path().apply {
                moveTo(userPt.x, userPt.y - 7.dp.toPx())
                lineTo(userPt.x + 5.dp.toPx(), userPt.y + 4.dp.toPx())
                lineTo(userPt.x, userPt.y + 1.dp.toPx())
                lineTo(userPt.x - 5.dp.toPx(), userPt.y + 4.dp.toPx())
                close()
            }
            drawPath(path = arrowPath, color = Color.White)

            // 5. Destination Temple / Pandal Pin
            val destCoord = route.coordinates.lastOrNull() ?: (SantipurRepository.SANTIPUR_LAT to SantipurRepository.SANTIPUR_LNG)
            val destPt = project(destCoord.first, destCoord.second, width, height)

            drawCircle(
                color = Gold500.copy(alpha = 0.35f),
                radius = 22.dp.toPx(),
                center = destPt
            )
            drawCircle(
                color = Maroon900,
                radius = 11.dp.toPx(),
                center = destPt
            )
            drawCircle(
                color = Gold400,
                radius = 8.dp.toPx(),
                center = destPt
            )
        }
    }
}
