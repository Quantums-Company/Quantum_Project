package edu.logiroute.logiroute.presentation.ui.components.vehicle

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.presentation.ui.theme.TextSecondary

@Composable
fun CapacityPercentageLabel(
    percentage: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = "$percentage%",
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Preview
@Composable
fun CapacityPercentageLabelPreview() = CapacityPercentageLabel(75)