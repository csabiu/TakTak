package com.taktak.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.taktak.app.ui.navigation.Screen
import com.taktak.app.ui.navigation.TakTakNavigation
import com.taktak.app.ui.theme.TakTakTheme

class MainActivity : ComponentActivity() {

    // Request permission launcher for notifications
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle permission result if needed
        if (!isGranted) {
            // User denied permission - notifications won't work
            // Could show a dialog explaining why notifications are needed
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val repository = (application as TakTakApplication).repository

        setContent {
            TakTakTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (shouldShowBottomBar(currentRoute)) {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Restaurant, contentDescription = null) },
                                    label = { Text("레시피") },
                                    selected = currentRoute?.startsWith("recipe") == true,
                                    onClick = {
                                        navController.navigate(Screen.Recipes.route) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Science, contentDescription = null) },
                                    label = { Text("발효중") },
                                    selected = currentRoute?.startsWith("batch") == true,
                                    onClick = {
                                        navController.navigate(Screen.Batches.route) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.WineBar, contentDescription = null) },
                                    label = { Text("시음") },
                                    selected = currentRoute?.startsWith("tasting") == true,
                                    onClick = {
                                        navController.navigate(Screen.TastingNotes.route) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        TakTakNavigation(
                            navController = navController,
                            repository = repository
                        )
                    }
                }
            }
        }
    }

    private fun shouldShowBottomBar(route: String?): Boolean {
        return route in listOf(
            Screen.Recipes.route,
            Screen.Batches.route,
            Screen.TastingNotes.route
        )
    }
}
