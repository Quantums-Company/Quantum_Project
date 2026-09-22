package edu.logiroute.logiroute.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.domain.model.Package
import edu.logiroute.logiroute.presentation.ui.theme.TextSecondary
import edu.logiroute.logiroute.presentation.ui.extension.brandColors

@Composable
fun PackagePriorityBadge(
    cargo: Package,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = cargo.priority.brandColors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = cargo.displayTitle(),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = cargo.displayWeight(),
            color = TextSecondary,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}

private fun Package.displayTitle(): String = "[$id] ${priority.name}"

private fun Package.displayWeight(): String = "$weight kg"