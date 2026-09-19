package org.bytebloom.data.local.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import org.bytebloom.data.remote.dto.packageDto.PackageRequestDto
import org.bytebloom.data.remote.dto.packageDto.PackageResponseDto
import org.bytebloom.data.remote.mapper.PackageDtoMapper
import org.bytebloom.domain.model.Package
import org.bytebloom.domain.repository.PackageRepository
import org.bytebloom.domain.repository.WarehouseRepository
import org.bytebloom.data.remote.client.SupabaseErrorTranslator

class SupabasePackageRepository(
    private val client: SupabaseClient,
    private val warehouseRepository: WarehouseRepository
) : PackageRepository {

    private suspend fun warehousesById() =
        warehouseRepository.getAll().associateBy { it.id }

    override suspend fun getAll(): List<Package> =
        SupabaseErrorTranslator.translate("getAll packages") {
            val dtos = client.from(TableName.PACKAGES)
                .select()
                .decodeList<PackageResponseDto>()
            PackageDtoMapper.toDomainList(dtos, warehousesById())
        }

    override suspend fun getById(id: String): Package? =
        SupabaseErrorTranslator.translate("getById package '$id'") {
            val dto = client.from(TableName.PACKAGES)
                .select { filter { eq("id", id) } }
                .decodeSingleOrNull<PackageResponseDto>()
            dto?.let { PackageDtoMapper.toDomain(it, warehousesById()) }
        }

    override suspend fun create(pkg: Package): Package =
        SupabaseErrorTranslator.translate("create package '${pkg.id}'") {
            val request = PackageRequestDto(
                id = pkg.id,
                weight = pkg.weight,
                originWarehouseId = pkg.originWarehouse.id,
                destinationWarehouseId = pkg.destinationWarehouse.id,
                priority = pkg.priority.name
            )
            client.from(TableName.PACKAGES).insert(request)
            pkg
        }

    override suspend fun update(pkg: Package): Package =
        SupabaseErrorTranslator.translate("update package '${pkg.id}'") {
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
            pkg
        }

    override suspend fun delete(id: String): Boolean =
        SupabaseErrorTranslator.translate("delete package '$id'") {
            client.from(TableName.PACKAGES).delete { filter { eq("id", id) } }
            true
        }
}