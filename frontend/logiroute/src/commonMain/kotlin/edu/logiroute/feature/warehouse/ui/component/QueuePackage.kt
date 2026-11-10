package edu.logiroute.feature.warehouse.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.sampledata.WarehouseSamples
import edu.logiroute.theme.TextPrimary
import edu.logiroute.theme.TextSecondary
import org.bytebloom.logiroute.domain.model.Warehouse

@Composable
fun QueuePackage(warehouse: Warehouse) {
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

@Preview
@Composable
fun QueuePackagePreview() {
    QueuePackage(WarehouseSamples.westWarehouse)
}