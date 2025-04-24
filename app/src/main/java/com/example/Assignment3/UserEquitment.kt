
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.Assignment3.PreferencesManager
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEquipmentScreen(
    navController: NavController?,
) {
    var equipmentList by remember { mutableStateOf<List<Equipment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val token = preferencesManager.getToken()

    // Fetch user's reserved equipment list
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://equipments-api.azurewebsites.net/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service = retrofit.create(UserEquipmentApiService::class.java)
                val response = service.getEquipments()

                if (response.isSuccessful) {
                    val reservedIds = preferencesManager.getReservedEquipmentIds()
                    val allEquipments = response.body()?.equipments ?: emptyList()
                    equipmentList = allEquipments.filter { equipment ->
                        reservedIds.contains(equipment._id)
                    }
                } else {
                    errorMessage = "Failed to load equipment: ${response.code()}"
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
                title = { Text("User") },
                navigationIcon = {
                    IconButton(onClick = {
                        preferencesManager.logout()
                        navController?.navigate("user") {
                            popUpTo(0)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Logout and return to login"
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
                errorMessage != null -> Text(
                    text = errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error
                )
                equipmentList.isEmpty() -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "You haven't reserved any equipment yet",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Reserve equipment to see them here",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                else -> UserEquipmentList(equipmentList, navController)
            }
        }
    }
}

@Composable
fun UserEquipmentList(
    equipments: List<Equipment>,
    navController: NavController?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(equipments) { equipment ->
            UserEquipmentCard(equipment = equipment, navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEquipmentCard(
    equipment: Equipment,
    navController: NavController?
) {
    Card(
        onClick = {
            navController?.navigate("equipment/${equipment._id}?from=myreservations")
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = equipment.name ?: "Unnamed Equipment",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = equipment.location ?: "Location not specified",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Contact: ${equipment.contact_person ?: "Not available"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}





interface UserEquipmentApiService {
    @POST("equipments/{equipmentId}/rent")
    suspend fun reserveEquipment(
        @Path("equipmentId") equipmentId: String,  // 使用正确的路径参数名
        @Body reservationRequest: ReservationRequest,
        @Header("Authorization") token: String  // 添加认证头
    ): Response<ReservationRequest>

    @GET("equipments")
    suspend fun getEquipments(): Response<UserEquipmentListResponse>

    @GET("equipments/{id}")
    suspend fun getEquipmentById(@Path("id") id: String): Response<UserEquipment>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<UserEquipment>
}


data class UserEquipment(
    @SerializedName("_id")
    val _id: String,

    @SerializedName("name")
    val name: String?,

    @SerializedName("location")
    val location: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("image")
    val image: String?,

    @SerializedName("contact_person")
    val contact_person: String?,

    @SerializedName("color")
    val color: String?,

    @SerializedName("highlight")
    val highlight: Boolean?,

    @SerializedName("created_at")
    val created_at: String?,

    @SerializedName("modified_at")
    val modified_at: String?
)

data class UserEquipmentListResponse(
    val equipments: List<Equipment>
)