package edu.logiroute.logiroute.ui.components.warehouse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.components.vehicle.ActiveFleetGrid
import org.bytebloom.logiroute.domain.model.Warehouse

@Composable
fun WarehouseMobileContent(warehouse: Warehouse, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WarehouseCargoQueueList(
            cargoQueue = warehouse.cargoQueue,
            modifier = Modifier.fillMaxWidth()
        )

        ActiveFleetGrid(
            vehicles = warehouse.stationedVehicles,
            modifier = Modifier.fillMaxWidth()
        )
    }
}