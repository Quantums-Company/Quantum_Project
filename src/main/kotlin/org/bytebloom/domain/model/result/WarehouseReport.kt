package org.bytebloom.domain.model.result

data class WarehouseReport(
    val warehouseId: String,
    val packageCount: Int,
    val totalPackageWeight: Double,
    val totalVehicleCapacity: Double
)