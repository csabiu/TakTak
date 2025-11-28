package com.taktak.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.taktak.app.ui.navigation.Screen
import com.taktak.app.ui.navigation.TakTakNavigation
import com.taktak.app.ui.theme.TakTakTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

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
                                    label = { Text("Recipes") },
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
                                    label = { Text("Batches") },
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
                                    label = { Text("Tastings") },
                                    selected = currentRoute?.startsWith("tasting") == true,
                                    onClick = {
                                        navController.navigate(Screen.TastingNotes.route) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Book, contentDescription = null) },
                                    label = { Text("Journal") },
                                    selected = currentRoute?.startsWith("journal") == true ||
                                             currentRoute?.startsWith("add_edit_journal") == true,
                                    onClick = {
                                        navController.navigate(Screen.Journal.route) {
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
            Screen.TastingNotes.route,
            Screen.Journal.route
        )
    }
}
