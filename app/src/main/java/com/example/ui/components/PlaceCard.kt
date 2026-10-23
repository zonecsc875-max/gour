package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CrowdStatus
import com.example.data.model.Place
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppLanguage

@Composable
fun PlaceCard(
    place: Place,
    distanceKm: Double?,
    isSaved: Boolean,
    language: AppLanguage,
    onCardClick: () -> Unit,
    onSaveToggle: () -> Unit,
    onNavigateClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = language == AppLanguage.BENGALI

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("place_card_${place.id}")
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Category chip, Verified pill & Bookmark button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Maroon100,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isBn) place.category.titleBn else place.category.titleEn,
                            style = MaterialTheme.typography.labelMedium,
                            color = Maroon800,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (place.isVerified) {
                        Surface(
                            color = StatusGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = StatusGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (isBn) "যাচাইকৃত" else "Verified",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StatusGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onSaveToggle,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("save_button_${place.id}")
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isSaved) "Remove from saved" else "Save place",
                        tint = if (isSaved) Gold700 else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Place Title & Transliteration
            Text(
                text = if (isBn) place.nameBn else place.nameEn,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Maroon900,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = if (isBn) place.nameEn else place.nameBn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Locality & Distance Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Terracotta,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = place.locality,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (distanceKm != null) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isBn) "${distanceKm} কিমি" else "${distanceKm} km",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = Gold900
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description summary
            Text(
                text = if (isBn) place.descriptionBn else place.descriptionEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Crowd status & Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Crowd indicator pill
                val (crowdColor, crowdBg) = when (place.crowdStatus) {
                    CrowdStatus.LOW -> StatusGreen to StatusGreen.copy(alpha = 0.12f)
                    CrowdStatus.MODERATE -> StatusAmber to StatusAmber.copy(alpha = 0.15f)
                    CrowdStatus.HIGH -> StatusRed to StatusRed.copy(alpha = 0.15f)
                    CrowdStatus.UNKNOWN -> Color.Gray to Color.LightGray.copy(alpha = 0.2f)
                }

                Surface(
                    color = crowdBg,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(crowdColor)
                        )
                        Text(
                            text = if (isBn) place.crowdStatus.labelBn else place.crowdStatus.labelEn,
                            style = MaterialTheme.typography.labelSmall,
                            color = crowdColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Action buttons: Navigate & Details
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalButton(
                        onClick = {
                            if (onNavigateClick != null) {
                                onNavigateClick()
                            } else {
                                val uri = Uri.parse("geo:${place.latitude},${place.longitude}?q=${place.latitude},${place.longitude}(${Uri.encode(place.nameEn)})")
                                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(mapIntent)
                            }
                        },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("navigate_button_${place.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Directions,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBn) "পথনির্দেশ" else "Navigate",
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onCardClick,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Maroon700,
                            contentColor = Gold100
                        ),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("details_button_${place.id}")
                    ) {
                        Text(
                            text = if (isBn) "বিস্তারিত" else "Details",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
