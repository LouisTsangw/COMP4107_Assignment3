package com.example.infoday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.infoday.DataStoreInstance.DARK_MODE
import com.example.infoday.ui.theme.InfoDayTheme

//API 36
class MainActivity : ComponentActivity() {
    @Preview(showBackground = true)
    @Composable
    fun DeptPreview() {
        InfoDayTheme(darkTheme = true) {
            DeptScreen(rememberNavController())
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BottomNavBar(navController: NavController) {
        val items = listOf(
            NavItem("Home", "home"),
            NavItem("Events", "dept"),
            NavItem("Itin", "itinerary"),
            NavItem("Map", "map"),
            NavItem("Info", "info")
        )

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationBar {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = item.title) },
                    modifier = Modifier.testTag(item.title),
                    label = { Text(item.title) },
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }

    data class NavItem(val title: String, val route: String)

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

                // Get the title based on current route
                val currentTitle = when {
                    currentRoute?.startsWith("event/") == true -> "Event Details"
                    currentRoute == "home" -> "Home"
                    currentRoute == "dept" -> "Departments"
                    currentRoute == "itinerary" -> "Itinerary"
                    currentRoute == "map" -> "Map"
                    currentRoute == "info" -> "Information"
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
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        composable("home") {
                            Greeting("Android")
                        }
                        composable("info") {
                            InfoScreen()
                        }
                        composable("map") {
                            MapScreen()
                        }
                        composable("dept") { DeptScreen(navController) }
                        composable("event/{deptId}") { backStackEntry ->
                            EventScreen(
                                snackbarHostState,
                                backStackEntry.arguments?.getString("deptId")
                            )
                        }
                        composable("home") { FeedScreen() }
                        composable("itinerary") {
                            ItineraryScreen(snackbarHostState)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InfoDayTheme {
        Greeting("Android")
    }
}