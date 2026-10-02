package edu.logiroute.logiroute.presentation.ui.components.vehicle

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import edu.logiroute.logiroute.presentation.ui.style.CardRow
import edu.logiroute.logiroute.presentation.ui.style.formatCostPerKm
import edu.logiroute.logiroute.presentation.ui.style.formatMaxCapacity

@Composable
fun VehicleMetrics(
    costPerKm: Double,
    maxCapacityKg: Double,
    modifier: Modifier = Modifier
) {
    val costPerKmString = formatCostPerKm(costPerKm)

    val maxCapacityString = formatMaxCapacity(maxCapacityKg)

    CardRow(modifier = modifier) {
        CostRate(costPerKmString)
        MaxCapacity(maxCapacityString)
    }
}

@Preview
@Composable
fun VehicleMetricsPreview() {
    VehicleMetrics(20000.0, 778.0)
}