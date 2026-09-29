package edu.logiroute.logiroute.presentation.ui.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.domain.model.Package
import edu.logiroute.logiroute.domain.model.Priority
import edu.logiroute.logiroute.presentation.ui.components.PackagePriorityBadge

@Composable
@Preview
fun PackagePriorityBadgesAllStatesPreview(modifier: Modifier = Modifier) {
    val urgentCargo = Package("PKG-URG-001", 12.5, "urgent", "WH-1", "WH-2")
    val standardCargo = Package("PKG-STD-092", 5.2, "standard     ", "WH-1", "WH-2")
    val lowCargo = Package("PKG-LOW-741", 150.0, "lOw", "WH-1", "WH-2")
    val longIdCargo = Package("PKG-CRITICAL-LONG-ID-GENERATED-BY-SYSTEM-AX992", 88.0, "URGENT", "WH-1", "WH-2")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        PackagePriorityBadge(cargo = urgentCargo)
        PackagePriorityBadge(cargo = standardCargo)
        PackagePriorityBadge(cargo = lowCargo)
        PackagePriorityBadge(cargo = longIdCargo)
    }
}
