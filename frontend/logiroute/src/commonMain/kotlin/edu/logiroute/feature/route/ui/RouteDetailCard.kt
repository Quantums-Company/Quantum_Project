package edu.logiroute.feature.route.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.feature.route.ui.component.RouteHeader
import edu.logiroute.feature.route.ui.component.RouteMetrics
import edu.logiroute.sampledata.RouteSamples
import edu.logiroute.style.Card
import org.bytebloom.logiroute.domain.model.Route

@Composable
fun RouteDetailCard(
    route: Route,
    modifier: Modifier = Modifier,
    onClick: (Route) -> Unit = {}
) = Card(
    modifier = modifier,
    onClick = { onClick.invoke(route) },
) {
    RouteHeader(route = route)
    RouteMetrics(route = route)
}

@Preview(name = "Route Transit - Edge Cases")
@Composable
fun RouteDetailCardPreview() = Column(
    modifier = Modifier.padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    RouteDetailCard(route = RouteSamples.longCrossCountryRoute)
    RouteDetailCard(route = RouteSamples.shortLocalRoute)
}