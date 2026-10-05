package edu.logiroute.logiroute.ui.components.vehicle

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.sampledata.VehicleSamples
import edu.logiroute.logiroute.ui.theme.TextPrimary

@Composable
fun VehicleId(vehicleId: String) {
    Text(
        text = vehicleId,
        color = TextPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.End,
    )
}

@Preview
@Composable
fun VehicleIdPreview() {
    VehicleId(VehicleSamples.centralVehicle.id)
}