package org.bytebloom.data.local.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.data.remote.dto.packageDto.PackageRequestDto
import org.bytebloom.data.remote.dto.packageDto.PackageResponseDto
import org.bytebloom.data.remote.mapper.PackageDtoMapper
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.WarehouseRepository

class SupabasePackageRepository(
    private val client: SupabaseClient,
    private val warehouseRepository: WarehouseRepository
) : PackageRepository {

    private suspend fun warehousesById() =
        warehouseRepository.getAll().associateBy { it.id }

    override suspend fun getAll(): List<Package> {
        val dtos = client.from(TableName.PACKAGES)
            .select()
            .decodeList<PackageResponseDto>()
        println("DEBUG: DTOs fetched from Supabase = ${dtos.size}")
        return PackageDtoMapper.toDomainList(dtos, warehousesById())
    }

    override suspend fun getById(id: String): Package? {
        val dto = client.from(TableName.PACKAGES)
            .select { filter { eq("id", id) } }
            .decodeSingleOrNull<PackageResponseDto>()
        return dto?.let { PackageDtoMapper.toDomain(it, warehousesById()) }
    }

    override suspend fun create(pkg: Package): Package {
        val request = PackageRequestDto(
            id = pkg.id,
            weight = pkg.weight,
            originWarehouseId = pkg.originWarehouse.id,
            destinationWarehouseId = pkg.destinationWarehouse.id,
            priority = pkg.priority.name
        )
        client.from(TableName.PACKAGES).insert(request)
        return pkg
    }

    override suspend fun update(pkg: Package): Package {
        val request = PackageRequestDto(
            id = pkg.id,
            weight = pkg.weight,
            originWarehouseId = pkg.originWarehouse.id,
            destinationWarehouseId = pkg.destinationWarehouse.id,
            priority = pkg.priority.name
        )
        client.from(TableName.PACKAGES).update(request) {
            filter { eq("id", pkg.id) }
        }
        return pkg
    }

    override suspend fun delete(id: String): Boolean {
        client.from(TableName.PACKAGES).delete { filter { eq("id", id) } }
        return true
    }
}