package edu.logiroute.logiroute.ui.components.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.sampledata.VehicleSamples.sampleLoads
import edu.logiroute.logiroute.ui.sampledata.VehicleSamples.sampleVehicles
import edu.logiroute.logiroute.ui.theme.InkBlack
import org.bytebloom.logiroute.domain.model.Vehicle

@Composable
fun ActiveFleetGrid(
    vehicles: List<Vehicle>,
    modifier: Modifier = Modifier,
    vehicleLoads: Map<String, Double> = emptyMap()
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 280.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = vehicles,
            key = { vehicle -> vehicle.id },
            contentType = { "vehicle_item" }) { vehicle ->

            val currentLoad = vehicleLoads[vehicle.id] ?: 0.0

            VehicleFleetItem(vehicle = vehicle, currentLoad = currentLoad)
        }
    }
}

@Preview(name = "Active Fleet Grid - Mobile (360dp)", widthDp = 360, heightDp = 640)
@Preview(name = "Active Fleet Grid - Desktop / Wide (1200dp)", widthDp = 1200, heightDp = 800)
@Composable
fun ActiveFleetGridPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InkBlack)
            .padding(16.dp)
    ) {
        ActiveFleetGrid(
            vehicles = sampleVehicles,
            vehicleLoads = sampleLoads,
            modifier = Modifier.fillMaxSize()
        )
    }
}