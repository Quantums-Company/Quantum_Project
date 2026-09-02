package org.bytebloom.domain.usecase.queries.reporting

data class WarehouseReport(
    val warehouseId: String,
    val packageCount: Int,
    val totalPackageWeight: Double,
    val totalVehicleCapacity: Double
)