package org.bytebloom.data.repository.remote

import org.bytebloom.data.remote.dto.warehouseDto.WarehouseRequestDto
import org.bytebloom.data.remote.mapper.WarehouseDtoMapper
import org.bytebloom.data.source.remote.WarehouseRemoteDataSource
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository
import java.time.Instant

class RemoteWarehouseRepository(
    private val remoteDataSource: WarehouseRemoteDataSource
) : WarehouseRepository {

    override suspend fun getAll(): List<Warehouse> =
        WarehouseDtoMapper.toDomainList(remoteDataSource.loadAll())

    override suspend fun getById(id: String): Warehouse? =
        remoteDataSource.loadById(id)?.let { WarehouseDtoMapper.toDomain(it) }

    override suspend fun create(warehouse: Warehouse): Warehouse {
        val dto = remoteDataSource.create(warehouse.toRequestDto())
        return dto?.let { WarehouseDtoMapper.toDomain(it) } ?: warehouse
    }

    override suspend fun update(warehouse: Warehouse): Warehouse {
        val dto = remoteDataSource.update(warehouse.id, warehouse.toRequestDto())
        return dto?.let { WarehouseDtoMapper.toDomain(it) } ?: warehouse
    }

    override suspend fun delete(id: String): Boolean =
        remoteDataSource.delete(id)

    private fun Warehouse.toRequestDto() = WarehouseRequestDto(
        id = id, name = name, regionalZone = regionalZone,
        longitude = longitude, latitude = latitude,
        updatedAt = Instant.now().toString()
    )
}