package edu.logiroute.feature.warehouse.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.sampledata.WarehouseSamples
import edu.logiroute.style.RowCard
import org.bytebloom.logiroute.domain.model.Warehouse

@Composable
fun WarehouseIdentityBadge(
    warehouse: Warehouse,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    RowCard(modifier, onClick = onClick) {
        WarehouseHeaderDetails(warehouse)
        Spacer(modifier = Modifier.width(16.dp))

        RegionBox(warehouse)
    }
}

@Composable
@Preview
fun WarehouseIdentityBadgePreview() {
    Column (
        modifier = Modifier.fillMaxSize()
            .padding( 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ){
        WarehouseIdentityBadge(warehouse = WarehouseSamples.centralWarehouse)
        WarehouseIdentityBadge(warehouse = WarehouseSamples.northWarehouse)
        WarehouseIdentityBadge(warehouse = WarehouseSamples.eastWarehouse)
        WarehouseIdentityBadge(warehouse = WarehouseSamples.westWarehouse)
    }
}