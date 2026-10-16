package org.bytebloom.data.local.common

import org.bytebloom.domain.model.EntityType
import org.bytebloom.domain.service.IdGenerator
import java.util.UUID

class UuidIdGenerator : IdGenerator {
    override fun next(entityType: EntityType): String = "${entityType.idPrefix}${UUID.randomUUID()}"
}