package org.bytebloom.logiroute.data.repository.remote

import org.bytebloom.logiroute.data.remote.dto.packageDto.PackageRequestDto
import org.bytebloom.logiroute.data.remote.mapper.PackageDtoMapper
import org.bytebloom.logiroute.data.source.remote.PackageRemoteDataSource
import org.bytebloom.logiroute.domain.model.Package
import org.bytebloom.logiroute.domain.repository.PackageRepository
import org.bytebloom.logiroute.domain.repository.WarehouseRepository
import java.time.Instant
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class RemotePackageRepository(
    private val warehouseRepository: WarehouseRepository,
    private val remoteDataSource: PackageRemoteDataSource
) : PackageRepository {

    private suspend fun <T> loadWithMapper(load: suspend () -> T): Pair<T, PackageDtoMapper> = coroutineScope {
        val result = async { load() }
        val warehouses = async { warehouseRepository.getAll() }
        result.await() to PackageDtoMapper(warehouses.await().associateBy { it.id } )
    }

    override suspend fun getAll(): List<Package> {
        val (dto, mapper) = loadWithMapper { remoteDataSource.loadAll() }
        return mapper.mapList(dto).succeeded}

    override suspend fun getById(id: String): Package? {
        val (dto, mapper) = loadWithMapper { remoteDataSource.loadById(id) }
        return dto?.let ( mapper::map )
    }

    override suspend fun create(pkg: Package): Package {
        val (dto, mapper) =loadWithMapper { remoteDataSource.create(pkg.toRequestDto())}
        return dto?.let ( mapper::map ) ?: pkg
    }

    override suspend fun update(pkg: Package): Package {
        val (dto, mapper) = loadWithMapper { remoteDataSource.update(pkg.id, pkg.toRequestDto())}
        return dto?.let ( mapper::map)  ?: pkg
    }

    override suspend fun delete(id: String): Boolean =
        remoteDataSource.delete(id)

    private fun Package.toRequestDto() = PackageRequestDto(
        id = id, weight = weight,
        originWarehouseId = originWarehouse.id,
        destinationWarehouseId = destinationWarehouse.id,
        priority = priority.name,
        updatedAt = Instant.now().toString()
    )
}