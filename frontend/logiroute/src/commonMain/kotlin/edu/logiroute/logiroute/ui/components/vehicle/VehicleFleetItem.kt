package edu.logiroute.logiroute.ui.components.vehicle

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.sampledata.VehicleSamples
import edu.logiroute.logiroute.ui.theme.ErrorRed
import edu.logiroute.logiroute.ui.theme.LightGreen
import edu.logiroute.logiroute.ui.theme.WarningAmber
import org.bytebloom.logiroute.domain.model.Vehicle

@Composable
fun VehicleFleetItem(
    vehicle: Vehicle,
    currentLoad: Double
) {
    val loadRatio = vehicle.maxCapacityKg.takeIf { it > 0.0 }?.let { currentLoad / it } ?: 0.0
    val borderModifier = when {
        loadRatio > 0.90 ->
            Modifier.border(2.dp, ErrorRed, FleetCardShape)

        loadRatio >= 0.70 ->
            Modifier.border(2.dp, WarningAmber, FleetCardShape)

        else ->
            Modifier.border(2.dp, LightGreen, FleetCardShape)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(borderModifier)
            .clip(FleetCardShape)
    ) {
        VehicleDetailCard(
            vehicle = vehicle,
            currentLoadKg = currentLoad
        )
    }
}

private val FleetCardShape = RoundedCornerShape(12.dp)

@Preview
@Composable
fun VehicleFleetItemPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        VehicleFleetItem(
            vehicle = VehicleSamples.safeLoadVehicle,
            currentLoad = 2500.0
        )

        VehicleFleetItem(
            vehicle = VehicleSamples.heavyLoadVehicle,
            currentLoad = 8900.0
        )

        VehicleFleetItem(
            vehicle = VehicleSamples.overloadedVehicle,
            currentLoad = 10500.0
        )
    }
}