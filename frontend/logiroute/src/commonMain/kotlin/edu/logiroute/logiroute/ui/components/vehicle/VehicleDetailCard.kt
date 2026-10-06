package edu.logiroute.logiroute.ui.components.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    onClick: (Vehicle) -> Unit = {},
    currentLoadKg: Double = 0.0,
) {
    val maxCapacity = vehicle.maxCapacityKg

    Card(modifier = modifier, onClick = { onClick.invoke(vehicle) },) {
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

@Preview(name = "Fleet Capacity Monitoring")
@Composable
fun VehicleDetailCardPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        VehicleDetailCard(
            vehicle = VehicleSamples.safeLoadVehicle,
            currentLoadKg = 2500.0
        )

        VehicleDetailCard(
            vehicle = VehicleSamples.heavyLoadVehicle,
            currentLoadKg = 8200.0
        )

        VehicleDetailCard(
            vehicle = VehicleSamples.overloadedVehicle,
            currentLoadKg = 10500.0
        )
    }
}