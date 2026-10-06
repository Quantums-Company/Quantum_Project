package edu.logiroute.logiroute.ui.components.Warehouse

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.sampledata.WarehouseSamples
import edu.logiroute.logiroute.ui.theme.TextSecondary
import org.bytebloom.domain.model.Warehouse

@Composable
fun WarehouseId(warehouse: Warehouse) {
    Text(
        text = "NODE: ${warehouse.id}",
        color = TextSecondary,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview
@Composable
fun WarehouseIdPreview() {
    WarehouseId(WarehouseSamples.centralWarehouse)
}