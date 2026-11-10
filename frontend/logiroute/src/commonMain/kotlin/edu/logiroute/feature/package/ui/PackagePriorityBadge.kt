package edu.logiroute.feature.`package`.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.feature.`package`.ui.component.PackageId
import edu.logiroute.feature.`package`.ui.component.PriorityPackage
import edu.logiroute.feature.`package`.ui.component.WeightPackage
import edu.logiroute.sampledata.PackageSamples
import edu.logiroute.theme.*
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority

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

    _root_ide_package_.edu.logiroute.style.RowCard(modifier, backgroundColor = backgroundColor) {
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