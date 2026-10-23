package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Place
import com.example.data.model.PlaceCategory
import com.example.data.repository.SantipurRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage
import kotlin.math.max
import kotlin.math.min

@Composable
fun SantipurCanvasMap(
    places: List<Place>,
    selectedPlace: Place?,
    onPlaceSelect: (Place) -> Unit,
    userLat: Double,
    userLng: Double,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = language == AppLanguage.BENGALI

    // Geographic bounding box for Santipur town
    val minLat = 23.2350
    val maxLat = 23.2620
    val minLng = 88.4200
    val maxLng = 88.4550

    var zoom by remember { mutableStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    // Map screen projection helper
    fun projectCoordinates(lat: Double, lng: Double, width: Float, height: Float): Offset {
        val normX = (lng - minLng) / (maxLng - minLng)
        val normY = 1.0 - ((lat - minLat) / (maxLat - minLat)) // Inverted Y for screen coordinates

        val rawX = (normX * width * 0.85f + width * 0.08f).toFloat()
        val rawY = (normY * height * 0.85f + height * 0.08f).toFloat()

        return Offset(
            x = (rawX * zoom) + panOffset.x,
            y = (rawY * zoom) + panOffset.y
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF3EEE6)) // Natural parchment map background
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, gestureZoom, _ ->
                        zoom = (zoom * gestureZoom).coerceIn(0.7f, 3.5f)
                        panOffset = Offset(
                            x = panOffset.x + pan.x,
                            y = panOffset.y + pan.y
                        )
                    }
                }
                .pointerInput(places, zoom, panOffset) {
                    detectTapGestures { tapOffset ->
                        val width = size.width.toFloat()
                        val height = size.height.toFloat()

                        // Find closest place to tap within 32dp
                        val tapRadius = 32.dp.toPx()
                        var closestPlace: Place? = null
                        var minDistance = Float.MAX_VALUE

                        for (place in places) {
                            val projected = projectCoordinates(place.latitude, place.longitude, width, height)
                            val dist = (projected - tapOffset).getDistance()
                            if (dist < tapRadius && dist < minDistance) {
                                minDistance = dist
                                closestPlace = place
                            }
                        }

                        if (closestPlace != null) {
                            onPlaceSelect(closestPlace)
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            // 1. Draw River Ganges (Bhagirathi River) on western boundary
            val riverPath = Path().apply {
                val start = projectCoordinates(23.2350, 88.4210, width, height)
                moveTo(start.x, start.y)
                val p1 = projectCoordinates(23.2420, 88.4230, width, height)
                val p2 = projectCoordinates(23.2500, 88.4215, width, height)
                val p3 = projectCoordinates(23.2620, 88.4240, width, height)
                cubicTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
            }
            drawPath(
                path = riverPath,
                color = Color(0xFF90CAF9),
                style = Stroke(width = 24.dp.toPx() * zoom)
            )

            // 2. Draw Main Road Arterials of Santipur
            // Arterial 1: Station Road towards Central Town
            val stationRoad = Path().apply {
                val pStn = projectCoordinates(23.2420, 88.4480, width, height)
                val pMore = projectCoordinates(23.2470, 88.4390, width, height)
                val pCenter = projectCoordinates(23.2505, 88.4344, width, height)
                moveTo(pStn.x, pStn.y)
                lineTo(pMore.x, pMore.y)
                lineTo(pCenter.x, pCenter.y)
            }
            drawPath(
                path = stationRoad,
                color = Color(0xFFD7CCC8),
                style = Stroke(width = 6.dp.toPx() * zoom)
            )

            // Arterial 2: Netaji Subhash Road & Sutragarh Road (Rash Procession Corridor)
            val nsRoad = Path().apply {
                val pBabla = projectCoordinates(23.2380, 88.4250, width, height)
                val pGoswami = projectCoordinates(23.2482, 88.4320, width, height)
                val pShyam = projectCoordinates(23.2514, 88.4361, width, height)
                val pSutra = projectCoordinates(23.2560, 88.4410, width, height)
                moveTo(pBabla.x, pBabla.y)
                lineTo(pGoswami.x, pGoswami.y)
                lineTo(pShyam.x, pShyam.y)
                lineTo(pSutra.x, pSutra.y)
            }
            drawPath(
                path = nsRoad,
                color = Gold500.copy(alpha = 0.5f),
                style = Stroke(width = 5.dp.toPx() * zoom)
            )

            // 3. User Location Indicator (if in proximity)
            val userPoint = projectCoordinates(userLat, userLng, width, height)
            drawCircle(
                color = Color(0xFF2196F3).copy(alpha = 0.25f),
                radius = 16.dp.toPx() * zoom,
                center = userPoint
            )
            drawCircle(
                color = Color(0xFF1976D2),
                radius = 6.dp.toPx() * zoom,
                center = userPoint
            )
            drawCircle(
                color = Color.White,
                radius = 2.5.dp.toPx() * zoom,
                center = userPoint
            )

            // 4. Draw Place Markers
            for (place in places) {
                val pt = projectCoordinates(place.latitude, place.longitude, width, height)
                val isSelected = selectedPlace?.id == place.id

                val markerColor = when (place.category) {
                    PlaceCategory.RASH_YATRA -> Gold700
                    PlaceCategory.KALI_PUJA -> VermilionRed
                    PlaceCategory.HERITAGE_TEMPLE -> Terracotta
                    PlaceCategory.TRANSPORT -> Color(0xFF1976D2)
                    PlaceCategory.FACILITY -> Color(0xFF388E3C)
                    else -> Maroon700
                }

                // Outer selection halo
                if (isSelected) {
                    drawCircle(
                        color = markerColor.copy(alpha = 0.35f),
                        radius = 18.dp.toPx() * zoom,
                        center = pt
                    )
                }

                // Main pin body
                drawCircle(
                    color = Color.White,
                    radius = 9.dp.toPx() * zoom,
                    center = pt
                )
                drawCircle(
                    color = markerColor,
                    radius = 7.dp.toPx() * zoom,
                    center = pt
                )
            }
        }

        // Map Control Floating Buttons (Reset / Zoom in / Zoom out)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilledTonalIconButton(
                onClick = { zoom = min(zoom + 0.3f, 3.5f) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In")
            }
            FilledTonalIconButton(
                onClick = { zoom = max(zoom - 0.3f, 0.7f) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
            }
            FilledTonalIconButton(
                onClick = {
                    zoom = 1.0f
                    panOffset = Offset.Zero
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Center Map")
            }
        }

        // River Label on bottom left
        Surface(
            color = Color.White.copy(alpha = 0.85f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF90CAF9))
                )
                Text(
                    text = if (isBn) "ভাগীরথী নদী (গঙ্গা)" else "Bhagirathi River (Ganges)",
                    style = MaterialTheme.typography.labelSmall,
                    color = Maroon900
                )
            }
        }

        // Selected Place Bottom Card Overlay
        if (selectedPlace != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBn) selectedPlace.nameBn else selectedPlace.nameEn,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Maroon900
                        )
                        Text(
                            text = "${selectedPlace.locality} • ${if (isBn) selectedPlace.crowdStatus.labelBn else selectedPlace.crowdStatus.labelEn}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            val uri = Uri.parse("geo:${selectedPlace.latitude},${selectedPlace.longitude}?q=${selectedPlace.latitude},${selectedPlace.longitude}(${Uri.encode(selectedPlace.nameEn)})")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Maroon700),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isBn) "যান" else "Go", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
