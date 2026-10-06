package edu.logiroute.logiroute.ui.components.Warehouse

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.sampledata.WarehouseSamples
import edu.logiroute.logiroute.ui.theme.TextPrimary
import edu.logiroute.logiroute.ui.theme.TextSecondary
import org.bytebloom.domain.model.Warehouse

@Composable
fun StationedFleet(warehouse: Warehouse) {
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

@Preview
@Composable
fun StationedFleetPreview() {
    StationedFleet(WarehouseSamples.southWarehouse)
}