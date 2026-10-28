package edu.logiroute.logiroute.ui.components.warehouse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.sampledata.VehicleSamples
import edu.logiroute.logiroute.ui.sampledata.WarehouseSamples
import edu.logiroute.logiroute.ui.theme.*
import org.bytebloom.logiroute.domain.model.Warehouse

private const val WIDE_SCREEN_BREAKPOINT_DP = 840

@Composable
fun WarehouseOperationalOverview(
    warehouse: Warehouse,
    modifier: Modifier = Modifier,
    vehicleLoads: Map<String, Double> = emptyMap(),
) {
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
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            if (maxWidth >= WIDE_SCREEN_BREAKPOINT_DP.dp) {
                WarehouseWideScreenContent(
                    warehouse = warehouse,
                    vehicleLoads = vehicleLoads,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                WarehouseMobileContent(
                    warehouse = warehouse,
                    vehicleLoads = vehicleLoads,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Preview(name = "Warehouse Overview - Mobile - Standard", widthDp = 400, heightDp = 800)
@Composable
fun WarehouseMobileStandardPreview() {
    WarehouseOperationalOverview(
        warehouse = WarehouseSamples.overloadedWarehouse,
        vehicleLoads = mapOf("TRK-999" to 450.0)
    )
}

@Preview(name = "Warehouse Overview - Wide Screen - Standard", widthDp = 1000, heightDp = 700)
@Composable
fun WarehouseWideScreenStandardPreview() {
    WarehouseOperationalOverview(
        warehouse = WarehouseSamples.centralWarehouse,
        vehicleLoads = VehicleSamples.sampleLoads
    )
}

@Preview(name = "Warehouse Overview - Mobile - Heavy Data", widthDp = 400, heightDp = 800)
@Composable
fun WarehouseMobileHeavyDataPreview() {
    WarehouseOperationalOverview(
        warehouse = WarehouseSamples.heavyDataWarehouse,
        vehicleLoads = List(12) { index -> "TRK-${200 + index}" to (35.0 + (index * 5.0)) }.toMap()
    )
}

@Preview(name = "Warehouse Overview - Wide Screen - Heavy Data", widthDp = 1000, heightDp = 700)
@Composable
fun WarehouseWideScreenHeavyDataPreview() {
    WarehouseOperationalOverview(
        warehouse = WarehouseSamples.heavyDataWarehouse,
        vehicleLoads = VehicleSamples.heavyDataLoads
    )
}

@Preview(name = "Warehouse Overview - Mobile - Overloaded", widthDp = 400, heightDp = 800)
@Composable
fun WarehouseMobileOverloadedPreview() {
    WarehouseOperationalOverview(
        warehouse = WarehouseSamples.overloadedWarehouse,
        vehicleLoads = VehicleSamples.sampleLoads
    )
}

@Preview(name = "Warehouse Overview - Wide Screen - Overloaded", widthDp = 1000, heightDp = 700)
@Composable
fun WarehouseWideScreenOverloadedPreview() {
    WarehouseOperationalOverview(
        warehouse = WarehouseSamples.overloadedWarehouse,
        vehicleLoads = VehicleSamples.sampleLoads
    )
}

@Preview(name = "Warehouse Overview - Mobile - Zero State", widthDp = 400, heightDp = 800)
@Composable
fun WarehouseMobileZeroStatePreview() {
    WarehouseOperationalOverview(
        warehouse = WarehouseSamples.emptyWarehouse,
        vehicleLoads = emptyMap()
    )
}

@Preview(name = "Warehouse Overview - Wide Screen - Zero State", widthDp = 1000, heightDp = 700)
@Composable
fun WarehouseWideScreenZeroStatePreview() {
    WarehouseOperationalOverview(
        warehouse = WarehouseSamples.emptyWarehouse,
        vehicleLoads = emptyMap()
    )
}

