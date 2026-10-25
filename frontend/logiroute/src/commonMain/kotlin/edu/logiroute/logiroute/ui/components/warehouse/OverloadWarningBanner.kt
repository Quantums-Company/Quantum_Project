package edu.logiroute.logiroute.ui.components.warehouse

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.components.vehicle.ActiveFleetGridPreview
import edu.logiroute.logiroute.ui.sampledata.VehicleSamples
import edu.logiroute.logiroute.ui.sampledata.WarehouseSamples
import edu.logiroute.logiroute.ui.style.BoxCard
import edu.logiroute.logiroute.ui.theme.ErrorRed
import org.jetbrains.compose.resources.stringResource
import quantum_project.frontend.logiroute.generated.resources.Res
import quantum_project.frontend.logiroute.generated.resources.warehouse_overloaded_warning

@Composable
fun OverloadWarningBanner(excessWeight: Double) {
    val warningText = stringResource(Res.string.warehouse_overloaded_warning, excessWeight)
    BoxCard(
        modifier =
            Modifier.fillMaxWidth(),
        backgroundColor = ErrorRed.copy(alpha = 0.15f)
    ) {
        Text(
            text = warningText,
            color = ErrorRed,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun OverloadWarningBannerPreview() {
    OverloadWarningBanner(excessWeight = 1000.0)
}