package edu.logiroute.logiroute.ui.style

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.ui.theme.CharcoalBlue

@Composable
fun RowCard(
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    shape : Shape = RoundedCornerShape(8.dp),
    backgroundColor: Color = CharcoalBlue,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
){
    val baseModifier =modifier.fillMaxWidth().clip(shape)
        .background(backgroundColor)

    val finalModifier = baseModifier.then(
        onClick?.let { action ->
            Modifier.clickable { action.invoke() }
        } ?: Modifier
    )

    Row(
        modifier = finalModifier.padding(contentPadding),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ){
        content()
    }
}