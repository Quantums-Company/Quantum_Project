package edu.logiroute.logiroute.ui.style

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.theme.CyberSprout

@Composable
fun BoxCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(4.dp),
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = CyberSprout ,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .width(80.dp)
            .height(30.dp)
            .clip(shape)
            .background(backgroundColor)
            .padding(horizontal = 2.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}