package edu.logiroute.logiroute.ui.components.`package`

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.sampledata.PackageSamples


@Composable
fun PackageId(packageId: String, textColor: Color) {
    Text(
        text = packageId,
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview
@Composable
fun PreviewPackageId() {
    PackageId(PackageSamples.standardCargo.id, Color.White)
}