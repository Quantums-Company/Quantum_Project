package edu.logiroute.feature.route.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.sampledata.RouteSamples
import edu.logiroute.theme.TextPrimary
import org.bytebloom.logiroute.domain.model.Route

@Composable
fun NameOriginWarehouse(route: Route, modifier: Modifier = Modifier) {
    Text(
        text = route.originWarehouse.name,
        modifier = modifier,
        color = TextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview
@Composable
fun NameOriginWarehousePreview() {
    NameOriginWarehouse(RouteSamples.sampleRoute)
}