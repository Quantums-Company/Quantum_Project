package edu.logiroute.logiroute.presentation.ui.components.vehicle

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.presentation.ui.theme.TextSecondary

@Composable
fun CostRate(costPerKmString: AnnotatedString) {
    Column {
        Text(text = "Cost Rate", color = TextSecondary, fontSize = 11.sp)
        Text(text = costPerKmString, fontSize = 13.sp)
    }
}

@Preview
@Composable
fun CostRatePreview() {
    CostRate(costPerKmString = AnnotatedString("Km"))
}