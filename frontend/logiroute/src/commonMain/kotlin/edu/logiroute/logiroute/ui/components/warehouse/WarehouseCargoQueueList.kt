package edu.logiroute.logiroute.ui.components.warehouse

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.components.`package`.PackagePriorityBadge
import edu.logiroute.logiroute.ui.sampledata.PackageSamples
import edu.logiroute.logiroute.ui.style.BoxCard
import edu.logiroute.logiroute.ui.theme.Border
import edu.logiroute.logiroute.ui.theme.CharcoalBlue
import edu.logiroute.logiroute.ui.theme.InkBlack
import edu.logiroute.logiroute.ui.theme.TextPrimary
import edu.logiroute.logiroute.ui.theme.TextSecondary
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.model.Priority
import org.jetbrains.compose.resources.stringResource
import quantum_project.frontend.logiroute.generated.resources.Res
import quantum_project.frontend.logiroute.generated.resources.cargo_header_label
import quantum_project.frontend.logiroute.generated.resources.empty_cargo_queue

@Composable
fun WarehouseCargoQueueList(cargoQueue: List<Package>, modifier: Modifier = Modifier) {
    val cargoHeaderLabel = stringResource(Res.string.cargo_header_label)
    WarehouseCargoLazyColumn(
        modifier =
            modifier.fillMaxWidth(),
        cargoQueue = cargoQueue,
        cargoHeaderLabel = cargoHeaderLabel
    )
}


@Preview(name = "Warehouse Cargo Queue - Populated State", widthDp = 400, heightDp = 600)
@Composable
fun WarehouseCargoQueueListPreview() {
    val sampleCargoQueue = listOf(
        PackageSamples.urgentCargo,
        PackageSamples.standardCargo,
        PackageSamples.lowCargo,
        PackageSamples.urgentCargo.copy(id = "PKG-002", weight = 8.0)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InkBlack)
            .padding(16.dp)
    ) {
        WarehouseCargoQueueList(
            cargoQueue = sampleCargoQueue,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(name = "Warehouse Cargo Queue - Empty State", widthDp = 400, heightDp = 300)
@Composable
fun WarehouseCargoQueueEmptyPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InkBlack)
            .padding(16.dp)
    ) {
        WarehouseCargoQueueList(
            cargoQueue = emptyList(),
            modifier = Modifier.fillMaxSize()
        )
    }
}