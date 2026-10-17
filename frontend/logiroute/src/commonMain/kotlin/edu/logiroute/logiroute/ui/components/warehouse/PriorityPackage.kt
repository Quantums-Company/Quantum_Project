package edu.logiroute.logiroute.ui.components.warehouse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.components.`package`.PackagePriorityBadge
import edu.logiroute.logiroute.ui.sampledata.PackageSamples
import edu.logiroute.logiroute.ui.theme.CharcoalBlue
import edu.logiroute.logiroute.ui.theme.TextDisabled
import edu.logiroute.logiroute.ui.theme.TextSecondary
import org.bytebloom.domain.model.Package

@Composable
fun PriorityPackage(cargo: Package?) {
    cargo?.let { cargo ->
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            CargoLabel()
            PackagePriorityBadge(cargo = cargo, modifier = Modifier.fillMaxWidth())
        }
    } ?: run {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(CharcoalBlue)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Pending Cargo",
                color = TextDisabled,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CargoLabel() {
    Text(text = "cargo", color = TextSecondary, fontSize = 11.sp)
}

@Preview
@Composable
fun PriorityPackagePreview() {
    PriorityPackage(PackageSamples.urgentCargo)
}