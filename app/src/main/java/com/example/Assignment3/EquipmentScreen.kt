

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentDetailScreen(
    equipmentId: String,
    navController: NavController?,
    from: String? = null
) {
    var equipment by remember { mutableStateOf<Equipment?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var reservationMessage by remember { mutableStateOf<String?>(null) }
    var isReserving by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val token = preferencesManager.getToken()
    val coroutineScope = rememberCoroutineScope()

    var isReservedLocally by remember {
        mutableStateOf(preferencesManager.isEquipmentReserved(equipmentId))
    }

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

    fun handleReserve() {
        isReserving = true
        coroutineScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://equipments-api.azurewebsites.net/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkHttpClient.Builder()
                        .addInterceptor { chain ->
                            chain.proceed(chain.request().newBuilder()
                                .header("Authorization", "Bearer $token")
                                .build())
                        }
                        .build())
                    .build()

                val service = retrofit.create(EquipmentApiService::class.java)
                val request = ReservationRequest(
                    startDate = "2900-12-29",
                    returnDate = "2900-12-30",
                    reservationId = equipmentId
                )

                Log.d("Reservation", "Attempting to reserve equipment $equipmentId")
                val response = service.reserveEquipment(
                    equipmentId = equipmentId,
                    reservationRequest = request,
                    token = "Bearer $token"
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        preferencesManager.addReservedEquipment(equipmentId)
                        isReservedLocally = true
                        reservationMessage = "Reservation successful!"
                    } else {
                        val errorMsg = when (response.code()) {
                            404 -> "Equipment not found (ID: $equipmentId)"
                            401 -> "Please login again"
                            else -> response.errorBody()?.string() ?: "Unknown error"
                        }
                        reservationMessage = "Failed to reserve: $errorMsg"
                        Log.e("Reservation", "API error: $errorMsg")
                    }
                    isReserving = false
                }
            } catch (e: Exception) {
                reservationMessage = "Error: ${e.localizedMessage}"
                isReserving = false
            }
        }
    }

    fun handleUnreserve() {
        isReserving = true
        coroutineScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://equipments-api.azurewebsites.net/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(OkHttpClient.Builder()
                        .addInterceptor { chain ->
                            chain.proceed(chain.request().newBuilder()
                                .header("Authorization", "Bearer $token")
                                .build())
                        }
                        .build())
                    .build()

                val service = retrofit.create(EquipmentApiService::class.java)

                Log.d("Reservation", "Attempting to cancel reservation for equipment $equipmentId")
                val response = service.cancelReservation(
                    equipmentId = equipmentId,
                    token = "Bearer $token"
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        preferencesManager.removeReservedEquipment(equipmentId)
                        isReservedLocally = false
                        reservationMessage = "Reservation canceled successfully"
                    } else {
                        val errorMsg = when (response.code()) {
                            404 -> "Reservation not found"
                            401 -> "Please login again"
                            else -> response.errorBody()?.string() ?: "Unknown error"
                        }
                        reservationMessage = "Failed to cancel reservation: $errorMsg"
                        Log.e("Reservation", "API error: $errorMsg")
                    }
                    isReserving = false
                }
            } catch (e: Exception) {
                reservationMessage = "Error: ${e.localizedMessage}"
                isReserving = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (from) {
                            "location" -> "Location"
                            "search" -> "Search"
                            "myreservations" -> "User"
                            else -> "Highlighted Equipments"
                        }
                    )
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
                            .padding(bottom = 72.dp)
                    ) {
                        EquipmentDetailCard(equipment = equipment!!)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (token != null) {
                    if (isReservedLocally) {
                        Button(
                            onClick = { handleUnreserve() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isReserving
                        ) {
                            if (isReserving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("UnReserve")
                            }
                        }
                    } else {
                        Button(
                            onClick = { handleReserve() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isReserving
                        ) {
                            if (isReserving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Reserve")
                            }
                        }
                    }
                } else {

                }
                Spacer(modifier = Modifier.height(8.dp))
                reservationMessage?.let {
                    Text(
                        text = it,
                        color = if (it.startsWith("Reservation")) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EquipmentDetailCard(equipment: Equipment) {
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
            DetailItem(label = "Name", value = equipment.name)
            DetailItem(label = "Contact person", value = equipment.contact_person)
            DetailItem(label = "Description", value = equipment.description)
            DetailItem(label = "Location", value = equipment.location)
            DetailItem(label = "Color", value = equipment.color)
            DetailItem(label = "Created at", value = equipment.created_at)
            DetailItem(label = "Modified at", value = equipment.modified_at)
        }
    }
}

@Composable
fun DetailItem(label: String, value: String?) {
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

