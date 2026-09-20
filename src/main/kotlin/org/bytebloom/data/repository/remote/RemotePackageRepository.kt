package org.bytebloom.data.repository.remote

import org.bytebloom.data.remote.dto.packageDto.PackageRequestDto
import org.bytebloom.data.remote.mapper.PackageDtoMapper
import org.bytebloom.data.source.remote.PackageRemoteDataSource
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.model.Warehouse
import org.bytebloom.domain.repository.PackageRepository
import java.time.Instant

class RemotePackageRepository(
    private val warehousesById: Map<String, Warehouse>,
    private val remoteDataSource: PackageRemoteDataSource
) : PackageRepository {

    override suspend fun getAll(): List<Package> =
        PackageDtoMapper.toDomainList(remoteDataSource.loadAll(), warehousesById)

    override suspend fun getById(id: String): Package? =
        remoteDataSource.loadById(id)?.let { PackageDtoMapper.toDomain(it, warehousesById) }

    override suspend fun create(pkg: Package): Package {
        val dto = remoteDataSource.create(pkg.toRequestDto())
        return dto?.let { PackageDtoMapper.toDomain(it, warehousesById) } ?: pkg
    }

    override suspend fun update(pkg: Package): Package {
        val dto = remoteDataSource.update(pkg.id, pkg.toRequestDto())
        return dto?.let { PackageDtoMapper.toDomain(it, warehousesById) } ?: pkg
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