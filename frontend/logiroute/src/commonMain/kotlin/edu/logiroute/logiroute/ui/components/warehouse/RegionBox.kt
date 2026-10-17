package edu.logiroute.logiroute.ui.components.warehouse

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.sampledata.WarehouseSamples
import edu.logiroute.logiroute.ui.style.BoxCard
import edu.logiroute.logiroute.ui.theme.InkBlack
import org.bytebloom.domain.model.Warehouse

@Composable
fun RegionBox(
    warehouse: Warehouse,
) {
    BoxCard {
        Text(
            text = warehouse.regionalZone,
            color = InkBlack,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
fun RegionBoxPreview() {
    RegionBox(WarehouseSamples.southWarehouse)
}