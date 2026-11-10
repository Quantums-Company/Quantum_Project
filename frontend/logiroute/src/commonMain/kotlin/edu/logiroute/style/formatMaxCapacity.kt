package edu.logiroute.style

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import edu.logiroute.theme.TextPrimary
import edu.logiroute.theme.TextSecondary

@Composable
fun formatMaxCapacity(maxCapacityKg: Double): AnnotatedString = buildAnnotatedString {
    withStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.Bold)) {
        append("${maxCapacityKg.toInt()} ")
    }
    withStyle(SpanStyle(color = TextSecondary)) {
        append("kg")
    }
}