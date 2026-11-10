package edu.logiroute.feature.warehouse.ui.component

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
import edu.logiroute.feature.`package`.ui.PackagePriorityBadge
import edu.logiroute.feature.`package`.ui.component.WarehouseCargoDestinationText
import edu.logiroute.sampledata.PackageSamples
import edu.logiroute.style.Card
import edu.logiroute.theme.InkBlack
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