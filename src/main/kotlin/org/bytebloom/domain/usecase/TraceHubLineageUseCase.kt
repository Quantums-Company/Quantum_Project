package org.bytebloom.domain.usecase

import org.bytebloom.domain.model.Warehouse


class TraceHubLineageUseCase{
    operator fun invoke(startWarehouse: Warehouse, targetWarehouse: Warehouse): List<Warehouse> =
        listOf(startWarehouse) + startWarehouse.outgoingRoutes
            .map { it.destinationWarehouse }
            .filter { it.id == targetWarehouse.id }
}
