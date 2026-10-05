package edu.logiroute.logiroute.ui.components.vehicle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.sampledata.VehicleSamples
import edu.logiroute.logiroute.ui.style.Card
import org.bytebloom.domain.model.Vehicle

@Composable
fun VehicleDetailCard(
    vehicle: Vehicle,
    modifier: Modifier = Modifier,
    currentLoadKg: Double = 0.0,
) {
    val maxCapacity = vehicle.maxCapacityKg

    Card(modifier = modifier) {
        VehicleHeader(
            vehicleId = vehicle.id,
            warehouseName = vehicle.currentWarehouse.name
        )

        VehicleMetrics(
            costPerKm = vehicle.costPerKm,
            maxCapacityKg = maxCapacity
        )

        VehicleCapacityIndicator(
            currentLoadKg = currentLoadKg,
            maxCapacityKg = maxCapacity
        )
    }
}

@Preview
@Composable
fun VehicleDetailCardPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        VehicleDetailCard(
            vehicle = VehicleSamples.centralVehicle,
            currentLoadKg = 2500.0
        )
    }
}