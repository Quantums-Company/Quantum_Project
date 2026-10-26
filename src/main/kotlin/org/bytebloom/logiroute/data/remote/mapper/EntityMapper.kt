package org.bytebloom.logiroute.data.remote.mapper

import org.bytebloom.logiroute.domain.model.exception.EntityValidationException

open class EntityMapper<Dto, Domain>(
    private val toDomain: (Dto) -> Domain?
) {
    fun map(dto: Dto): Domain? = toDomain(dto)

    fun mapList(dtos: List<Dto>): MappingReport<Domain> {
        val outcomes: List<Pair<Domain?, String?>> = dtos.map { dto ->
            try {
                toDomain(dto) to null
            } catch (exception: EntityValidationException) {
                null to "$dto -> ${exception.message}"
            }
        }

        return MappingReport(
            succeeded = outcomes.mapNotNull { (domain, _) -> domain },
            skipped = outcomes.mapNotNull { (_, error) -> error }
        )
    }
}