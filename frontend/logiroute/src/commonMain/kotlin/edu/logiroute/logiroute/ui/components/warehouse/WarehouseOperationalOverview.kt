package edu.logiroute.logiroute.ui.components.warehouse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.sampledata.PackageSamples
import edu.logiroute.logiroute.ui.sampledata.WarehouseSamples
import edu.logiroute.logiroute.ui.theme.*
import org.bytebloom.logiroute.domain.model.Vehicle
import org.bytebloom.logiroute.domain.model.Warehouse

private const val WIDE_SCREEN_BREAKPOINT_DP = 840

@Composable
fun WarehouseOperationalOverview(warehouse: Warehouse, modifier: Modifier = Modifier) {
    val totalCargoWeight = warehouse.cargoQueue.sumOf { it.weight }
    val totalFleetCapacity = warehouse.stationedVehicles.sumOf { it.maxCapacityKg }
    val isOverloaded = totalCargoWeight > totalFleetCapacity
    val overloadDifference = totalCargoWeight - totalFleetCapacity
    Column(
        modifier =
            modifier.fillMaxSize().background(InkBlack).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WarehouseSummaryCard(warehouse = warehouse)
        isOverloaded.takeIf { it }?.let {
            OverloadWarningBanner(
                excessWeight = overloadDifference
            )
        }
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()
            .weight(1f)) {
            when (
                val isWideScreen = maxWidth >= WIDE_SCREEN_BREAKPOINT_DP.dp
            ) {
                isWideScreen -> WarehouseWideScreenContent(warehouse = warehouse, modifier = Modifier.fillMaxSize())
                else -> WarehouseMobileContent(warehouse = warehouse, modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Preview(name = "Warehouse Overview - Mobile", widthDp = 400, heightDp = 800)
@Preview(name = "Warehouse Overview - Wide Screen", widthDp = 900, heightDp = 700)
@Composable
fun WarehouseOperationalOverviewPreview() {
    WarehouseOperationalOverview(
        warehouse = WarehouseSamples.centralWarehouse
    )
}

