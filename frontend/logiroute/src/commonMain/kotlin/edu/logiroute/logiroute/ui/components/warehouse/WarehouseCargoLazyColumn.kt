package edu.logiroute.logiroute.ui.components.warehouse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.sampledata.PackageSamples
import edu.logiroute.logiroute.ui.theme.InkBlack
import org.bytebloom.logiroute.domain.model.Package
import org.jetbrains.compose.resources.stringResource
import quantum_project.frontend.logiroute.generated.resources.Res
import quantum_project.frontend.logiroute.generated.resources.empty_cargo_queue

@Composable
fun WarehouseCargoLazyColumn(
    cargoQueue: List<Package>,
    cargoHeaderLabel: String,
    modifier: Modifier = Modifier
) {
    val emptyQueueMessage = stringResource(Res.string.empty_cargo_queue)
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        emptyCargoQueueItem(cargoQueue, emptyQueueMessage)
        warehouseCargoQueueItems(cargoQueue, cargoHeaderLabel)
    }
}

private fun LazyListScope.warehouseCargoQueueItems(
    cargoQueue: List<Package>,
    cargoHeaderLabel: String
) {
    cargoQueue.takeUnless { it.isEmpty() }?.groupBy { it.priority }?.forEach { (priority, packages) ->
        stickyHeader(
            key = "header_$priority",
            contentType = "sticky_header"
        ) {
            WarehouseCargoHeader(
                title = "$cargoHeaderLabel - $priority",
                count = packages.size,
                modifier = Modifier.fillMaxWidth()
            )
        }
        items(
            items = packages,
            key = { pkg -> pkg.id },
            contentType = { "cargo_item" })
        { pkg ->
            WarehouseCargoItemRow(
                packageItem = pkg
            )
        }
    }
}

private fun LazyListScope.emptyCargoQueueItem(
    cargoQueue: List<Package>,
    emptyQueueMessage: String
) {
    cargoQueue.takeIf { it.isEmpty() }?.let {
        item(
            key = "empty_state",
            contentType = "empty"
        ) {
            WarehouseCargoEmptyState(message = emptyQueueMessage)
        }
    }
}

@Preview(name = "Warehouse Cargo LazyColumn - Populated State", widthDp = 400, heightDp = 600)
@Composable
fun WarehouseCargoLazyColumnPreview() {
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
        WarehouseCargoLazyColumn(
            cargoQueue = sampleCargoQueue,
            cargoHeaderLabel = "Cargo",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(name = "Warehouse Cargo LazyColumn - Empty State", widthDp = 400, heightDp = 300)
@Composable
fun WarehouseCargoLazyColumnEmptyPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(InkBlack)
            .padding(16.dp)
    ) {
        WarehouseCargoLazyColumn(
            cargoQueue = emptyList(),
            cargoHeaderLabel = "Cargo",
            modifier = Modifier.fillMaxSize()
        )
    }
}