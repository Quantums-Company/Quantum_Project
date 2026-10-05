package edu.logiroute.logiroute.ui.components.route

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import edu.logiroute.logiroute.ui.sampledata.RouteSamples
import edu.logiroute.logiroute.ui.style.CardRow
import org.bytebloom.domain.model.Route

@Composable
fun RouteHeaderRow(route: Route) = CardRow{
    NameOriginWarehouse(route)
    RouteDirectionArrow()
    NameDestinationWarehouse(route)
}

@Preview
@Composable
fun RouteHeaderRowPreview() {
    RouteHeaderRow(RouteSamples.sampleRoute)
}