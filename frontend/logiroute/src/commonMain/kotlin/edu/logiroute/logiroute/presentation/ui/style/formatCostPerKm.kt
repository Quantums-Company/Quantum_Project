package edu.logiroute.logiroute.presentation.ui.style

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import edu.logiroute.logiroute.presentation.ui.theme.LightGreen
import edu.logiroute.logiroute.presentation.ui.theme.TextSecondary

@Composable
fun formatCostPerKm(costPerKm: Double): AnnotatedString = buildAnnotatedString {
    withStyle(SpanStyle(color = LightGreen, fontWeight = FontWeight.Bold)) {
        append("$$costPerKm")
    }
    withStyle(SpanStyle(color = TextSecondary)) {
        append("/km")
    }
}