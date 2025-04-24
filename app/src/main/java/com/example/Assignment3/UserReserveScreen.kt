
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.Assignment3.PreferencesManager
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


fun createRetrofit(token: String?): Retrofit {
    val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(token))
        .build()

    return Retrofit.Builder()
        .baseUrl("https://equipments-api.azurewebsites.net/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserReserveScreen(
    navController: NavController?
) {
    var equipment by remember { mutableStateOf<Equipment?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val userId = preferencesManager.getId()
    val token = preferencesManager.getToken()

    if (token != null) {
        Text(text = "Token: $token")
    } else {
        Text(text = "No token found. Please log in.")
    }
    if (userId != null) {
        LaunchedEffect(userId) {
            coroutineScope.launch {
                try {
                    val retrofit = createRetrofit(token)
                    val service = retrofit.create(EquipmentApiService::class.java)
                    val response = service.getUserById(userId)

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
    } else {
        errorMessage = "User ID not found. Please log in."
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "User")
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                errorMessage != null -> Text(
                    text = errorMessage ?: "Unknown error",
                    modifier = Modifier.align(Alignment.Center)
                )
                equipment == null -> Text(
                    "Equipment not found",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 72.dp) // Space for bottom navigation
                    ) {
                        UserReserveCard(equipment = equipment!!)
                    }
                }
            }
        }
    }
}
@Composable
fun UserReserveCard(equipment: Equipment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserReserveDetailItem(label = "Name", value = equipment.name)
            UserReserveDetailItem(label = "Contact person", value = equipment.contact_person)
            UserReserveDetailItem(label = "Description", value = equipment.description)
            UserReserveDetailItem(label = "Location", value = equipment.location)
            UserReserveDetailItem(label = "Color", value = equipment.color)
            UserReserveDetailItem(label = "Created at", value = equipment.created_at)
            UserReserveDetailItem(label = "Modified at", value = equipment.modified_at)
        }
    }
}

@Composable
fun UserReserveDetailItem(label: String, value: String?) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value ?: "Not specified",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

