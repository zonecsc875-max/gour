package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.ui.components.SantipurBottomBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SantipurViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: SantipurViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SantipurApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SantipurApp(
    viewModel: SantipurViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Location Permission Request Flow
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            viewModel.updateLocation(23.2505, 88.4344, true)
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Toast message handling via Material 3 SnackbarHost
    LaunchedEffect(uiState.toastMessage) {
        val msg = uiState.toastMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearToast()
        }
    }

    // Predictive back / BackHandler
    BackHandler(enabled = uiState.navigationStack.size > 1) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (uiState.currentScreen != AppScreen.IN_APP_NAVIGATION) {
                SantipurBottomBar(
                    currentScreen = uiState.currentScreen,
                    language = uiState.language,
                    onNavigate = { screen ->
                        if (uiState.currentScreen != screen) {
                            viewModel.navigateTo(screen)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = uiState.currentScreen,
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                    AppScreen.FESTIVALS_EXPLORER -> FestivalsExplorerScreen(viewModel = viewModel)
                    AppScreen.RASH_YATRA_SPECIAL -> RashYatraScreen(viewModel = viewModel)
                    AppScreen.KALI_PUJA_SPECIAL -> KaliPujaScreen(viewModel = viewModel)
                    AppScreen.DIRECTORY -> DirectoryScreen(viewModel = viewModel)
                    AppScreen.PLACE_DETAIL -> PlaceDetailScreen(viewModel = viewModel)
                    AppScreen.INTERACTIVE_MAP -> InteractiveMapScreen(viewModel = viewModel)
                    AppScreen.CROWD_AVOIDANCE -> CrowdAvoidanceScreen(viewModel = viewModel)
                    AppScreen.ROUTE_PLANNER -> RoutePlannerScreen(viewModel = viewModel)
                    AppScreen.CALENDAR -> CalendarScreen(viewModel = viewModel)
                    AppScreen.TRANSPORT_FACILITIES -> TransportFacilitiesScreen(viewModel = viewModel)
                    AppScreen.HERITAGE_ARTICLES -> HeritageArticlesScreen(viewModel = viewModel)
                    AppScreen.SAVED_PLACES -> SavedPlacesScreen(viewModel = viewModel)
                    AppScreen.CORRECTION_FORM -> CorrectionScreen(viewModel = viewModel)
                    AppScreen.ADMIN_PANEL -> AdminScreen(viewModel = viewModel)
                    AppScreen.ABOUT_SANTIPUR -> AboutScreen(viewModel = viewModel)
                    AppScreen.IN_APP_NAVIGATION -> InAppNavigationScreen(viewModel = viewModel)
                    AppScreen.RAI_RAJA_STORY -> RaiRajaStoryScreen(viewModel = viewModel)
                    AppScreen.BAMAKALI_DANCE_EVENT -> BamakaliDanceScreen(viewModel = viewModel)
                    AppScreen.RASH_HERITAGE_DIRECTORY -> RashHeritageDirectoryScreen(viewModel = viewModel)
                }
            }
        }
    }
}
