
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
import androidx.compose.material3.Button
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

    LaunchedEffect(userId) {
        if (userId == null || token.isNullOrEmpty()) {
            errorMessage = "Please log in first"
            isLoading = false
            return@LaunchedEffect
        }

        coroutineScope.launch {
            try {
                val retrofit = createRetrofit(token)
                val service = retrofit.create(EquipmentApiService::class.java)
                val response = service.getUserById(userId)

                if (response.isSuccessful) {
                    equipment = response.body()
                } else {
                    errorMessage = when (response.code()) {
                        401 -> "Unauthorized: Please log in again"
                        404 -> "User not found"
                        else -> "Failed to load user data (${response.code()})"
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Network error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                errorMessage != null -> Text(
                    errorMessage ?: "Error",
                    Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
                equipment != null -> {
                    Column(Modifier.fillMaxSize()) {
                        UserReserveCard(equipment = equipment!!)
                        Spacer(modifier = Modifier.weight(1f))
                        UnreserveButton()
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

@Composable
fun UnreserveButton() {
    Button(
        onClick = { /* Handle unreserve action */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Unreserve")
    }
}