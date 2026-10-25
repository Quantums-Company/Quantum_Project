package edu.logiroute.logiroute.ui.components.warehouse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.theme.Border
import edu.logiroute.logiroute.ui.theme.InkBlack
import edu.logiroute.logiroute.ui.theme.TextPrimary
import edu.logiroute.logiroute.ui.theme.TextSecondary

@Composable
fun WarehouseCargoHeader(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(InkBlack)
            .drawBehind {
                drawBottomBorder()
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "($count)",
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun DrawScope.drawBottomBorder() {
    val strokeWidth = 1.dp.toPx()
    drawLine(
        color = Border,
        start = Offset(0f, size.height),
        end = Offset(size.width, size.height),
        strokeWidth = strokeWidth
    )
}