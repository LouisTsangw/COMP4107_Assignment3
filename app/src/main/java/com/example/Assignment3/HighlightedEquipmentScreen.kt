
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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighlightedEquipmentScreen(
    navController: NavController?
) {
    var equipmentList by remember { mutableStateOf<List<Equipment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch equipment list
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
                navigationIcon = {}
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
                else -> EquipmentList(equipmentList, navController)
            }
        }
    }
}

@Composable
fun EquipmentList(equipments: List<Equipment>, navController: NavController?) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(equipments) { equipment ->
            EquipmentCard(equipment = equipment, navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentCard(equipment: Equipment, navController: NavController?) {
    Card(
        onClick = {
            navController?.navigate("equipment/${equipment._id}")
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
                text = equipment.contact_person ?: "No contact",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
// API 服务接口
interface EquipmentApiService {
    @GET("equipments")
    suspend fun getEquipments(): Response<EquipmentListResponse>

    @GET("equipments/{id}")
    suspend fun getEquipmentById(@Path("id") id: String): Response<Equipment>
}

// 数据类
data class Equipment(
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

data class EquipmentListResponse(
    val equipments: List<Equipment>
)