package edu.logiroute.logiroute.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.logiroute.logiroute.domain.model.Warehouse
import edu.logiroute.logiroute.presentation.ui.theme.Border
import edu.logiroute.logiroute.presentation.ui.theme.CharcoalBlue
import edu.logiroute.logiroute.presentation.ui.theme.JetBlack
import edu.logiroute.logiroute.presentation.ui.theme.TextDisabled
import edu.logiroute.logiroute.presentation.ui.theme.TextPrimary
import edu.logiroute.logiroute.presentation.ui.theme.TextSecondary

@Composable
fun WarehouseSummaryCard(
    warehouse: Warehouse,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .background(JetBlack)
            .border(1.dp, Border, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        WarehouseIdentityBadge(
            warehouse = warehouse,
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Queued Cargo", color = TextSecondary, fontSize = 11.sp)
                Text(
                    text = warehouse.displayCargoSize(),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Stationed Fleet", color = TextSecondary, fontSize = 11.sp)
                Text(
                    text = warehouse.displayFleetSize(),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        val highestUrgentCargo = warehouse.findHighestPriorityCargo()

        if (highestUrgentCargo != null) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Highest Urgent Dispatch", color = TextSecondary, fontSize = 11.sp)
                PackagePriorityBadge(cargo = highestUrgentCargo, modifier = Modifier.fillMaxWidth())
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(CharcoalBlue)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Pending Cargo",
                    color = TextDisabled,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun Warehouse.displayCargoSize(): String = "${cargoQueue.size} Packages"

private fun Warehouse.displayFleetSize(): String = "${stationedVehicles.size} Vehicles"