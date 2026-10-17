package edu.logiroute.logiroute.ui.components.warehouse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import edu.logiroute.logiroute.ui.sampledata.WarehouseSamples
import org.bytebloom.domain.model.Warehouse

@Composable
fun WarehouseMetrics(warehouse: Warehouse) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        QueuePackage(warehouse)
        StationedFleet(warehouse)
    }
}

@Preview
@Composable
fun WarehouseMetricsPreview() {
    WarehouseMetrics(WarehouseSamples.northWarehouse)
}