package org.bytebloom.logiroute.presentation.screen

/**
 * The part of the "View contract" that every screen shares.
 *
 * A screen interface (for example `ShortestRouteScreen`) extends this one and adds the
 * `show…` functions that are specific to that screen. The presenter only ever talks to
 * this interface, so the real implementation can be a terminal UI today and an Android
 * Fragment / Compose screen tomorrow.
 */
interface BaseScreen {

    fun showLoadingIndicator(isLoading: Boolean)

    fun showErrorMessage(errorMessage: String)
}
