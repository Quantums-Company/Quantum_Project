package edu.logiroute.logiroute.presentation.ui.components.route

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.presentation.ui.sampledata.RouteSamples
import edu.logiroute.logiroute.presentation.ui.theme.TextSecondary
import org.bytebloom.domain.model.Route

@Composable
fun RouteDistance(route: Route) {
    Text(
        text = "${route.distanceKm} km",
        color = TextSecondary,
        fontSize = 12.sp
    )
}

@Preview
@Composable
fun RouteDistancePreview() {
    RouteDistance(RouteSamples.sampleRoute)
}