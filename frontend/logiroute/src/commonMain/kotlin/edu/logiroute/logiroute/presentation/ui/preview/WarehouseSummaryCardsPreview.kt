package edu.logiroute.logiroute.presentation.ui.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.domain.model.Package
import edu.logiroute.logiroute.domain.model.Priority
import edu.logiroute.logiroute.domain.model.RegionZone
import edu.logiroute.logiroute.domain.model.Vehicle
import edu.logiroute.logiroute.domain.model.Warehouse
import edu.logiroute.logiroute.presentation.ui.components.WarehouseSummaryCard

@Composable
@Preview
fun WarehouseSummaryCardsPreview(modifier: Modifier = Modifier) {
    val standardCargo = Package("PKG-STD-101", 15.0, "standard", "WH-001", "WH-002")
    val urgentCargo = Package("PKG-URG-202", 45.5, "URGENT       ", "WH-001", "WH-002")
    val lowCargo = Package("PKG-LOW-303", 120.0, "low", "WH-001", "WH-002")

    val activeWarehouse = Warehouse(
        id = "WH-001",
        name = "Central Cargo Terminal - Main Logistics Hub Expansion",
        regionalZone = "central",
        longitude = 35.11,
        latitude = 32.04,
        cargoQueue = listOf(standardCargo, urgentCargo, lowCargo),
        outgoingRoutes = emptyList(),
        stationedVehicles = listOf(mockVehicle(), mockVehicle())
    )

    val emptyWarehouse = Warehouse(
        id = "WH-002",
        name = "Northern Network Node",
        regionalZone = "north",
        longitude = 34.22,
        latitude = 31.45,
        cargoQueue = emptyList(),
        outgoingRoutes = emptyList(),
        stationedVehicles = emptyList()
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WarehouseSummaryCard(
            warehouse = activeWarehouse,
            modifier = Modifier.fillMaxWidth()
        )

        WarehouseSummaryCard(
            warehouse = emptyWarehouse,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun mockVehicle(): Vehicle {
    return Vehicle(
        id = "V-01",
        maxCapacityKg = 1000.0,
        costPerKm = 2.5,
        currentWarehouseId = "WH-001"
    )
}