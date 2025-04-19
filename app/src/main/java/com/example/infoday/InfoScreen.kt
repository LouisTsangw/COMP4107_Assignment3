package com.example.infoday

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.infoday.DataStoreInstance.DARK_MODE
import com.example.infoday.ui.theme.InfoDayTheme
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun InfoPreview() {
    InfoDayTheme {
        InfoScreen()
    }
}

@Composable
fun InfoGreeting() {
    val padding = 16.dp
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.size(padding))
        Image(
            painter = painterResource(id = R.drawable.hkbu_logo),
            contentDescription = stringResource(id = R.string.hkbu_logo),
            modifier = Modifier.size(180.dp)
        )
        Spacer(Modifier.size(padding))
        Text(
            text = "HKBU InfoDay App",
            fontSize = 30.sp,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun InfoScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InfoGreeting()
        PhoneList()
        SettingList()
        FeedBack()
    }
}

data class Contact(val office: String, val tel: String) {
    companion object {
        val data = listOf(
            Contact(office = "Admission Office", tel = "3411-2200"),
            Contact(office = "Emergencies", tel = "3411-7777"),
            Contact(office = "Health Services Center", tel = "3411-7447")
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneList() {
    val context = LocalContext.current
    Column {
        Contact.data.forEach { contact ->
            ListItem(
                headlineContent = { Text(contact.office) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Phone icon"
                    )
                },
                trailingContent = { Text(contact.tel) },
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = "tel:${contact.tel}".toUri()
                    context.startActivity(intent)
                }
            )
        }
    }
}


@Composable
fun FeedBack() {
    val padding = 16.dp
    var message by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Your feedback") },
            singleLine = true,
            modifier = Modifier.semantics {
                contentDescription = "Feedback input field"
            }
        )
        Spacer(Modifier.size(padding))

        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        val response = KtorClient.postFeedback(message)
                        Log.i("FeedbackResponse", response)
                        message = ""
                    } catch (e: Exception) {
                        Log.e("FeedbackError", "Failed to submit feedback", e)
                    }
                }
            },
            modifier = Modifier.semantics {
                contentDescription = "Submit feedback button"
            }
        ) {
            Text(text = "Submit feedback")
        }
    }
}

@Composable
fun SettingList() {
    val context = LocalContext.current
    val checked by DataStoreInstance.getBooleanPreferences(context, DARK_MODE)
        .collectAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()

    ListItem(
        headlineContent = { Text("Dark Mode") },
        leadingContent = {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "Settings icon"
            )
        },
        trailingContent = {
            Switch(
                modifier = Modifier.semantics {
                    contentDescription = "Dark mode toggle switch"
                },
                checked = checked == true,
                onCheckedChange = {
                    coroutineScope.launch {
                        DataStoreInstance.saveBooleanPreferences(context, DARK_MODE, it)
                    }
                }
            )
        }
    )
}