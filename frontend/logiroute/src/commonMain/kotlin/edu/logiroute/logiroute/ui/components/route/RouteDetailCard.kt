package edu.logiroute.logiroute.ui.components.route

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.sampledata.RouteSamples
import edu.logiroute.logiroute.ui.style.Card
import org.bytebloom.domain.model.Route

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