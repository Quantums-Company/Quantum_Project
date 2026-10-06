package edu.logiroute.logiroute.ui.components.Warehouse

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.sampledata.WarehouseSamples
import org.bytebloom.domain.model.Warehouse

@Composable
fun RowScope.WarehouseHeaderDetails(warehouse: Warehouse) {
    Column(modifier = Modifier.weight(1f)) {
        WarehouseName(warehouse)

        Spacer(modifier = Modifier.height(4.dp))

        WarehouseId(warehouse)
    }
}

@Preview
@Composable
fun PreviewWarehouseHeaderDetails() {
    Row {
        WarehouseHeaderDetails(WarehouseSamples.westWarehouse)
    }
}