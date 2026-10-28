package edu.logiroute.logiroute.ui.components.warehouse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.components.vehicle.ActiveFleetGrid
import edu.logiroute.logiroute.ui.sampledata.WarehouseSamples
import org.bytebloom.logiroute.domain.model.Warehouse

@Composable
fun WarehouseMobileContent(
    warehouse: Warehouse,
    modifier: Modifier = Modifier,
    vehicleLoads: Map<String, Double> = emptyMap()
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WarehouseCargoQueueList(
            cargoQueue = warehouse.cargoQueue,
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        ActiveFleetGrid(
            vehicles = warehouse.stationedVehicles,
            vehicleLoads = vehicleLoads,
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
    }
}

@Preview(name = "Warehouse Overview - Mobile", widthDp = 400, heightDp = 800)
@Composable
fun WarehouseMobileOverviewPreview() {
    WarehouseMobileContent(
        warehouse = WarehouseSamples.overloadedWarehouse,
        modifier = Modifier.fillMaxSize()
    )
}