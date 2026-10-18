package org.bytebloom.logiroute.presentation.presenter

/**
 * Everything a UI needs, in one place.
 * A console UI, a Compose desktop UI or an Android Activity all receive the same object.
 */
class AppPresenters(
    val dashboard: DashboardPresenter,
    val route: RoutePresenter,
    val classification: ClassificationPresenter,
    val telemetry: TelemetryPresenter
) {
    fun close() {
        dashboard.close()
        route.close()
        classification.close()
        telemetry.close()
    }
}
