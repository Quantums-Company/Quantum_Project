package edu.logiroute.logiroute.ui.components.vehicle

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.theme.TextSecondary

@Composable
fun MaxCapacity(maxCapacityString: AnnotatedString) {
    Column(horizontalAlignment = Alignment.End) {
        Text(text = "Max Capacity", color = TextSecondary, fontSize = 11.sp)
        Text(text = maxCapacityString, fontSize = 13.sp)
    }
}