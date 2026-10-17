package org.bytebloom.logiroute.data.id

import org.bytebloom.logiroute.domain.model.EntityType
import org.bytebloom.logiroute.domain.service.IdGenerator
import java.util.UUID

class UuidIdGenerator : IdGenerator {
    override fun next(entityType: EntityType): String = "${entityType.idPrefix}${UUID.randomUUID()}"
}