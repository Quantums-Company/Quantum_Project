package edu.logiroute.logiroute.ui.components.Warehouse

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.sampledata.WarehouseSamples
import org.bytebloom.domain.model.Warehouse
import edu.logiroute.logiroute.ui.theme.Border
import edu.logiroute.logiroute.ui.theme.JetBlack

@Composable
fun WarehouseSummaryCard(
    warehouse: Warehouse,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick?.invoke() }
            .background(JetBlack)
            .border(1.dp, Border, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        WarehouseIdentityBadge(
            warehouse = warehouse,
            modifier = Modifier.fillMaxWidth()
        )

        WarehouseMetrics(warehouse)

        val cargo = warehouse.cargoQueue.firstOrNull()

        PriorityPackage(cargo)
    }
}


@Composable
@Preview
fun WarehouseSummaryCardPreview() {
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