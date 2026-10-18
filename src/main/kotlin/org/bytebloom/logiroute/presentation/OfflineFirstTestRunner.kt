package org.bytebloom.logiroute.presentation

import kotlinx.coroutines.runBlocking
import org.bytebloom.logiroute.domain.usecase.packages.GetAllPackagesUseCase
import org.bytebloom.logiroute.domain.usecase.routes.GetAllRoutesUseCase
import org.bytebloom.logiroute.domain.usecase.vehicles.GetAllVehiclesUseCase
import org.bytebloom.logiroute.domain.usecase.warehouses.GetAllWarehousesUseCase
import org.koin.core.Koin

class OfflineFirstTestRunner(
    private val koin: Koin
) {
    private val getAllWarehouses: GetAllWarehousesUseCase by lazy { koin.get() }
    private val getAllRoutes: GetAllRoutesUseCase by lazy { koin.get() }
    private val getAllVehicles: GetAllVehiclesUseCase by lazy { koin.get() }
    private val getAllPackages:GetAllPackagesUseCase by lazy { koin.get() }

    fun run() = runBlocking {
        var isRunning = true
        while (isRunning) {
            printMenu()
            isRunning = handleUserChoice(readlnOrNull()?.trim())
        }
    }

    private fun printMenu() {
        println()
        println("=== Offline-first test ===")
        println("1. Fetch warehouses")
        println("2. Fetch routes")
        println("3. Fetch vehicles")
        println("4. Fetch packages")
        println("5. Fetch all tables")
        println("0. Exit")
        print("Choose: ")
    }

    private suspend fun handleUserChoice(choice: String?): Boolean = when (choice) {
        "1" -> {
            val result = getAllWarehouses()
            println("Warehouses: ${result.size}")
            true
        }

        "2" -> {
            val result = getAllRoutes()
            println("Routes: ${result.size}")
            true
        }

        "3" -> {
            val result = getAllVehicles()
            println("Vehicles: ${result.size}")
            true
        }

        "4" -> {
            val result = getAllPackages()
            println("Packages: ${result.size}")
            true
        }

        "5" -> {
            fetchAndPrintAll()
            true
        }

        "0" -> false

        else -> {
            println("Invalid option.")
            true
        }
    }

    private suspend fun fetchAndPrintAll() {
        val w = getAllWarehouses()
        val r = getAllRoutes()
        val v = getAllVehicles()
        val p = getAllPackages()

        println("Warehouses: ${w.size}")
        println("Routes: ${r.size}")
        println("Vehicles: ${v.size}")
        println("Packages: ${p.size}")
    }
}