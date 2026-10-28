package edu.logiroute.logiroute.ui.components.`package`

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.theme.TextSecondary
import org.bytebloom.logiroute.domain.model.Package

@Composable
fun WarehouseCargoDestinationText(packageItem: Package) {
    Text(
        text = "Destination: ${packageItem.destinationWarehouse.name}",
        color = TextSecondary,
        fontSize = 12.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}