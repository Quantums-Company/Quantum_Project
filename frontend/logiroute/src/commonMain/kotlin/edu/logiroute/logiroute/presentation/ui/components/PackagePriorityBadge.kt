package edu.logiroute.logiroute.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.presentation.ui.sampledata.PackageSamples
import org.bytebloom.domain.model.Package
import edu.logiroute.logiroute.presentation.ui.theme.TextSecondary
import edu.logiroute.logiroute.presentation.ui.theme.CharcoalBlue
import edu.logiroute.logiroute.presentation.ui.theme.ErrorRed
import edu.logiroute.logiroute.presentation.ui.theme.InkBlack
import edu.logiroute.logiroute.presentation.ui.theme.LightGreen
import org.bytebloom.domain.model.Priority

@Composable
fun PackagePriorityBadge(
    cargo: Package,
    modifier: Modifier = Modifier
) {
    val priority = Priority.from(cargo.priority.name).name

    val backgroundColor = when (priority) {
        "URGENT" -> ErrorRed
        "STANDARD" -> LightGreen
        "LOW" -> CharcoalBlue
        else -> ErrorRed
    }

    val textColor = when (priority) {
        "URGENT" -> InkBlack
        "STANDARD" -> InkBlack
        "LOW" -> TextSecondary
        else -> ErrorRed
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            DisplayIdPackage(cargo, textColor)
            DisplayPriorityPackage(priority, textColor)
        }

        DisplayWeightPackage(cargo)
    }
}

@Composable
private fun DisplayPriorityPackage(priority: String, textColor: Color) {
    Text(
        text = priority,
        color = textColor,
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        maxLines = 1
    )
}

@Composable
private fun DisplayIdPackage(cargo: Package, textColor: Color) {
    Text(
        text = cargo.id,
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun DisplayWeightPackage(cargo: Package) {
    Text(
        text = "${cargo.weight} kg",
        color = TextSecondary,
        fontSize = 11.sp,
        maxLines = 1
    )
}

@Preview
@Composable
fun DisplayPriorityPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PackagePriorityBadge(cargo = PackageSamples.standardCargo)
        PackagePriorityBadge(cargo = PackageSamples.urgentCargo)
        PackagePriorityBadge(cargo = PackageSamples.lowCargo)
    }
}