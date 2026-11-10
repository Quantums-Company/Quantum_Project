package edu.logiroute.feature.route.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.sampledata.RouteSamples
import edu.logiroute.style.RowCard
import org.bytebloom.logiroute.domain.model.Route

@Composable
fun RouteHeader(route: Route) = RowCard {
    NameOriginWarehouse(route, modifier = Modifier.weight(1f))
    Spacer(modifier = Modifier.width(8.dp))
    RouteDirectionArrow()
    Spacer(modifier = Modifier.width(8.dp))
    NameDestinationWarehouse(
        route,
        modifier = Modifier.weight(1f)
    )
}

@Preview
@Composable
fun RouteHeaderPreview() {
    RouteHeader(RouteSamples.longCrossCountryRoute)
}