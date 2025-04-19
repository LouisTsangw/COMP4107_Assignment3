package com.example.infoday

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.infoday.ui.theme.InfoDayTheme
import kotlinx.coroutines.launch

@Entity(tableName = "event")
data class Event(
    @PrimaryKey val id: Int,
    val title: String,
    val deptId: String,
    var saved: Boolean
) {
    companion object {
        val data = listOf(
            Event(id = 1, title = "Career Talks", deptId = "COMS", saved = false),
            Event(id = 2, title = "Guided Tour", deptId = "COMS", saved = true),
            Event(id = 3, title = "MindDrive Demo", deptId = "COMP", saved = false),
            Event(id = 4, title = "Project Demo", deptId = "COMP", saved = false)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventScreen(
    snackbarHostState: SnackbarHostState,
    deptId: String?
) {
    val context = LocalContext.current
    val eventDao = remember { EventDatabase.getInstance(context).eventDao() }
    val events by eventDao.getByDeptId(deptId ?: "").collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    LazyColumn {
        items(events) { event ->
            ListItem(
                headlineContent = {
                    Text(
                        text = event.title,
                        style = if (event.saved) {
                            MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            MaterialTheme.typography.titleMedium
                        }
                    )
                },
                modifier = Modifier.combinedClickable(
                    onClick = { /* Handle click */ },
                    onLongClick = {
                        coroutineScope.launch {
                            event.saved = true
                            eventDao.update(event)
                            snackbarHostState.showSnackbar(
                                "Event '${event.title}' has been added to itinerary."
                            )
                        }
                    }
                )
            )
            HorizontalDivider()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventPreview() {
    val snackbarHostState = remember { SnackbarHostState() }

    InfoDayTheme {
        EventScreen(
            snackbarHostState = snackbarHostState,
            deptId = "COMP"
        )
    }
}