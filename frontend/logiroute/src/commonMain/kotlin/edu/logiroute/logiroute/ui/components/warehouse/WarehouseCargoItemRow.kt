package edu.logiroute.logiroute.ui.components.warehouse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.components.`package`.PackagePriorityBadge
import edu.logiroute.logiroute.ui.sampledata.PackageSamples
import edu.logiroute.logiroute.ui.style.BoxCard
import edu.logiroute.logiroute.ui.style.Card
import edu.logiroute.logiroute.ui.style.RowCard
import edu.logiroute.logiroute.ui.theme.CharcoalBlue
import edu.logiroute.logiroute.ui.theme.InkBlack
import edu.logiroute.logiroute.ui.theme.TextPrimary
import edu.logiroute.logiroute.ui.theme.TextSecondary
import org.bytebloom.logiroute.domain.model.Package
import org.jetbrains.compose.resources.stringResource
import quantum_project.frontend.logiroute.generated.resources.Res
import quantum_project.frontend.logiroute.generated.resources.cargo_weight_format

@Composable
fun WarehouseCargoItemRow(
    packageItem: Package,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = CharcoalBlue,
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
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

@Composable
private fun WarehouseCargoDestinationText(packageItem: Package) {
    Text(
        text = "Destination: ${packageItem.destinationWarehouse.name}",
        color = TextSecondary,
        fontSize = 12.sp
    )
}