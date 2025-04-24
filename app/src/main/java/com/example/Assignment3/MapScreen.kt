
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController?
) {
    var equipmentList by remember { mutableStateOf<List<Equipment>>(emptyList()) }
    var filteredEquipmentList by remember { mutableStateOf<List<Equipment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://equipments-api.azurewebsites.net/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service = retrofit.create(EquipmentApiService::class.java)
                val response = service.getEquipments()

                if (response.isSuccessful) {
                    equipmentList = response.body()?.equipments?.filter { it.highlight == true } ?: emptyList()
                } else {
                    errorMessage = "Failed to load: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Highlighted Equipments") },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigate("HighlightedEquipment") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to List"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                errorMessage != null -> Text(text = errorMessage ?: "Unknown error")
                equipmentList.isEmpty() -> Text("No highlighted equipment found")
                else -> LocationList(equipmentList, navController) { selectedLocation ->
                    filteredEquipmentList = equipmentList.filter { it.location == selectedLocation }
                }
            }
        }
    }
}

@Composable
fun LocationList(equipments: List<Equipment>, navController: NavController?, onLocationSelected: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(locations.distinct()) { location ->
            LocationCard(
                location = location,
                onClick = {
                    onLocationSelected(location)
                    val equipmentForNav = equipments.firstOrNull { it.location == location }
                    equipmentForNav?.let {
                        // Pass the location name as an argument to the details screen
                        navController?.navigate("equipment/${it._id}")
                    }
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationCard(location: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Text(
            text = location,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
    }
}


private val locations = listOf(
    "Street", "Place", "Center", "Drive", "Avenue", "Trail", "Park", "Road",
    "Plaza", "Hill", "Parkway", "Crossing", "Pass", "Court", "Circle",
    "Lane", "Terrace", "Alley", "Point", "Way"
)