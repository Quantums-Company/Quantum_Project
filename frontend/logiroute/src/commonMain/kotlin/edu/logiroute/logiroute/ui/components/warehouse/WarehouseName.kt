package edu.logiroute.logiroute.ui.components.warehouse

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.sampledata.WarehouseSamples
import edu.logiroute.logiroute.ui.theme.TextPrimary
import org.bytebloom.domain.model.Warehouse

@Composable
fun WarehouseName(warehouse: Warehouse) {
    Text(
        text = warehouse.name,
        color = TextPrimary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview
@Composable
fun WarehouseNamePreview() {
    WarehouseName(WarehouseSamples.centralWarehouse)
}