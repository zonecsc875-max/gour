package com.example.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.Gold700
import com.example.ui.theme.Maroon100
import com.example.ui.theme.Maroon700
import com.example.ui.viewmodel.AppLanguage
import com.example.ui.viewmodel.AppScreen

@Composable
fun SantipurBottomBar(
    currentScreen: AppScreen,
    language: AppLanguage,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = language == AppLanguage.BENGALI

    NavigationBar(
        modifier = modifier
            .navigationBarsPadding()
            .testTag("santipur_bottom_navigation"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = NavigationBarDefaults.Elevation
    ) {
        NavigationBarItem(
            selected = currentScreen == AppScreen.HOME,
            onClick = { onNavigate(AppScreen.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text(if (isBn) "হোম" else "Home") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Maroon100,
                selectedIconColor = Maroon700,
                selectedTextColor = Maroon700
            ),
            modifier = Modifier.testTag("nav_item_home")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.INTERACTIVE_MAP,
            onClick = { onNavigate(AppScreen.INTERACTIVE_MAP) },
            icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
            label = { Text(if (isBn) "ম্যাপ" else "Map") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Maroon100,
                selectedIconColor = Maroon700,
                selectedTextColor = Maroon700
            ),
            modifier = Modifier.testTag("nav_item_map")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.DIRECTORY,
            onClick = { onNavigate(AppScreen.DIRECTORY) },
            icon = { Icon(Icons.Default.TempleHindu, contentDescription = "Directory") },
            label = { Text(if (isBn) "মন্দির" else "Directory") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Maroon100,
                selectedIconColor = Maroon700,
                selectedTextColor = Maroon700
            ),
            modifier = Modifier.testTag("nav_item_directory")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.RASH_YATRA_SPECIAL,
            onClick = { onNavigate(AppScreen.RASH_YATRA_SPECIAL) },
            icon = { Icon(Icons.Default.Celebration, contentDescription = "Rash Yatra") },
            label = { Text(if (isBn) "রাসযাত্রা" else "Rash") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Maroon100,
                selectedIconColor = Gold700,
                selectedTextColor = Maroon700
            ),
            modifier = Modifier.testTag("nav_item_rash")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.CROWD_AVOIDANCE ||
                    currentScreen == AppScreen.TRANSPORT_FACILITIES ||
                    currentScreen == AppScreen.ROUTE_PLANNER,
            onClick = { onNavigate(AppScreen.CROWD_AVOIDANCE) },
            icon = { Icon(Icons.Default.Groups, contentDescription = "Crowd & Hub") },
            label = { Text(if (isBn) "ভিড় গাইড" else "Crowd") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Maroon100,
                selectedIconColor = Maroon700,
                selectedTextColor = Maroon700
            ),
            modifier = Modifier.testTag("nav_item_crowd")
        )
    }
}
