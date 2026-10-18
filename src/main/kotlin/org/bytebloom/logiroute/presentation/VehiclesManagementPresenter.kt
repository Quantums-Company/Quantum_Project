package org.bytebloom.logiroute.presentation

import kotlinx.coroutines.CancellationException
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.presentation.screen.VehiclesManagementScreen

class VehiclesManagementPresenter(
    private val getAllVehicles: GetAllVehiclesUseCase,
    private val screen: VehiclesManagementScreen
) {
    suspend fun loadVehicles() {
        screen.showLoadingIndicator(true)
        try {
            screen.showVehicles(getAllVehicles())
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            screen.showErrorMessage(error.userMessage())
        } finally {
            screen.showLoadingIndicator(false)
        }
    }
}
