package edu.logiroute.logiroute.ui.components.`package`

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.sampledata.PackageSamples
import edu.logiroute.logiroute.ui.theme.TextSecondary
import org.bytebloom.domain.model.Package

@Composable
fun WeightPackage(cargo: Package) {
    Text(
        text = "${cargo.weight} kg",
        color = TextSecondary,
        fontSize = 11.sp,
        maxLines = 1
    )
}

@Preview
@Composable
fun WeightPackagePreview() {
    WeightPackage(PackageSamples.standardCargo)
}