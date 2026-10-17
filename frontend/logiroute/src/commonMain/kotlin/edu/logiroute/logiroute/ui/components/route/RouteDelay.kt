package edu.logiroute.logiroute.ui.components.route

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.sampledata.RouteSamples
import edu.logiroute.logiroute.ui.theme.TextSecondary
import edu.logiroute.logiroute.ui.theme.WarningAmber
import org.bytebloom.domain.model.Route

@Composable
fun RouteDelay(route: Route) {
    val delayColor = route.typicalDelayMin.takeIf { it > 60 }?.let { WarningAmber } ?: TextSecondary
    Text(
        text = "${route.typicalDelayMin} min delay",
        color = delayColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
}

@Preview
@Composable
fun RouteDelayPreview() {
    RouteDelay(RouteSamples.sampleRoute)
}