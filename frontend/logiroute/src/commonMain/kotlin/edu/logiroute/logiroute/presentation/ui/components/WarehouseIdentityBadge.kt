package edu.logiroute.logiroute.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import edu.logiroute.logiroute.presentation.ui.theme.ErrorRed
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.presentation.ui.sampledata.WarehouseSamples
import org.bytebloom.domain.model.Warehouse
import edu.logiroute.logiroute.presentation.ui.theme.CharcoalBlue
import edu.logiroute.logiroute.presentation.ui.theme.CyberSprout
import edu.logiroute.logiroute.presentation.ui.theme.InkBlack
import edu.logiroute.logiroute.presentation.ui.theme.TextPrimary
import edu.logiroute.logiroute.presentation.ui.theme.TextSecondary


@Composable
fun WarehouseIdentityBadge(
    warehouse: Warehouse,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val region = warehouse.regionalZone
    val zoneBadgeColor = when (region) {
        "NORTH",
        "EAST",
        "CENTRAL",
        "SOUTH",
        "WEST"
            -> CyberSprout

        else -> ErrorRed
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .background(CharcoalBlue)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            DisplayWarehouseName(warehouse)

            Spacer(modifier = Modifier.height(4.dp))

            DisplayWarehouseId(warehouse)
        }

        Spacer(modifier = Modifier.width(16.dp))

        DisplayRegionBox(zoneBadgeColor, warehouse)
    }
}

@Composable
private fun DisplayRegionBox(
    zoneBadgeColor: Color,
    warehouse: Warehouse
) {
    Box(
        modifier = Modifier
            .width(80.dp)
            .height(30.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(zoneBadgeColor)
            .padding(horizontal = 2.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = warehouse.regionalZone,
            color = InkBlack,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DisplayWarehouseId(warehouse: Warehouse) {
    Text(
        text = "NODE: ${warehouse.id}",
        color = TextSecondary,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun DisplayWarehouseName(warehouse: Warehouse) {
    Text(
        text = warehouse.name,
        color = TextPrimary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
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