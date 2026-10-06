package edu.logiroute.logiroute.ui.components.route

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.sampledata.RouteSamples
import edu.logiroute.logiroute.ui.theme.TextPrimary
import org.bytebloom.domain.model.Route

@Composable
fun NameOriginWarehouse(route: Route) {
    Text(
        text = route.originWarehouse.name,
        color = TextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Preview
@Composable
fun NameOriginWarehousePreview() {
    NameOriginWarehouse(RouteSamples.sampleRoute)
}