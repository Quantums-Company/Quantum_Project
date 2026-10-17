package edu.logiroute.logiroute.ui.components.route

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.theme.SapphireSky

@Composable
fun RouteDirectionArrow() {
    Text(
        text = " ➔ ",
        color = SapphireSky,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview
@Composable
fun RouteDirectionArrowPreview() {
    RouteDirectionArrow()
}