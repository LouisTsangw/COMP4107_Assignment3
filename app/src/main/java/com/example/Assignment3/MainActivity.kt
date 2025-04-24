package com.example.Assignment3

import EquipmentDetailScreen
import HighlightedEquipmentScreen
import LocationScreen
import SearchEquipmentScreen
import UserReserveScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.Assignment3.DataStoreInstance.DARK_MODE
import com.example.Assignment3.MainActivity.NavItem
import com.example.Assignment3.ui.theme.InfoDayTheme

class MainActivity : ComponentActivity() {
    @Preview(showBackground = true)
    @Composable
    fun DeptPreview() {
        InfoDayTheme(darkTheme = true) {
            // Preview content here
        }
    }

    data class NavItem(val title: String, val route: String, val icon: ImageVector)



    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkMode by DataStoreInstance.getBooleanPreferences(this, DARK_MODE)
                .collectAsState(initial = false)
            val snackbarHostState = remember { SnackbarHostState() }

            InfoDayTheme(darkTheme = darkMode == true) {
                val navController = rememberNavController()
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                    },
                    bottomBar = { BottomNavBar(navController = navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "highlightedequipment",
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        composable("highlightedequipment?location={location}") { backStackEntry ->
                            val location = backStackEntry.arguments?.getString("location")
                            HighlightedEquipmentScreen(navController, location = location)
                        }
                        composable(
                            "equipment/{equipmentId}?from={from}",
                            arguments = listOf(
                                navArgument("equipmentId") { type = NavType.StringType },
                                navArgument("from") {
                                    type = NavType.StringType
                                    nullable = true
                                    defaultValue = null
                                }
                            )
                        ) { backStackEntry ->
                            EquipmentDetailScreen(
                                equipmentId = backStackEntry.arguments?.getString("equipmentId")!!,
                                navController = navController,
                                from = backStackEntry.arguments?.getString("from")
                            )
                        }
                        composable("location") {
                            LocationScreen(navController)
                        }
                        composable("search") {
                            SearchEquipmentScreen(navController)
                        }
                        composable("user") {
                            LoginScreen(
                                onLoginSuccess = { navController.navigate("highlightedequipment") },
                                onNavigateToRegister = { navController.navigate("register") }
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = { navController.navigate("user") },
                                onNavigateToLogin= { navController.navigate("user") },
                                onBackPressed= { navController.navigate("user") }
                            )
                        }
                        composable("userreserve") { UserReserveScreen(navController) }
                        composable("auth") {
                            AuthScreen(
                                onLoginSuccess = { navController.navigate("highlightedequipment") },
                                onRegisterSuccess = { navController.navigate("highlightedequipment") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        NavItem("Highlighted Equipments", "highlightedequipment", Icons.Filled.Home),
        NavItem("Location", "location", Icons.Filled.LocationOn),
        NavItem("Search", "search", Icons.Filled.Search),
        NavItem("User", "user", Icons.Filled.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier
            .height(100.dp)
            .background(Color.Black),
        containerColor = Color.Black
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(36.dp),
                        tint = Color.White
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.DarkGray,
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.White
                )
            )
        }
    }
}