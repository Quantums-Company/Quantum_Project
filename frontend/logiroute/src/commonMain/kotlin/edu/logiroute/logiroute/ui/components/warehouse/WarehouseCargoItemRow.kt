package edu.logiroute.logiroute.ui.components.warehouse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.components.`package`.PackagePriorityBadge
import edu.logiroute.logiroute.ui.components.`package`.WarehouseCargoDestinationText
import edu.logiroute.logiroute.ui.sampledata.PackageSamples
import edu.logiroute.logiroute.ui.style.Card
import edu.logiroute.logiroute.ui.theme.InkBlack
import org.bytebloom.logiroute.domain.model.Package

@Composable
fun WarehouseCargoItemRow(
    packageItem: Package,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = InkBlack,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().background(color = InkBlack),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WarehouseCargoDestinationText(packageItem)

            PackagePriorityBadge(
                cargo = packageItem,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(name = "Warehouse Cargo Item Row - Urgent", widthDp = 400)
@Composable
fun WarehouseCargoItemRowUrgentPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(InkBlack)
            .padding(16.dp)
    ) {
        WarehouseCargoItemRow(
            packageItem = PackageSamples.urgentCargo
        )
    }
}

@Preview(name = "Warehouse Cargo Item Row - Standard", widthDp = 400)
@Composable
fun WarehouseCargoItemRowStandardPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(InkBlack)
            .padding(16.dp)
    ) {
        WarehouseCargoItemRow(
            packageItem = PackageSamples.standardCargo
        )
    }
}