package com.example.infoday

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import com.example.infoday.ui.theme.InfoDayTheme
import kotlinx.coroutines.launch

@Composable
fun ItineraryScreen(
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val eventDao = EventDatabase.getInstance(context).eventDao()
    val savedEvents by eventDao.getSavedEvents().collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    if (savedEvents.isEmpty()) {
        Text(
            text = "Your itinerary is empty",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    } else {
        LazyColumn {
            items(savedEvents, key = { it.id }) { event ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    trailingContent = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    eventDao.update(event.copy(saved = false))
                                    snackbarHostState.showSnackbar(
                                        "Removed '${event.title}' from itinerary"
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove from itinerary"
                            )
                        }
                    }
                )
                Divider()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItineraryPreview() {
    InfoDayTheme {
        ItineraryScreen(
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}