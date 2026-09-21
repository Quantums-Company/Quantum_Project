package org.bytebloom.data.repository.remote

import org.bytebloom.data.remote.dto.warehouseDto.WarehouseRequestDto
import org.bytebloom.data.remote.mapper.WarehouseDtoMapper
import org.bytebloom.data.source.remote.WarehouseRemoteDataSource
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.util.retryWithBackoff
import java.time.Instant

class RemoteWarehouseRepository(
    private val remoteDataSource: WarehouseRemoteDataSource
) : WarehouseRepository {

    override suspend fun getAll(): List<Warehouse> {
        val dtos = retryWithBackoff { remoteDataSource.loadAll() }.getOrThrow()
        return WarehouseDtoMapper.toDomainList(dtos)
    }

    override suspend fun getById(id: String): Warehouse? {
        val dto = retryWithBackoff { remoteDataSource.loadById(id) }.getOrThrow()
        return dto?.let { WarehouseDtoMapper.toDomain(it) }
    }

    override suspend fun create(warehouse: Warehouse): Warehouse {
        val dto = retryWithBackoff { remoteDataSource.create(warehouse.toRequestDto()) }.getOrThrow()
        return dto?.let { WarehouseDtoMapper.toDomain(it) } ?: warehouse
    }

    override suspend fun update(warehouse: Warehouse): Warehouse {
        val dto = retryWithBackoff { remoteDataSource.update(warehouse.id, warehouse.toRequestDto()) }.getOrThrow()
        return dto?.let { WarehouseDtoMapper.toDomain(it) } ?: warehouse
    }

    override suspend fun delete(id: String): Boolean {
        return retryWithBackoff { remoteDataSource.delete(id) }.getOrThrow()
    }

    private fun Warehouse.toRequestDto() = WarehouseRequestDto(
        id = id, name = name, regionalZone = regionalZone,
        longitude = longitude, latitude = latitude,
        updatedAt = Instant.now().toString()
    )
}