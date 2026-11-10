package edu.logiroute.feature.route.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import edu.logiroute.sampledata.RouteSamples
import edu.logiroute.style.RowCard
import org.bytebloom.logiroute.domain.model.Route

@Composable
fun RouteMetrics(route: Route) {
    RowCard {
        RouteDistance(route)
        RouteDelay(route)
    }
}

@Preview
@Composable
fun RouteMetricsPreview() {
    RouteMetrics(RouteSamples.sampleRoute)
}