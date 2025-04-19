package com.example.Assignment3

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

/**
 * Screen displaying the campus map with markers.
 */
@Composable
fun MapScreen() {
    val buildings = Building.data
    val hkbu = LatLng(22.33787, 114.18131) // Hong Kong Baptist University coordinates
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(hkbu, 17f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        buildings.forEach { building ->
            Marker(
                state = rememberMarkerState(
                    position = LatLng(
                        building.latitude,
                        building.longitude
                    )
                ),
                title = building.title,
                snippet = "Marker in HKBU"
            )
        }
    }
}
/**
 * Data class representing a building on campus.
 */
data class Building(
    val title: String,
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        /**
         * List of buildings with their coordinates.
         */
        val data = listOf(
            Building("AC Hall", 22.341280, 114.179768),
            Building("Lam Woo International Conference Center", 22.337716, 114.182013),
            Building("Communication and Visual Arts Building", 22.334382, 114.182528)
        )
    }
}