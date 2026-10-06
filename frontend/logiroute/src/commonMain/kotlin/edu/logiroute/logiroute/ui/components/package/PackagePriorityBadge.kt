package edu.logiroute.logiroute.ui.components.`package`

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.sampledata.PackageSamples
import edu.logiroute.logiroute.ui.style.RowCard
import org.bytebloom.domain.model.Package
import edu.logiroute.logiroute.ui.theme.TextSecondary
import edu.logiroute.logiroute.ui.theme.CharcoalBlue
import edu.logiroute.logiroute.ui.theme.ErrorRed
import edu.logiroute.logiroute.ui.theme.InkBlack
import edu.logiroute.logiroute.ui.theme.LightGreen
import org.bytebloom.domain.model.Priority

@Composable
fun PackagePriorityBadge(
    cargo: Package,
    modifier: Modifier = Modifier
) {
    val priority = Priority.from(cargo.priority.name).name

    val backgroundColor = when (priority) {
        "URGENT" -> ErrorRed
        "STANDARD" -> LightGreen
        "LOW" -> CharcoalBlue
        else -> ErrorRed
    }

    val textColor = when (priority) {
        "URGENT" -> InkBlack
        "STANDARD" -> InkBlack
        "LOW" -> TextSecondary
        else -> ErrorRed
    }

    RowCard(modifier, backgroundColor = backgroundColor) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            PackageId(cargo.id, textColor)
            PriorityPackage(priority, textColor)
        }

        WeightPackage(cargo)
    }
}

@Preview
@Composable
fun DisplayPriorityPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PackagePriorityBadge(cargo = PackageSamples.standardCargo)
        PackagePriorityBadge(cargo = PackageSamples.urgentCargo)
        PackagePriorityBadge(cargo = PackageSamples.lowCargo)
    }
}