package org.bytebloom.data.remote.mapper

open class EntityMapper<Dto, Domain>(
    private val toDomain: (Dto) -> Domain?
) {
    fun map(dto: Dto): Domain? = toDomain(dto)
    fun mapList(dtos: List<Dto>): List<Domain> = dtos.mapNotNull(toDomain)
}