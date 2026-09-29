package edu.logiroute.logiroute.presentation.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.domain.model.Warehouse
import edu.logiroute.logiroute.domain.model.RegionZone
import edu.logiroute.logiroute.presentation.ui.components.WarehouseIdentityBadge
import edu.logiroute.logiroute.presentation.ui.theme.JetBlack

@Composable
@Preview
fun WarehouseIdentityBadgesPreview(modifier: Modifier = Modifier) {
    val northNode = Warehouse(
        id = "WH-001",
        name = "Northern Network Node",
        regionalZone = "     noRth",
        longitude = 34.22,
        latitude = 31.45,
        cargoQueue = emptyList(),
        outgoingRoutes = emptyList(),
        stationedVehicles = emptyList()
    )

    val centralNode = Warehouse(
        id = "WH-002",
        name = "Central Cargo Terminal - Main Logistics Hub Expansion",
        regionalZone = "     Central ",
        longitude = 35.11,
        latitude = 32.04,
        cargoQueue = emptyList(),
        outgoingRoutes = emptyList(),
        stationedVehicles = emptyList()
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WarehouseIdentityBadge(
            warehouse = northNode,
            modifier = Modifier.fillMaxWidth().background(JetBlack),
            onClick = {
                println("QUACK 🦆 | Clicked on Facility: ${northNode.name} [ID: ${northNode.id}]")
            }
        )

        WarehouseIdentityBadge(
            warehouse = centralNode,
            modifier = Modifier.fillMaxWidth().background(JetBlack),
            onClick = {
                println("QUACK 🦆 | Clicked on Facility: ${centralNode.name} [ID: ${centralNode.id}]")
            }
        )
    }
}
