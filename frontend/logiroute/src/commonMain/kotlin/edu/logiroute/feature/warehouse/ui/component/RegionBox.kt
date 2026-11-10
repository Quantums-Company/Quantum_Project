package edu.logiroute.feature.warehouse.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.sampledata.WarehouseSamples
import edu.logiroute.style.BoxCard
import edu.logiroute.theme.InkBlack
import org.bytebloom.logiroute.domain.model.Warehouse

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