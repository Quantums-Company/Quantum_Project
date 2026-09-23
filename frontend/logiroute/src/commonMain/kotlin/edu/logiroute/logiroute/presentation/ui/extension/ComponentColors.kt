package edu.logiroute.logiroute.presentation.ui.extension

import androidx.compose.ui.graphics.Color
import edu.logiroute.logiroute.domain.model.Priority
import edu.logiroute.logiroute.domain.model.RegionZone
import edu.logiroute.logiroute.presentation.ui.theme.*

val Priority.brandColors: Pair<Color, Color>
    get() = when (this) {
        Priority.URGENT -> ErrorRed to InkBlack
        Priority.STANDARD -> LightGreen to InkBlack
        Priority.LOW -> CharcoalBlue to TextSecondary
    }

val RegionZone.zoneBadgeColor: Color
    get() = when (this) {
        RegionZone.NORTH,
        RegionZone.EAST,
        RegionZone.CENTRAL,
        RegionZone.SOUTH,
        RegionZone.WEST
            -> CyberSprout
    }