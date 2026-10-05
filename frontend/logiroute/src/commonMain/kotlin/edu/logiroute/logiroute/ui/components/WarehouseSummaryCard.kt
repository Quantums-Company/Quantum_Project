package edu.logiroute.logiroute.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.components.DisplayPriorityPackage
import edu.logiroute.logiroute.ui.sampledata.WarehouseSamples
import org.bytebloom.domain.model.Warehouse
import edu.logiroute.logiroute.ui.theme.Border
import edu.logiroute.logiroute.ui.theme.CharcoalBlue
import edu.logiroute.logiroute.ui.theme.JetBlack
import edu.logiroute.logiroute.ui.theme.TextDisabled
import edu.logiroute.logiroute.ui.theme.TextPrimary
import edu.logiroute.logiroute.ui.theme.TextSecondary
import org.bytebloom.domain.model.Package

@Composable
fun WarehouseSummaryCard(
    warehouse: Warehouse,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(JetBlack)
            .border(1.dp, Border, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        WarehouseIdentityBadge(
            warehouse = warehouse,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DisplayQueuePackage(warehouse)
            DisplayStationedFleet(warehouse)
        }

        val cargo = warehouse.cargoQueue.firstOrNull()

        DisplayPriorityPackage(cargo)
    }
}

@Composable
private fun ColumnScope.DisplayPriorityPackage(cargo: Package?) {

    cargo?.let { cargo ->
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = "cargo", color = TextSecondary, fontSize = 11.sp)
            PackagePriorityBadge(cargo = cargo, modifier = Modifier.fillMaxWidth())
        }
    } ?: run {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(CharcoalBlue)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Pending Cargo",
                color = TextDisabled,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DisplayStationedFleet(warehouse: Warehouse) {
    Column(horizontalAlignment = Alignment.End) {
        Text(text = "Stationed Fleet", color = TextSecondary, fontSize = 11.sp)
        Text(
            text = "${warehouse.stationedVehicles.size}",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DisplayQueuePackage(warehouse: Warehouse) {
    Column {
        Text(text = "Queued Cargo", color = TextSecondary, fontSize = 11.sp)
        Text(
            text = "${warehouse.cargoQueue.size}",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
@Preview
private fun WarehouseSummaryCardPreview() {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WarehouseSummaryCard(WarehouseSamples.northWarehouse)
        WarehouseSummaryCard(WarehouseSamples.southWarehouse)
        WarehouseSummaryCard(WarehouseSamples.centralWarehouse)
    }
}