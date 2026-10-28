package edu.logiroute.logiroute.ui.components.warehouse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.ui.theme.TextSecondary
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import quantum_project.frontend.logiroute.generated.resources.Res
import quantum_project.frontend.logiroute.generated.resources.empty_cargo_queue
import quantum_project.frontend.logiroute.generated.resources.warehouse_overloaded_warning

@Composable
fun WarehouseCargoEmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
        )
    }
}

@Preview
@Composable
fun WarehouseCargoEmptyPreview() {
    WarehouseCargoEmptyState(stringResource(Res.string.empty_cargo_queue))
}