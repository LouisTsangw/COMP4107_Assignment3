
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
fun EquipmentDetailScreen(
    equipmentId: String,
    navController: NavController?
) {
    var equipment by remember { mutableStateOf<Equipment?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch equipment details
    LaunchedEffect(equipmentId) {
        coroutineScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://equipments-api.azurewebsites.net/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service = retrofit.create(EquipmentApiService::class.java)
                val response = service.getEquipmentById(equipmentId)

                if (response.isSuccessful) {
                    equipment = response.body()
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
                title = { Text("Equipment Details") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
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
                equipment == null -> Text("Equipment not found")
                else -> EquipmentDetailContent(equipment = equipment!!)
            }
        }
    }
}

@Composable
fun EquipmentDetailContent(equipment: Equipment) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Name: ${equipment.name ?: "Not specified"}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Contact: ${equipment.contact_person ?: "Not specified"}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Description: ${equipment.description ?: "Not specified"}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Location: ${equipment.location ?: "Not specified"}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Color: ${equipment.color ?: "Not specified"}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Created: ${equipment.created_at ?: "Not specified"}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Modified: ${equipment.modified_at ?: "Not specified"}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}


