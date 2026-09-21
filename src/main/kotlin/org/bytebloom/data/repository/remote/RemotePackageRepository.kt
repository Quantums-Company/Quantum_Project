package org.bytebloom.data.repository.remote

import org.bytebloom.data.remote.dto.packageDto.PackageRequestDto
import org.bytebloom.data.remote.mapper.PackageDtoMapper
import org.bytebloom.data.source.remote.PackageRemoteDataSource
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.util.retryWithBackoff
import java.time.Instant

class RemotePackageRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val remoteDataSource: PackageRemoteDataSource
) : PackageRepository {

    override suspend fun getAll(): List<Package> {
        val dtos = retryWithBackoff { remoteDataSource.loadAll() }.getOrThrow()
        return PackageDtoMapper.toDomainList(dtos, warehousesById)
    }

    override suspend fun getById(id: String): Package? {
        val dto = retryWithBackoff { remoteDataSource.loadById(id) }.getOrThrow()
        return dto?.let { PackageDtoMapper.toDomain(it, warehousesById) }
    }

    override suspend fun create(pkg: Package): Package {
        val dto = retryWithBackoff { remoteDataSource.create(pkg.toRequestDto()) }.getOrThrow()
        return dto?.let { PackageDtoMapper.toDomain(it, warehousesById) } ?: pkg
    }

    override suspend fun update(pkg: Package): Package {
        val dto = retryWithBackoff { remoteDataSource.update(pkg.id, pkg.toRequestDto()) }.getOrThrow()
        return dto?.let { PackageDtoMapper.toDomain(it, warehousesById) } ?: pkg
    }

    override suspend fun delete(id: String): Boolean {
        return retryWithBackoff { remoteDataSource.delete(id) }.getOrThrow()
    }

    private fun Package.toRequestDto() = PackageRequestDto(
        id = id, weight = weight,
        originWarehouseId = originWarehouse.id,
        destinationWarehouseId = destinationWarehouse.id,
        priority = priority.name,
        updatedAt = Instant.now().toString()
    )
}