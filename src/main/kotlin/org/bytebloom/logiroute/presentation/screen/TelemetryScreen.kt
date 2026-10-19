package org.bytebloom.logiroute.presentation.screen

import org.bytebloom.logiroute.domain.model.VehicleTelemetry
import org.bytebloom.logiroute.domain.model.result.VehicleDelayNotification

/**
 * What the live telemetry screen must be able to show. Implemented by the UI.
 *
 * The presenter calls these functions from a background thread while the stream is running;
 * an Android implementation must switch to the main thread before touching any view.
 */
interface TelemetryScreen : BaseScreen {

    fun showTrackingStarted(vehicleIds: Set<String>)

    fun showTelemetry(telemetry: VehicleTelemetry)

    fun showDelayAlert(notification: VehicleDelayNotification)

    /** Called once when the stream ends (stopped by the user, finished by itself, or failed). */
    fun showTrackingStopped(latestReadings: List<VehicleTelemetry>)
}
