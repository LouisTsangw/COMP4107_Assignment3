package com.example.Assignment3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.Assignment3.DataStoreInstance.DARK_MODE
import com.example.Assignment3.ui.theme.InfoDayTheme

class MainActivity : ComponentActivity() {
    @Preview(showBackground = true)
    @Composable
    fun DeptPreview() {
        InfoDayTheme(darkTheme = true) {
            DeptScreen(rememberNavController())
        }
    }

    data class NavItem(val title: String, val route: String)

    @Composable
    fun BottomNavBar(navController: NavController) {
        val items = listOf(
            NavItem("Highlighted Equipments", "HighlightedEquipment"),
            NavItem("Location", "Location"),
            NavItem("Search", "Search"),
            NavItem("User", "User")
        )

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationBar(
            modifier = Modifier.height(56.dp)
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    icon = {},
                    label = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    modifier = Modifier
                        .testTag(item.title)
                        .padding(horizontal = 4.dp),
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
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkMode by DataStoreInstance.getBooleanPreferences(this, DARK_MODE)
                .collectAsState(initial = false)
            val snackbarHostState = remember { SnackbarHostState() }

            InfoDayTheme(darkTheme = darkMode == true) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val currentTitle = when {
                    currentRoute == "HighlightedEquipment" -> "Highlighted Equipments"
                    currentRoute == "Location" -> "Location"
                    currentRoute == "Search" -> "Search"
                    currentRoute == "User" -> "User"
                    else -> ""
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text(currentTitle) },
                            navigationIcon = {
                                if (currentRoute?.startsWith("event/") == true) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            },
                        )
                    },
                    bottomBar = { BottomNavBar(navController = navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "HighlightedEquipment",
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        composable("HighlightedEquipment") {
//                          HighlightedEquipmentScreen()
                        }
                        composable("Location") {
                            MapScreen()
                        }
                        composable("search") {
                            Text("Search Screen")
                        }
                        composable("user") {
                            Text("User Profile Screen")
                        }
                    }
                }
            }
        }
    }
}