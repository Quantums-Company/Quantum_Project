package edu.logiroute.logiroute.presentation.ui.components.vehicle

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.presentation.ui.theme.*

@Composable
fun VehicleCapacityIndicator(
    currentLoadKg: Double,
    maxCapacityKg: Double,
    modifier: Modifier = Modifier
) {
    val ratio = calculateLoadRatio(currentLoadKg, maxCapacityKg)
    val percentageInt = (ratio * 100).toInt()
    val fillColor = getCapacityColor(ratio)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CapacityProgressIndicator(
            ratio = ratio,
            fillColor = fillColor,
            modifier = Modifier.weight(1f)
        )
        CapacityPercentageLabel(
            percentage = percentageInt,
            modifier = Modifier.width(40.dp)
        )
    }
}

private fun calculateLoadRatio(current: Double, max: Double): Float =
    max.takeIf { it > 0.0 }?.let {
        (current / it).toFloat().coerceIn(0.0f, 1.0f)
    } ?: 0.0f

private fun getCapacityColor(ratio: Float): Color = when {
    ratio > 0.90f -> ErrorRed
    ratio >= 0.70f -> WarningAmber
    else -> LightGreen
}

@Preview(backgroundColor = 0xFF121921)
@Composable
fun VehicleCapacityIndicatorPreview() {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        VehicleCapacityIndicator(currentLoadKg = 5000.0, maxCapacityKg = 10000.0)
        VehicleCapacityIndicator(currentLoadKg = 8200.0, maxCapacityKg = 10000.0)
        VehicleCapacityIndicator(currentLoadKg = 10500.0, maxCapacityKg = 10000.0)
    }
}