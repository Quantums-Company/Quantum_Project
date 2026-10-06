package edu.logiroute.logiroute.ui.components.`package`

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun PriorityPackage(priority: String, textColor: Color) {
    Text(
        text = priority,
        color = textColor,
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        maxLines = 1
    )
}

@Preview
@Composable
fun PreviewPriorityPackage() {
    PriorityPackage("urgent", textColor = Color.White)
}