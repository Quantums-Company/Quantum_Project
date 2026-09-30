package org.bytebloom.data.remote.mapper

import org.bytebloom.domain.model.exception.EntityValidationException

open class EntityMapper<Dto, Domain>(
    private val toDomain: (Dto) -> Domain?
) {
    fun map(dto: Dto): Domain? = toDomain(dto)

    fun mapList(dtos: List<Dto>): MappingReport<Domain> {
        val succeeded = mutableListOf<Domain>()
        val skipped = mutableListOf<String>()

        for (dto in dtos) {
            try {
                toDomain(dto)?.let { succeeded.add(it) }
            } catch (e: EntityValidationException) {
                skipped.add("$dto -> ${e.message}")
            }
        }

        return MappingReport(succeeded, skipped)
    }
}