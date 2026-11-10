package edu.logiroute.feature.`package`.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.sampledata.PackageSamples
import edu.logiroute.theme.TextSecondary
import org.bytebloom.logiroute.domain.model.Package

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