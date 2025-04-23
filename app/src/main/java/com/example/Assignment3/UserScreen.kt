package com.example.infoday.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Data classes for API requests/responses
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(
    val email: String,
    val password: String,
    val contact: String,
    val department: String,
    val remark: String,
    val isAdmin: Boolean
)
data class AuthResponse(
    val token: String? = null,  // 唯一必需字段
    val success: Boolean = true, // 添加默认值
    val message: String? = null  // 可选字段
)
data class UserData(
    val _id: String,
    val email: String,
    val contact: String?,
    val department: String?,
    val remark: String?,
    val isAdmin: Boolean
)

// API Service interface
interface AuthApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("users")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
}


// Create Retrofit instance
private val retrofit = Retrofit.Builder()
    .baseUrl("https://equipments-api.azurewebsites.net/api/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build())
    .build()

private val authApiService = retrofit.create(AuthApiService::class.java)

@Composable
fun UserScreen(
    onLoginSuccess: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    var showLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("bringsell1@ow.ly") }
    var password by remember { mutableStateOf("123456") }
    var contact by remember { mutableStateOf("defaultContact") }
    var department by remember { mutableStateOf("defaultDepartment") }
    var remark by remember { mutableStateOf("defaultRemark") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    fun saveToken(token: String) {
        val sharedPref = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        sharedPref.edit().putString("token", token).apply()
    }
    // Handle login
    fun login(onSuccess: (String) -> Unit, showError: (String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "邮箱和密码不能为空"
            showError(errorMessage!!)
            return
        }

        isLoading = true
        errorMessage = null

        CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
            try {
                val response = authApiService.login(LoginRequest(email, password))
                println("完整API响应: $response")

                withContext(Dispatchers.Main) {
                    if (response.token != null) {
                        saveToken(response.token)
                        // 显示成功消息和部分token
                        val message = "登录成功\nToken: ${response.token.take(20)}..."
                        onSuccess(message)

                        // 调试用：在Logcat打印完整token
                        println("完整Token: ${response.token}")

                        onLoginSuccess()
                    } else {
                        showError(response.message ?: "登录失败: 无效凭证")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("登录错误: ${e.localizedMessage ?: "未知错误"}")
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    // Handle registration
    fun register(
        onSuccess: () -> Unit, // 新增成功回调
        showAlert: (String) -> Unit // 新增提示回调
    ) {
        if (email.isBlank() || password.isBlank() || contact.isBlank()) {
            errorMessage = "Required fields cannot be empty"
            return
        }

        isLoading = true
        errorMessage = null

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = authApiService.register(
                    RegisterRequest(email, password, contact, department, remark,isAdmin = false)
                )
                if (response.success) {
                    // 在UI线程显示成功提示
                    withContext(Dispatchers.Main) {
                        showAlert("注册成功！")
                        email = ""
                        password = ""
                        contact = ""
                        department = ""
                        remark = ""
                        onSuccess()
                    }
                } else {
                    errorMessage = response.message
                }
            } catch (e: Exception) {
                errorMessage = "Registration failed: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    // Clear form
    fun clearForm() {
        email = ""
        password = ""
        contact = ""
        department = ""
        remark = ""
        errorMessage = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (showLogin) "Login" else "Register",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Error message
        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (showLogin) {
            LoginForm(
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                isLoading = isLoading,
                onLogin = { login(  onSuccess = { message ->
                    // 显示成功Toast
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                },
                    showError = { message ->
                        // 显示错误Toast
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                ) },
                onSwitchToRegister = {
//                    clearForm()
                    showLogin = false
                }
            )
        } else {
            RegisterForm(
                email = email,
                onEmailChange = { email = it },
                password = password,
                onPasswordChange = { password = it },
                contact = contact,
                onContactChange = { contact = it },
                department = department,
                onDepartmentChange = { department = it },
                remark = remark,
                onRemarkChange = { remark = it },
                isLoading = false,
                onRegister = { register( onSuccess = {
                    // 注册成功后的操作
                    showLogin = true // 切换回登录界面
                },
                    showAlert = { message ->
                        // 显示提示，可以使用 Toast 或 AlertDialog
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }) },
                onSwitchToLogin = {
                    clearForm()
                    showLogin = true
                }
            )
        }
    }
}

@Composable
private fun LoginForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onLogin: () -> Unit,
    onSwitchToRegister: () -> Unit
) {
    Column {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 3.dp
                )
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onSwitchToRegister,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Don't have an account? Register")
        }
    }
}

@Composable
private fun RegisterForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    contact: String,
    onContactChange: (String) -> Unit,
    department: String,
    onDepartmentChange: (String) -> Unit,
    remark: String,
    onRemarkChange: (String) -> Unit,
    isLoading: Boolean,
    onRegister: () -> Unit,
    onSwitchToLogin: () -> Unit
) {

    Column {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = contact,
            onValueChange = onContactChange,
            label = { Text("Contact") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = department,
            onValueChange = onDepartmentChange,
            label = { Text("Department") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = remark,
            onValueChange = onRemarkChange,
            label = { Text("Remark") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRegister,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading &&
                    email.isNotBlank() &&
                    password.isNotBlank() &&
                    contact.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 3.dp
                )
            } else {
                Text("Register")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onSwitchToLogin,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Already have an account? Login")
        }
    }
}