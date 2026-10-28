package edu.logiroute.logiroute.ui.components.warehouse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.components.vehicle.ActiveFleetGrid
import edu.logiroute.logiroute.ui.sampledata.WarehouseSamples
import org.bytebloom.logiroute.domain.model.Warehouse

@Composable
fun WarehouseWideScreenContent(
    warehouse: Warehouse,
    modifier: Modifier = Modifier,
    vehicleLoads: Map<String, Double> = emptyMap()
) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WarehouseCargoQueueList(
            cargoQueue = warehouse.cargoQueue,
            modifier = Modifier.weight(0.4f).fillMaxHeight()
        )
        ActiveFleetGrid(
            vehicles = warehouse.stationedVehicles,
            vehicleLoads = vehicleLoads,
            modifier = Modifier.weight(0.6f).fillMaxHeight())
    }
}

@Preview
@Composable
fun WarehouseWideScreenContentPreview() {
    WarehouseWideScreenContent(WarehouseSamples.centralWarehouse)
}