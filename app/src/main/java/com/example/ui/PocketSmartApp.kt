package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary

enum class Screen(val title: String, val icon: ImageVector) {
    Home("Explore", Icons.Default.Home),
    Interior("Interior", Icons.Default.Weekend),
    Party("Party", Icons.Default.Celebration),
    Jewelry("Jewelry", Icons.Default.Diamond),
    History("History", Icons.Default.History),
    Settings("Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketSmartApp(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(Screen.Home) }
    val snackbarHostState = remember { SnackbarHostState() }
    val uiMessage by viewModel.uiMessage.collectAsStateWithLifecycle()

    // Handle back button when on sub-screens
    if (currentScreen != Screen.Home) {
        BackHandler {
            currentScreen = Screen.Home
        }
    }

    LaunchedEffect(uiMessage) {
        uiMessage?.let { msg ->
            when (msg) {
                is UiMessage.Success -> snackbarHostState.showSnackbar(
                    message = msg.message,
                    duration = SnackbarDuration.Short
                )
                is UiMessage.Error -> snackbarHostState.showSnackbar(
                    message = msg.message,
                    duration = SnackbarDuration.Long
                )
            }
            viewModel.clearUiMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Text(
                            text = "⚡ PocketSmart ",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Text(
                            text = "AI",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = BrandSecondary
                            )
                        )
                    }
                },
                navigationIcon = {
                    if (currentScreen != Screen.Home) {
                        IconButton(onClick = { currentScreen = Screen.Home }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Home"
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { currentScreen = Screen.Settings },
                        modifier = Modifier.testTag("action_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (currentScreen == Screen.Settings) BrandPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                val navItems = listOf(Screen.Home, Screen.Interior, Screen.Party, Screen.Jewelry, Screen.History)
                navItems.forEach { screen ->
                    NavigationBarItem(
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (currentScreen == screen) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}"),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandPrimary,
                            selectedTextColor = BrandPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Home -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToInterior = { currentScreen = Screen.Interior },
                    onNavigateToParty = { currentScreen = Screen.Party },
                    onNavigateToJewelry = { currentScreen = Screen.Jewelry },
                    onNavigateToHistory = { currentScreen = Screen.History }
                )
                Screen.Interior -> InteriorPlannerScreen(viewModel = viewModel)
                Screen.Party -> PartyPlannerScreen(viewModel = viewModel)
                Screen.Jewelry -> JewelryPlannerScreen(viewModel = viewModel)
                Screen.History -> HistoryScreen(viewModel = viewModel)
                Screen.Settings -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
