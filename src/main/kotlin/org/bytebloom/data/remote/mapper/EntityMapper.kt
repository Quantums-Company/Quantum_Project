package org.bytebloom.data.remote.mapper

import org.bytebloom.domain.model.exception.EntityValidationException
import org.bytebloom.util.Logger

open class EntityMapper<Dto, Domain>(
    private val toDomain: (Dto) -> Domain?
) {
    fun map(dto: Dto): Domain? =
        try {
            toDomain(dto)
        } catch (e: EntityValidationException) {
            Logger.warning("Skipped invalid record while mapping '$dto': ${e.message}")
            null
        }

    fun mapList(dtos: List<Dto>): List<Domain> = dtos.mapNotNull(::map)
}