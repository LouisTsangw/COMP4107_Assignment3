package com.example.Assignment3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HighlightedEquipmentScreen() {
    // Sample data - replace with your actual data source
    val equipmentList = listOf(
        Equipment(
            name = "Equipment 1",
            contactPerson = "John Doe",
            description = "Nulla justo. Aliquam quis turpis eget elit sodales scelerisque.",
            location = "Room 101",
            color = "Red",
            createdAt = "2023-05-15",
            modifiedAt = "2023-06-20"
        ),
        Equipment(
            name = "Equipment 2",
            contactPerson = "Jane Smith",
            description = "Mauris sit amet eros. Suspendisse accumsan tortor quis turpis.",
            location = "Room 205",
            color = "Blue",
            createdAt = "2023-04-10",
            modifiedAt = "2023-05-25"
        ),
        Equipment(
            name = "Equipment 3",
            contactPerson = "Mike Johnson",
            description = "Vestibulum quam sapien, varius ut, blandit non, interdum in, ante.",
            location = "Room 312",
            color = "Green",
            createdAt = "2023-03-05",
            modifiedAt = "2023-04-15"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn {
            items(equipmentList) { equipment ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = equipment.name,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = equipment.contactPerson,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = equipment.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Location",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = equipment.location,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Column {
                                Text(
                                    text = "Color",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = equipment.color,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Created at",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = equipment.createdAt,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Column {
                                Text(
                                    text = "Modified at",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = equipment.modifiedAt,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Data class for equipment
data class Equipment(
    val name: String,
    val contactPerson: String,
    val description: String,
    val location: String,
    val color: String,
    val createdAt: String,
    val modifiedAt: String
)