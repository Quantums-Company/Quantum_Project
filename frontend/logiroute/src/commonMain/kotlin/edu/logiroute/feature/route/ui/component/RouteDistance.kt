package edu.logiroute.feature.route.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.sampledata.RouteSamples
import edu.logiroute.theme.TextSecondary
import org.bytebloom.logiroute.domain.model.Route

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