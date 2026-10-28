package edu.logiroute.logiroute.ui.components.vehicle

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import edu.logiroute.logiroute.ui.style.RowCard
import edu.logiroute.logiroute.ui.style.formatCostPerKm
import edu.logiroute.logiroute.ui.style.formatMaxCapacity

@Composable
fun VehicleMetrics(
    costPerKm: Double,
    maxCapacityKg: Double,
    modifier: Modifier = Modifier
) {
    val costPerKmString = formatCostPerKm(costPerKm)

    val maxCapacityString = formatMaxCapacity(maxCapacityKg)

    RowCard(modifier = modifier) {
        CostRate(costPerKmString)
        MaxCapacity(maxCapacityString)
    }
}

@Preview
@Composable
fun VehicleMetricsPreview() {
    VehicleMetrics(20000.0, 778.0)
}