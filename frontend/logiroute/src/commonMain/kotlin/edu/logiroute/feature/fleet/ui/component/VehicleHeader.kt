package edu.logiroute.feature.fleet.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.sampledata.VehicleSamples
import edu.logiroute.sampledata.WarehouseSamples
import edu.logiroute.style.RowCard

@Composable
fun VehicleHeader(
    vehicleId: String,
    warehouseName: String,
    modifier: Modifier = Modifier
) {
    RowCard {
        VehicleId(vehicleId)
        Spacer(modifier = modifier.width(16.dp))
        WarehouseName(warehouseName)
    }
}

@Preview
@Composable
fun VehicleHeaderPreview() {
    VehicleHeader(VehicleSamples.westVehicle.id, WarehouseSamples.westWarehouse.name)
}