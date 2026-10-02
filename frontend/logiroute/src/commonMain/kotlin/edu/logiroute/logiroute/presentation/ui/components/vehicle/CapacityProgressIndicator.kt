package edu.logiroute.logiroute.presentation.ui.components.vehicle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.presentation.ui.theme.JetBlack
import edu.logiroute.logiroute.presentation.ui.theme.LightGreen

@Composable
fun CapacityProgressIndicator(
    ratio: Float,
    fillColor: Color,
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier
        .height(12.dp)
        .drawBehind {
            drawRoundRect(color = JetBlack, cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()))
            ratio.takeIf { it > 0f }?.let { activeRatio ->
                drawRoundRect(
                    color = fillColor,
                    size = Size(width = size.width * activeRatio, height = size.height),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }
)

@Preview
@Composable
fun CapacityProgressIndicatorPreview() = CapacityProgressIndicator(
    ratio = 0.8f,
    fillColor = LightGreen,
    modifier = Modifier.width(200.dp).padding(10.dp)
)