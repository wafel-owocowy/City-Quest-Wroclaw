package com.example.city_quest_wroclaw.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.city_quest_wroclaw.ui.screens.CollectionScreen
import com.example.city_quest_wroclaw.ui.screens.MapScreen
import com.example.city_quest_wroclaw.ui.screens.ObjectDetailsScreen
import com.example.city_quest_wroclaw.ui.screens.ProfileScreen
import com.example.city_quest_wroclaw.ui.screens.SettingsScreen
import com.example.city_quest_wroclaw.ui.screens.StartScreen
import com.example.city_quest_wroclaw.viewmodel.CityQuestViewModel

sealed class Screen(val route: String) {
    object Start : Screen("start_screen")
    object Map : Screen("map_screen")
    object ObjectDetails : Screen("object_details_screen/{attractionId}") {
        fun createRoute(attractionId: Int) = "object_details_screen/$attractionId"
    }
    object Profile : Screen("profile_screen")
    object Collection : Screen("collection_screen")
    object Settings : Screen("settings_screen")
}

sealed class BottomNavItem(val screen: Screen, val title: String, val icon: ImageVector) {
    object Map : BottomNavItem(Screen.Map, "Map", Icons.Default.LocationOn)
    object Profile : BottomNavItem(Screen.Profile, "Profile", Icons.Default.Person)
    object Collection : BottomNavItem(Screen.Collection, "Collections", Icons.AutoMirrored.Filled.List)
    object Settings : BottomNavItem(Screen.Settings, "Settings", Icons.Default.Settings)
}

@Composable
fun Navigation(navController: NavHostController, viewModel: CityQuestViewModel) {
    val items = listOf(
        BottomNavItem.Map,
        BottomNavItem.Profile,
        BottomNavItem.Collection,
        BottomNavItem.Settings
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Show bottom bar only on main tabs
    val showBottomBar = items.any { it.screen.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Start.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Start.route) {
                StartScreen(
                    onStartClick = { navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Start.route) { inclusive = true }
                    } }
                )
            }
            composable(Screen.Map.route) {
                MapScreen(
                    viewModel = viewModel,
                    onAttractionClick = { attractionId ->
                        navController.navigate(Screen.ObjectDetails.createRoute(attractionId))
                    }
                )
            }
            composable(
                route = Screen.ObjectDetails.route,
                arguments = listOf(navArgument("attractionId") { type = NavType.IntType })
            ) { backStackEntry ->
                val attractionId = backStackEntry.arguments?.getInt("attractionId") ?: return@composable
                ObjectDetailsScreen(
                    attractionId = attractionId,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.Collection.route) {
                CollectionScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
