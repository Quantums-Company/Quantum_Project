package edu.logiroute.logiroute

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.logiroute.logiroute.presentation.ui.preview.PackagePriorityBadgesAllStatesPreview
import edu.logiroute.logiroute.presentation.ui.preview.WarehouseIdentityBadgesPreview
import edu.logiroute.logiroute.presentation.ui.preview.WarehouseSummaryCardsPreview
import edu.logiroute.logiroute.presentation.ui.theme.InkBlack

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showPackages by remember { mutableStateOf(false) }
        var showWarehouses by remember { mutableStateOf(false) }
        var showSummaries by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .background(InkBlack)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
            ) {
                Button(onClick = { showPackages = !showPackages }) {
                    Text(if (showPackages) "Hide Packages" else "Show Packages")
                }

                Button(onClick = { showWarehouses = !showWarehouses }) {
                    Text(if (showWarehouses) "Hide Warehouses" else "Show Warehouses")
                }

                Button(onClick = { showSummaries = !showSummaries }) {
                    Text(if (showSummaries) "Hide Summaries" else "Show Summaries")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(showPackages) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PackagePriorityBadgesAllStatesPreview(
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            AnimatedVisibility(showWarehouses) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    WarehouseIdentityBadgesPreview(
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                }

            }

            AnimatedVisibility(showSummaries) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    WarehouseSummaryCardsPreview(
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }
    }
}