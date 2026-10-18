package org.bytebloom.logiroute.presentation.screen

interface CommandPatternScreen {
    fun showLoadingIndicator(isLoading: Boolean)
    fun showErrorMessage(errorMessage: String)
    fun showTitle()
    fun showStep(title: String, succeeded: Boolean, details: String)
    fun showState(label: String, queuedPackageIds: List<String>, stationedVehicleIds: List<String>, canUndo: Boolean, canRedo: Boolean)
    fun showFinished(message: String)
}
